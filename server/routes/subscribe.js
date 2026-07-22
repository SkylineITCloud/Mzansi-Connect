const { Router } = require("express");
const { subscribeRules, handleValidationErrors } = require("../middleware/validate");
const db = require("../db");

const router = Router();

router.post("/", subscribeRules, handleValidationErrors, (req, res) => {
  const { email } = req.body;
  const entry = db.subscribe(email);
  if (!entry) {
    return res.status(200).json({ message: "Already subscribed." });
  }
  res.status(201).json({ id: entry.id, message: "Subscribed successfully." });
});

router.get("/", (_req, res) => {
  res.json(db.getSubscribers());
});

module.exports = router;
