# Mzansi Connect

**Smart technology for every South African.**

Mzansi Connect is a South African technology initiative focused on making practical, affordable Internet of Things (IoT) solutions accessible to communities, small businesses, schools, landlords, and entrepreneurs. The project also includes an Android price-comparison prototype built for South African shoppers.

## Repository contents

| Location | Purpose |
| --- | --- |
| [`index.html`](index.html) | Static Mzansi Connect website / project presentation. |
| [`Logo.png`](Logo.png) | Mzansi Connect brand asset used by the website. |
| [`docs/`](docs/) | Business, market, operational, financial, and technical planning documents. |
| [`price-compare-app/`](price-compare-app/) | Android price-comparison prototype and its Node.js API. |

## Mzansi Connect focus areas

- Affordable IoT products for security, water, electricity, rentals, transport, and agriculture.
- Connected-community concepts, including smart street poles, mesh internet, and smart business hubs.
- Local skills development, technician opportunities, and digital inclusion.

## Planned IoT products

| Product | Purpose |
| --- | --- |
| Smart Spaza Shop Security | Motion, door, panic, and SMS alerts for small businesses. |
| Smart Water Tank Monitor | Water-level, leak, and overflow monitoring. |
| Kasi Smart Electricity Monitor | Usage, prepaid-meter, and appliance-consumption tracking. |
| Smart Rental Room Manager | Utility and occupancy monitoring for landlords. |
| Taxi Fleet Tracker | Vehicle location, driver-behaviour, and fuel monitoring. |

## Price Compare prototype

The prototype lets users search products, compare prices between stores, save products, and use authenticated accounts. It consists of:

- an Android application written in Kotlin; and
- a Node.js / Express API with a SQLite database.

See the [prototype README](price-compare-app/README.md) for setup instructions and the [documentation index](docs/README.md) for project plans.

## Quick start

### View the website

Open [`index.html`](index.html) in a browser.

### Run the prototype backend

```powershell
Set-Location price-compare-app/backend
npm install
npm run seed
npm start
```

The API listens on `http://localhost:3000` by default. Check its status at `GET /api/health`.

### Run the Android app

Open [`price-compare-app/android`](price-compare-app/android) in Android Studio, then build and run it on an emulator or Android device. The default API URL is configured for the Android emulator.

## Documentation

The planning documents are listed in [`docs/README.md`](docs/README.md). They cover the business plan, feasibility, market research, marketing, operations, finance, SWOT analysis, and technical specification.

## Contact

Founded by Skyline IT & Cloud · Durban, South Africa  
projectpstg@gmail.com
