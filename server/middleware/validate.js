const { body, validationResult } = require("express-validator");

const contactRules = [
  body("name").trim().notEmpty().withMessage("Name is required")
    .isLength({ max: 100 }).withMessage("Name too long"),
  body("email").trim().isEmail().withMessage("Valid email required")
    .normalizeEmail(),
  body("message").trim().notEmpty().withMessage("Message is required")
    .isLength({ max: 2000 }).withMessage("Message too long"),
  body("company").optional().trim().isLength({ max: 200 }),
];

const subscribeRules = [
  body("email").trim().isEmail().withMessage("Valid email required")
    .normalizeEmail(),
];

function handleValidationErrors(req, res, next) {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ errors: errors.array() });
  }
  next();
}

module.exports = { contactRules, subscribeRules, handleValidationErrors };
