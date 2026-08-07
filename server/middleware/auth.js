function requireApiKey(req, res, next) {
  const key = req.headers["x-api-key"];
  const expected = process.env.ADMIN_API_KEY;

  if (!expected || !key || key !== expected) {
    return res.status(401).json({ error: "Unauthorized — invalid or missing X-API-Key" });
  }
  next();
}

module.exports = { requireApiKey };
