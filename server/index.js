require("dotenv").config();
const express = require("express");
const cors = require("cors");
const helmet = require("helmet");
const morgan = require("morgan");
const rateLimit = require("express-rate-limit");
const path = require("path");

const contactRoutes = require("./routes/contact");
const subscribeRoutes = require("./routes/subscribe");
const { requireApiKey } = require("./middleware/auth");

const app = express();
const PORT = process.env.PORT || 3000;

// Security headers
app.use(helmet({ crossOriginResourcePolicy: { policy: "cross-origin" } }));

// CORS lockdown
const allowedOrigins = process.env.ALLOWED_ORIGINS
  ? process.env.ALLOWED_ORIGINS.split(",")
  : ["http://localhost:3000"];

app.use(cors({
  origin: (origin, cb) => {
    if (!origin || allowedOrigins.includes(origin)) return cb(null, true);
    cb(new Error("Not allowed by CORS"));
  },
  methods: ["GET", "POST", "PUT", "DELETE"],
  allowedHeaders: ["Content-Type", "X-API-Key"],
}));

// Global rate limit
app.use(rateLimit({
  windowMs: 15 * 60 * 1000,
  max: parseInt(process.env.RATE_LIMIT_MAX) || 200,
  standardHeaders: true,
  legacyHeaders: false,
  message: { error: "Too many requests — try again later" },
}));

// Contact form tight rate limit
const contactLimiter = rateLimit({
  windowMs: 60 * 60 * 1000,
  max: parseInt(process.env.CONTACT_RATE_LIMIT_MAX) || 5,
  standardHeaders: true,
  legacyHeaders: false,
  message: { error: "Too many submissions — try again later" },
});

app.use(express.json({ limit: "10kb" }));
app.use(morgan("dev"));

// Static site
app.use(express.static(path.join(__dirname, "..")));

// API routes
app.use("/api/contact", contactLimiter, contactRoutes);
app.use("/api/subscribe", contactLimiter, subscribeRoutes);

// API key protected admin routes
app.get("/api/admin/stats", requireApiKey, (req, res) => {
  const db = require("./db");
  res.json({ contacts: db.contactCount(), subscribers: db.subscriberCount() });
});

app.get("/api/admin/contacts", requireApiKey, (req, res) => {
  const db = require("./db");
  res.json(db.getContacts());
});

app.get("/api/admin/subscribers", requireApiKey, (req, res) => {
  const db = require("./db");
  res.json(db.getSubscribers());
});

// Health
app.get("/api/health", (_req, res) => {
  res.json({ status: "ok", uptime: process.uptime() });
});

// SPA fallback
app.get("*", (req, res) => {
  if (req.path.startsWith("/api")) {
    return res.status(404).json({ error: "Not found" });
  }
  res.sendFile(path.join(__dirname, "..", "index.html"));
});

// Global error handler
app.use((err, _req, res, _next) => {
  console.error("Unhandled error:", err);
  if (err.message === "Not allowed by CORS") {
    return res.status(403).json({ error: "Origin not allowed" });
  }
  res.status(500).json({ error: "Internal server error" });
});

app.listen(PORT, "0.0.0.0", () => {
  console.log(`  Mzansi Connect API running on http://0.0.0.0:${PORT}`);
});
