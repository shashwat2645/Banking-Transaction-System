# Banking Transaction System

A production-oriented Banking Transaction System built using Java, Spring Boot, and PostgreSQL.

The project demonstrates core banking operations such as account creation, deposits, withdrawals, fund transfers, and transaction history while following a layered architecture (Controller → Service → Repository). The project is being developed incrementally with production-grade features such as idempotency, concurrency handling, database locking, logging, and performance testing planned in upcoming iterations.

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Lombok

---

## Features

### Implemented

- Create bank accounts
- View account details
- Deposit money
- Withdraw money
- Transfer funds between accounts
- Automatic transaction recording
- Transaction history API
- Global exception handling
- Layered architecture

---

## Project Structure

```text
src
├── controller
├── service
│   └── impl
├── repository
├── entity
├── dto
│   ├── request
│   └── response
├── exception
├── enums
└── config
```

---

## REST APIs

### Account APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/accounts` | Create a new account |
| GET | `/accounts/{accountNumber}` | Get account details |
| POST | `/accounts/deposit` | Deposit money |
| POST | `/accounts/withdraw` | Withdraw money |
| POST | `/accounts/transfer` | Transfer money |

### Transaction APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/transactions` | Get all transactions |

---

## Architecture

```text
                Client
                   │
                   ▼
          AccountController
                   │
                   ▼
          AccountServiceImpl
                   │
        ┌──────────┴──────────┐
        │                     │
        ▼                     ▼
 Update Account        Record Transaction
        │                     │
        └──────────┬──────────┘
                   ▼
             PostgreSQL Database
```

---

## Project Status

### Completed

- Account Management
- Deposit API
- Withdraw API
- Transfer API
- Transaction Recording
- Transaction History API
- Exception Handling

### Planned

- Idempotency
- Duplicate Transaction Prevention
- Concurrency Handling
- Database Locking
- Structured Logging
- Unit & Integration Testing
- Performance Testing
- SQL Optimization
- Documentation & Architecture Diagrams

---

## Development Progress

- ✅ Day 1 – Project Setup, Database Design, Entities, Repositories
- ✅ Day 2 – DTOs, CRUD APIs, Deposit & Withdrawal APIs
- ✅ Day 3 – Transfer API, Business Validations, Exception Handling
- ✅ Day 4 – Transaction Module & Transaction History
- ⏳ Day 5 – Idempotency & Duplicate Transaction Handling