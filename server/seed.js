const db = require("./db");

db.insertContact({ name: "Thandi Mokoena", email: "thandi@example.com", message: "Interested in the Smart Spaza Shop Security system for my tuck shop in Umlazi.", company: "Mokoena Traders" });
db.insertContact({ name: "Bongani Zulu", email: "bongani@example.com", message: "Looking for a water tank monitoring solution for our farm in KwaZulu-Natal.", company: "Zulu Farms" });
db.insertContact({ name: "Lerato Molefe", email: "lerato@example.com", message: "Would like to partner as an installation technician in Soweto." });

db.subscribe("info@skylineit.site");
db.subscribe("hello@mzansiconnect.co.za");

console.log("  Demo data seeded.");
