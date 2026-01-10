# 🖥️ Product Review Backend

A RESTful API built with **Spring Boot** for managing products and reviews.

## 🎯 Features

- **Product Management** – List products with pagination and sorting
- **Product Details** – View individual product information
- **Review System** – Submit and retrieve product reviews with ratings (1–5)
- **Aggregated Data** – Average ratings and review counts per product

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 4.0**
- **Spring Data JPA** (Hibernate ORM)
- **H2 Database** (in-memory for development)
- **Bean Validation** (input validation)
- **Lombok** (boilerplate reduction)

## 📁 Project Structure

```
backend/src/main/java/com/alper/product_review_backend/
├── ProductReviewBackendApplication.java  # Main entry point
├── config/
│   ├── DataInitializer.java              # Seeds sample data
│   └── GlobalExceptionHandler.java       # Error handling
├── controller/
│   ├── ProductController.java            # Product endpoints
│   └── ReviewController.java             # Review endpoints
├── service/
│   ├── ProductService.java               # Product business logic
│   └── ReviewService.java                # Review business logic
├── repository/
│   ├── ProductRepository.java            # Product data access
│   └── ReviewRepository.java             # Review data access
├── domain/
│   ├── Product.java                      # Product entity
│   └── Review.java                       # Review entity
└── dto/
    ├── ProductSummaryDto.java            # Product list response
    ├── ProductDetailDto.java             # Product detail response
    ├── ReviewDto.java                    # Review response
    ├── CreateReviewRequest.java          # Review submission request
    └── ApiError.java                     # Error response
```

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | List products (paginated, sortable) |
| GET | `/api/products/{id}` | Get product details |
| GET | `/api/products/{id}/reviews` | Get reviews for a product |
| POST | `/api/products/{id}/reviews` | Submit a new review |

### Query Parameters (Product List)

- `page` – Page number (default: 0)
- `size` – Items per page (default: 10)
- `sort` – Sort field and direction (e.g., `price,asc`, `averageRating,desc`)

### Review Request Body

```json
{
  "rating": 5,
  "comment": "Great product!"
}
```

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+

### Run Locally

```bash
# Navigate to backend directory
cd backend

# Build and run
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`

### H2 Console

Access the database console at `http://localhost:8080/h2-console`

- **JDBC URL:** `jdbc:h2:mem:productdb`
- **Username:** `sa`
- **Password:** (empty)

## 🧪 Testing

```bash
# Run all tests
./mvnw test
```

Tests include:
- Unit tests for service layer
- Integration tests for controllers
- Repository tests

## 📌 Notes

- **Authentication** is not included (out of scope)
- Sample data is auto-seeded on startup via `DataInitializer`
- Categories: Phones, Laptops, Tablets, Audio, Wearables, Accessories
