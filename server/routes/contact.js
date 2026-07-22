const { Router } = require("express");
const { contactRules, handleValidationErrors } = require("../middleware/validate");
const db = require("../db");

const router = Router();

router.post("/", contactRules, handleValidationErrors, (req, res) => {
  const { name, email, message, company } = req.body;
  const entry = db.insertContact({ name, email, message, company });
  res.status(201).json({ id: entry.id, message: "Message received. We'll be in touch." });
});

router.get("/", (_req, res) => {
  res.json(db.getContacts());
});

module.exports = router;
