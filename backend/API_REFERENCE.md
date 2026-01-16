# Product Review API Reference

Base URL: `http://localhost:8080`

---

## 🔓 Authentication

### Register User
```
POST /api/auth/register
```

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Validation:**
- `username`: Required, 3-50 characters
- `email`: Required, valid email format
- `password`: Required, 6-100 characters

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "username": "john_doe",
  "role": "USER"
}
```

**Errors:**
- `400 Bad Request` - Validation failed
- `409 Conflict` - Username or email already exists

---

### Login
```
POST /api/auth/login
```

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "username": "john_doe",
  "role": "USER"
}
```

**Errors:**
- `401 Unauthorized` - Invalid credentials

---

## 📦 Products (Public)

### Get All Products (Paginated)
```
GET /api/products
```

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 20 | Items per page |
| `sort` | string | - | Sort field and direction |

**Sort Examples:**
- `?sort=price,asc` - Sort by price ascending
- `?sort=price,desc` - Sort by price descending
- `?sort=name,asc` - Sort by name A-Z
- `?sort=averageRating,desc` - Sort by rating (highest first)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Wireless Headphones",
      "category": "Electronics",
      "price": 79.99,
      "averageRating": 4.5,
      "reviewCount": 12
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "direction": "ASC",
      "property": "price"
    }
  },
  "totalElements": 50,
  "totalPages": 3,
  "first": true,
  "last": false,
  "numberOfElements": 20
}
```

---

### Get Product by ID
```
GET /api/products/{id}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Wireless Headphones",
  "description": "High-quality wireless headphones with noise cancellation",
  "category": "Electronics",
  "price": 79.99,
  "averageRating": 4.5,
  "reviewCount": 12
}
```

**Errors:**
- `404 Not Found` - Product not found

---

## ⭐ Reviews

### Get Reviews for Product (Public)
```
GET /api/products/{productId}/reviews
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "rating": 5,
    "comment": "Excellent product! Highly recommended.",
    "username": "john_doe",
    "createdAt": "2026-01-15T10:30:00Z"
  },
  {
    "id": 2,
    "rating": 4,
    "comment": "Good quality, fast shipping.",
    "username": "jane_smith",
    "createdAt": "2026-01-14T15:45:00Z"
  }
]
```

---

### Add Review (Requires Auth)
```
POST /api/products/{productId}/reviews
```

**Headers:**
```
Authorization: Bearer <your_jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "rating": 5,
  "comment": "Amazing product!"
}
```

**Validation:**
- `rating`: Required, 1-5
- `comment`: Optional, max 2000 characters

**Response (200 OK):**
```json
{
  "id": 3,
  "rating": 5,
  "comment": "Amazing product!",
  "username": "john_doe",
  "createdAt": "2026-01-17T12:00:00Z"
}
```

**Errors:**
- `400 Bad Request` - Validation failed
- `401 Unauthorized` - Missing or invalid token
- `404 Not Found` - Product not found

---

## 🔐 Admin Endpoints (Requires ADMIN Role)

All admin endpoints require:
```
Authorization: Bearer <admin_jwt_token>
```

### Create Admin User
```
POST /api/admin/users
```

**Request Body:**
```json
{
  "username": "new_admin",
  "email": "admin@example.com",
  "password": "admin123"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "username": "new_admin",
  "role": "ADMIN"
}
```

**Errors:**
- `403 Forbidden` - Not an admin user

---

### Get All Users
```
GET /api/admin/users
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN",
    "createdAt": "2026-01-01T00:00:00Z",
    "enabled": true
  },
  {
    "id": 2,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "USER",
    "createdAt": "2026-01-15T10:00:00Z",
    "enabled": true
  }
]
```

---

### Create Product
```
POST /api/admin/products
```

**Request Body:**
```json
{
  "name": "New Product",
  "description": "A fantastic new product",
  "category": "Electronics",
  "price": 99.99
}
```

**Validation:**
- `name`: Required, max 255 characters
- `description`: Optional, max 1000 characters
- `category`: Required
- `price`: Required, must be > 0

**Response (201 Created):**
```json
{
  "id": 51,
  "name": "New Product",
  "description": "A fantastic new product",
  "category": "Electronics",
  "price": 99.99,
  "averageRating": 0.0,
  "reviewCount": 0
}
```

---

## 🚨 Error Responses

All errors follow this format:

```json
{
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Product not found: 999",
  "timestamp": "2026-01-17T12:00:00Z",
  "path": "/api/products/999"
}
```

### Common HTTP Status Codes

| Code | Meaning |
|------|---------|
| `200` | Success |
| `201` | Created |
| `400` | Bad Request (validation error) |
| `401` | Unauthorized (missing/invalid token) |
| `403` | Forbidden (insufficient permissions) |
| `404` | Not Found |
| `409` | Conflict (duplicate username/email) |
| `500` | Internal Server Error |

---

## 🔑 Using JWT Tokens

After login/register, store the token and include it in subsequent requests:

```javascript
// Store after login
localStorage.setItem('token', response.token);

// Use in requests
fetch('/api/products/1/reviews', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  },
  body: JSON.stringify({ rating: 5, comment: 'Great!' })
});
```

---

## 📝 Quick Reference

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/auth/register` | POST | ❌ | Register new user |
| `/api/auth/login` | POST | ❌ | Login |
| `/api/products` | GET | ❌ | List products (paginated) |
| `/api/products/{id}` | GET | ❌ | Get product details |
| `/api/products/{id}/reviews` | GET | ❌ | Get product reviews |
| `/api/products/{id}/reviews` | POST | ✅ USER | Add review |
| `/api/admin/users` | GET | ✅ ADMIN | List all users |
| `/api/admin/users` | POST | ✅ ADMIN | Create admin user |
| `/api/admin/products` | POST | ✅ ADMIN | Create product |

---

## 🧪 Test Credentials

The app initializes with a default admin user:
```
Username: admin
Password: admin123
```
