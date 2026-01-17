# 📱 Product Review Application

**Backend:** Spring Boot 4.0 with Spring Security & JWT  
**Mobile App:** Native Android (Kotlin + Jetpack Compose)  
**Authentication:** JWT-based with Role-Based Access Control (RBAC)

---

## 📌 Project Overview

The **Product Review Application** is a full-stack system that allows users to browse products, submit reviews, rate products, and view aggregated feedback through a modern mobile interface.  
The project implements **secure REST API design, JWT authentication, AOP-based validation, and a polished native Android UI**.

---

## 📦 Deliverables

### 🏛️ System Architecture
> **[View Architecture Diagram on Excalidraw](https://excalidraw.com/#json=3kbNwGWVb0CEIPF7W8rp_,x3a-wCwH3MBPpH7MJc3OYQ)**

### 🎬 Frontend Code Walkthrough
> **[Watch Frontend Walkthrough Video](https://drive.google.com/file/d/11QYtG6797z346qXVIFTwK0EzraecO3bh/view?usp=sharing)**

### 🎬 Backend Code Walkthrough
> **[Watch Backend Walkthrough Video](https://drive.google.com/file/d/1PGf-WP-ijXNIMz5wugFE3zNy_t3gI_gW/view?usp=sharing)**

### 🎬 Application Demo
> **[Watch Application Demo Video](https://drive.google.com/file/d/1WorkYyeroHIcakn5l_s9h8dpvFKNfAVp/view?usp=sharing)**

### 📥 Build Artifacts
> **APK Download:** [Product Review APK](https://drive.google.com/file/d/1uPJxqjDyOZ_REuOUJ7JDb2AGlbGRc3GI/view?usp=sharing)

### 🌐 Web Access
> *Not implemented* - The application is mobile-first (native Android) and does not include a web interface.

---

## 🎯 Objectives

- Build a secure, scalable RESTful backend using **Spring Boot**
- Implement **JWT-based authentication** with role-based access control
- Apply **Aspect-Oriented Programming (AOP)** for cross-cutting concerns
- Develop a native Android application using **Kotlin & Jetpack Compose**
- Write comprehensive **integration tests** for API endpoints

---

## 🧩 Core Features

### 🔐 Security & Authentication
- **Spring Security 6** integration
- **JWT-based authentication:**
  - User registration with password hashing (BCrypt)
  - Secure login with token issuance
  - Stateless session management
- **Role-Based Access Control (RBAC):**
  - `USER` role: View products, submit reviews
  - `ADMIN` role: Full access including user management, product creation
- **Protected endpoints** with method-level security (`@PreAuthorize`)
- Custom JWT filters: `JwtAuthenticationFilter`, `JwtAuthenticationEntryPoint`, `JwtAccessDeniedHandler`

### 🎯 Validation with AOP
- **Centralized validation** using Aspect-Oriented Programming
- **Custom annotations:**
  - `@ValidateRequest` - Triggers validation aspect
  - `@ValidRange` - Validates numeric ranges (e.g., rating 1-5)
  - `@ValidLength` - Validates string lengths
  - `@EntityExists` - Validates entity existence in database
- **Additional aspects:**
  - `LoggingAspect` - Automatic method logging with timing
  - `PerformanceAspect` - Performance monitoring
- **Benefits:**
  - Clean controller/service layers
  - Consistent error handling
  - Reusable validation logic

### 🛒 Product Management
- Retrieve paginated list of products
- View product details (name, description, category, price, average rating)
- Admin-only product creation
- Sorting by price, rating, name
- Category-based filtering
- Search functionality

### ⭐ Review & Rating System
- Submit text-based reviews with ratings (1-5 scale)
- View paginated review history per product
- Automatic average rating calculation
- Review count aggregation

### 📱 Mobile Experience (Native Android)
- **Native Android** with Kotlin & Jetpack Compose
- **MVVM Architecture** with clean separation of concerns
- **Key screens:**
  - Login / Register (with toggle between modes)
  - Products Grid (with search, sort, category filter)
  - Product Details (with reviews)
- Material 3 design system
- Secure token storage
- Graceful error and loading state handling

---

## 🏗️ Architecture

### Backend (Spring Boot 4.0)
```
src/main/java/com/alper/product_review_backend/
├── aop/
│   ├── annotation/    # Custom validation annotations
│   └── aspect/        # ValidationAspect, LoggingAspect, PerformanceAspect
├── config/            # SecurityConfig, CORS config
├── controller/        # REST controllers (Auth, Product, Review, Admin)
├── domain/            # JPA entities (User, Product, Review, Role)
├── dto/               # Request/Response DTOs
├── exception/         # Custom exceptions & global handler
├── repository/        # Spring Data JPA repositories
├── security/          # JWT components (TokenProvider, Filters, Handlers)
└── service/           # Business logic services
```

**Key Technologies:**
- Spring Boot 4.0.1
- Spring Security 6 with JWT (jjwt 0.12.6)
- Spring Data JPA / Hibernate
- H2 Database (development)
- Testcontainers (integration testing)
- Lombok, Validation API

### Mobile App (Native Android / Kotlin)
```
android-kotlin/app/src/main/java/com/alper/productreview/
├── data/
│   ├── api/           # ApiClient, ApiService (Retrofit)
│   ├── auth/          # TokenStore (JWT persistence)
│   ├── model/         # DTOs (ProductDto, ReviewDto, etc.)
│   └── repository/    # AuthRepository, ProductRepository
├── domain/
│   ├── model/         # Domain models
│   └── repository/    # Repository interfaces
├── ui/
│   ├── screens/
│   │   ├── auth/      # LoginScreen, LoginViewModel
│   │   ├── products/  # ProductsScreen, ProductsViewModel
│   │   └── detail/    # ProductDetailScreen, ProductDetailViewModel
│   ├── components/    # Reusable UI components
│   ├── theme/         # Material 3 theming
│   └── util/          # UI utilities
├── MainActivity.kt    # Entry point with Navigation setup
└── ProductReviewApp.kt
```

**Key Technologies:**
- Kotlin
- Jetpack Compose (UI toolkit)
- Navigation Compose
- Retrofit + OkHttp (networking)
- Moshi (JSON parsing)
- Hilt (dependency injection ready)
- Coroutines + Flow (async)
- Material 3 Design

---

## 🧪 Testing & Quality

### Integration Tests
- **Controller integration tests** using `@SpringBootTest` and `MockMvc`
- Tests cover:
  - `AuthControllerIntegrationTest` - Registration, login flows
  - `ProductControllerIntegrationTest` - Product CRUD, pagination, sorting
  - `ReviewControllerIntegrationTest` - Review submission, retrieval
  - `AdminControllerIntegrationTest` - Admin-only operations
- **Testcontainers** configured for database testing (PostgreSQL)
- Environment-independent, reproducible test execution

### Code Quality
- Consistent API response formats
- Comprehensive input validation
- Proper exception handling with meaningful error messages
- Clean separation of concerns (MVVM on Android, layered architecture on backend)

---

## 🔮 Future Improvements

- **Web Application:** Add a React/Next.js web frontend for browser access
- **OAuth2 Integration:** Support social login (Google, GitHub)
- **Image Upload:** Allow product images and user avatars
- **Push Notifications:** Firebase Cloud Messaging for review notifications
- **Search Functionality:** Full-text search across products and reviews
- **Review Moderation:** Admin tools for flagging/removing inappropriate reviews
- **Analytics Dashboard:** Admin dashboard with charts for product performance
- **Caching Layer:** Redis caching for improved read performance
- **Rate Limiting:** Protect API from abuse with request throttling
- **CI/CD Pipeline:** Automated testing and deployment with GitHub Actions
- **iOS Build:** Native iOS app using Swift/SwiftUI
- **Offline Support:** Room database for offline caching on Android

---

## 🚀 Getting Started

### Backend
```bash
cd backend
./mvnw spring-boot:run
```
API will be available at `http://localhost:8080`

### Android App
1. Open `android-kotlin` folder in Android Studio
2. Sync Gradle dependencies
3. Run on emulator or physical device

---

## 📁 Project Structure Note

> **Note:** The `mobile/` folder contains a deprecated React Native (Expo) implementation that is no longer maintained. The current mobile app is in `android-kotlin/` using native Kotlin with Jetpack Compose.

---

## 📄 License

This project is for educational/demonstration purposes.
