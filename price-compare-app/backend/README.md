# Mzansi Price Compare API

The API provides authentication, product browsing, product search, saved products, and trending-search data for the Mzansi Price Compare Android prototype.

## Requirements

- Node.js 18 or newer is recommended.
- npm.

## Setup

```powershell
npm install
npm run seed
npm start
```

The server listens on port `3000` unless the `PORT` environment variable is set. The seed command creates and populates the local SQLite demo database.

## Environment variables

| Variable | Purpose |
| --- | --- |
| `PORT` | Optional API port; defaults to `3000`. |
| `JWT_SECRET` | Secret used to sign authentication tokens. Set a strong, unique value outside local development. |

## Routes

| Method | Route | Authentication | Description |
| --- | --- | --- | --- |
| `GET` | `/api/health` | No | Health check. |
| `POST` | `/api/auth/register` | No | Register a user. |
| `POST` | `/api/auth/login` | No | Sign in and receive a token. |
| `GET` | `/api/auth/me` | Bearer token | Return the current user. |
| `GET` | `/api/products` | No | List products. |
| `GET` | `/api/products/categories` | No | List product categories. |
| `GET` | `/api/products/:id` | No | Return a product with store listings. |
| `POST` | `/api/products/:id/save` | Bearer token | Save a product. |
| `DELETE` | `/api/products/:id/save` | Bearer token | Remove a saved product. |
| `GET` | `/api/products/saved/list` | Bearer token | List saved products. |
| `GET` | `/api/search` | No | Search by product name or brand; `q` needs at least two characters. |
| `GET` | `/api/search/trending` | No | List the most frequent searches from the previous seven days. |

## Development notes

The API uses a local SQLite database intended for prototype data. Use HTTPS, managed secrets, production validation, rate limiting, and a production database before deploying publicly.

Return to the [prototype README](../README.md) for full setup instructions.
