# Retail Billing & Inventory System

Backend REST API built using Java 21, Spring Boot, MySQL, JWT Authentication, and Razorpay Integration.

---

## Overview

A lightweight, single-store retail billing and inventory management backend designed for Point-of-Sale (POS) operations.

Features include:

- Staff Management
- Product & Category Management
- Inventory Tracking
- Customer Management
- Order Processing
- Payment Handling
- Sales Reporting
- JWT Authentication & Authorization

---

## Tech Stack

| Layer | Technology | Purpose |
|---------|------------|----------|
| Language | Java 21 | LTS Release |
| Framework | Spring Boot 3.3.x | Application Framework |
| Security | Spring Security 6 + JWT (HS256) | Authentication & Authorization |
| ORM | Spring Data JPA + Hibernate 6 | Database Access |
| Database | MySQL 8.x | Persistent Storage |
| Payments | Razorpay Java SDK | QR Code Payments |
| Build Tool | Maven | Dependency Management |
| API Docs | SpringDoc OpenAPI | Swagger Documentation |
| Boilerplate Reduction | Lombok | Reduce Boilerplate Code |

---

## Prerequisites

- Java 21
- Maven 3.8+
- MySQL 8.x
- Razorpay Account (Test Mode Supported)

---

# Getting Started

## 1. Clone Repository

```bash
git clone https://github.com/your-username/retail-billing.git

cd retail-billing
```

---

## 2. Create Database

```sql
CREATE DATABASE retail_billing;
```

---

## 3. Configure Environment Variables

The application loads sensitive configuration from environment variables.

| Variable | Description | Example |
|-----------|-------------|----------|
| DB_HOST | MySQL Host | localhost |
| DB_PORT | MySQL Port | 3306 |
| DB_NAME | Database Name | retail_billing |
| DB_USER | MySQL Username | root |
| DB_PASSWORD | MySQL Password | yourpassword |
| JWT_SECRET | JWT Signing Key (min 32 chars) | your-secret-key |
| JWT_EXPIRATION_MS | Token Expiry (ms) | 3600000 |
| RAZORPAY_KEY_ID | Razorpay Key ID | rzp_test_xxxxx |
| RAZORPAY_KEY_SECRET | Razorpay Secret | xxxxxxxxxx |
| LOW_STOCK_THRESHOLD | Low Stock Alert Threshold | 10 |
| ORDER_NUMBER_PREFIX | Order Number Prefix | ORD |

---

## 4. Run Application

```bash
mvn spring-boot:run
```

Application starts at:

```text
http://localhost:8080
```

---

# API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

All APIs are grouped and documented using OpenAPI.

---

# First-Time Setup

The application allows only **one Owner account**.

Register the owner once:

```http
POST /api/v1/auth/register
```

Request Body:

```json
{
  "name": "Store Owner",
  "email": "owner@store.com",
  "password": "yourpassword"
}
```

Any subsequent registration attempt returns:

```http
409 Conflict
```

Staff accounts can then be created by the Owner.

---

# Modules

| Module | Base URL | Access |
|----------|-----------|---------|
| Auth | /api/v1/auth | Public / Authenticated |
| Users | /api/v1/users | Owner Only |
| Categories | /api/v1/categories | Owner + Staff |
| Products | /api/v1/products | Owner + Staff |
| Inventory | /api/v1/inventory | Owner Only |
| Customers | /api/v1/customers | Owner + Staff |
| Orders | /api/v1/orders | Owner + Staff |
| Payments | /api/v1/payments | Owner + Staff |
| Reports | /api/v1/reports | Owner Only |

---

# Billing Workflow

```text
Register Owner
        ↓
Login
        ↓
Create Staff
        ↓
Create Category
        ↓
Create Product
        ↓
Create Order (DRAFT)
        ↓
Add Items
        ↓
Confirm Order
        ↓
Record Payment
        ↓
PAID
```

---

# Key Design Decisions

### Single Owner Model

- Only one Owner account allowed.
- Second registration returns `409 Conflict`.

### Draft Order Pattern

- Stock is not deducted while an order is in DRAFT state.
- Stock is deducted only after confirmation.

### Price Snapshotting

Order items store:

- Product Name
- Unit Price

This preserves historical billing data.

### Soft Deletes

Products and categories are never physically deleted.

Benefits:

- Maintains historical order integrity.
- Prevents orphaned records.

### Pessimistic Locking

Order confirmation uses:

```java
PESSIMISTIC_WRITE
```

to prevent overselling during concurrent transactions.

### Two-Pass Stock Validation

1. Validate stock for all items.
2. Deduct stock only if all validations pass.

### Customer Identification

Customers are identified solely by:

```text
Phone Number
```

Email is optional and not stored.

### Monetary Calculations

All monetary values use:

```java
BigDecimal
```

Never:

```java
float
double
```

### Secure Payment Verification

Razorpay payments are verified using:

```text
HMAC-SHA256 Signature Validation
```

---

# Package Structure

```text
com.pranjal
│
├── config
│   └── SecurityConfig
│
├── auth
│   ├── AuthController
│   ├── AuthService
│   └── dto
│
├── user
│   ├── UserController
│   ├── UserEntity
│   ├── UserRepository
│   ├── CustomUserDetailService
│   ├── Role
│   ├── service
│   └── dto
│
├── category
│   ├── CategoryController
│   ├── CategoryService
│   ├── CategoryRepository
│   └── dto
│
├── product
│   ├── ProductController
│   ├── ProductService
│   ├── ProductRepository
│   └── dto
│
├── inventory
│   ├── InventoryController
│   ├── InventoryService
│   ├── InventoryRepository
│   ├── InventoryLog
│   └── dto
│
├── customer
│   ├── CustomerController
│   ├── CustomerService
│   ├── CustomerRepository
│   └── dto
│
├── order
│   ├── OrderController
│   ├── OrderService
│   ├── repositories
│   ├── entities
│   └── dto
│
├── payment
│   ├── PaymentController
│   ├── PaymentService
│   ├── PaymentRepository
│   └── dto
│
├── report
│   ├── ReportController
│   ├── ReportService
│   └── dto
│
├── security
│   └── JwtService
│
└── common
    ├── ApiResponse
    ├── GlobalExceptionHandler
    └── RequestLoggingFilter
```

---

# Security

Authentication is implemented using:

- JWT Bearer Tokens
- Spring Security 6
- Stateless Sessions

Role-based access:

| Role | Permissions |
|--------|------------|
| OWNER | Full System Access |
| STAFF | Operational Access |

---

# Features

## Authentication

- Register Owner
- Login
- Current User Profile
- JWT Authentication

## Staff Management

- Create Staff
- Update Staff
- Activate / Deactivate Staff
- Change Password

## Product Management

- Create Product
- Update Product
- Soft Delete Product
- Low Stock Monitoring

## Inventory Management

- Manual Stock Adjustments
- Inventory Audit Logs
- Product Stock History

## Customer Management

- Create Customer
- Update Customer
- Search by Phone Number

## Order Management

- Draft Orders
- Add / Remove Items
- Confirm Orders
- Cancel Orders

## Payments

- Cash Payments
- Razorpay QR Payments
- Signature Verification

## Reports

- Sales Summary
- Daily Sales Report
- Top Selling Products

---

# License

**Private – Internal Store Tool**

Not intended for public distribution.