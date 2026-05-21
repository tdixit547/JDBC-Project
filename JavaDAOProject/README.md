# JavaDAOProject — Mini E-Commerce System (Refactored)

A console-based mini e-commerce application built with **Java**, **JDBC**, and **MySQL**, designed as a DBMS Lab project. The project follows the **DAO (Data Access Object)** pattern with a clean layered architecture separating concerns across Model, DAO, Service, and Controller layers.

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Database Setup](#-database-setup)
- [Configuration](#-configuration)
- [Build & Run](#-build--run)
- [Menu Structure](#-menu-structure)
- [Design Patterns](#-design-patterns)
- [Transaction Flows](#-transaction-flows)
- [Database Schema](#-database-schema)
- [Important Notes](#-important-notes)

---

## ✨ Features

| Feature                | Description                                                  |
|------------------------|--------------------------------------------------------------|
| Product Management     | Add, view, update stock, and delete products                 |
| User Management        | Register users with auto-created wallets                     |
| Wallet System          | View balance, add funds to user wallets                      |
| Shopping Cart          | Add/remove products, view cart, clear cart                   |
| Checkout               | Transactional checkout with wallet deduction & stock update  |
| Bill Generation        | Automatic bill & line-item creation on checkout              |
| Bill Viewing           | View bills by user, view detailed bill with line items       |
| Input Validation       | Robust console input handling with error recovery            |
| Exception Handling     | User-friendly error messages via custom exception hierarchy  |

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                    │
│  Main.java → MenuController → Sub-Controllers           │
│  (ConsoleHelper for I/O utilities)                      │
├─────────────────────────────────────────────────────────┤
│                     SERVICE LAYER                       │
│  ProductService, UserService, CartService,              │
│  CheckoutService, BillService                           │
│  (Business logic, validation, transaction management)   │
├─────────────────────────────────────────────────────────┤
│                       DAO LAYER                         │
│  Interfaces: ProductDAO, UserDAO, CartDAO, etc.         │
│  Implementations: ProductDAOImpl, UserDAOImpl, etc.     │
│  (Data access via JDBC PreparedStatements)              │
├─────────────────────────────────────────────────────────┤
│                    FACTORY LAYER                        │
│  DAOFactory (creates DAO instances)                     │
├─────────────────────────────────────────────────────────┤
│                   UTILITY LAYER                         │
│  DBConnection (Singleton), TransactionManager           │
├─────────────────────────────────────────────────────────┤
│                      DATABASE                           │
│  MySQL (products, users, wallets, cart, bills, etc.)    │
└─────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
JavaDAOProject/
├── lib/
│   └── mysql-connector-j-9.6.0.jar
├── src/
│   ├── Main.java                          # Application entry point (bootstrapper)
│   ├── controller/
│   │   ├── ConsoleHelper.java             # Shared I/O utility (input reading, formatting)
│   │   ├── MenuController.java            # Main menu router & checkout handler
│   │   ├── ProductController.java         # Product management sub-menu
│   │   ├── UserController.java            # User & wallet management sub-menu
│   │   ├── CartController.java            # Cart operations sub-menu
│   │   └── BillController.java            # Bill viewing sub-menu
│   ├── model/
│   │   ├── Product.java                   # Product entity (id, name, price, count)
│   │   ├── User.java                      # User entity (id, name, email)
│   │   ├── Cart.java                      # Cart entity (id, userId, productId, qty)
│   │   ├── Wallet.java                    # Wallet entity (id, userId, balance)
│   │   ├── Bill.java                      # Bill entity (id, userId, total, date, status)
│   │   └── BillItem.java                  # BillItem entity (id, billId, productId, qty, price)
│   ├── dao/
│   │   ├── ProductDAO.java                # Product DAO interface
│   │   ├── UserDAO.java                   # User DAO interface
│   │   ├── CartDAO.java                   # Cart DAO interface
│   │   ├── WalletDAO.java                 # Wallet DAO interface
│   │   ├── BillDAO.java                   # Bill DAO interface
│   │   ├── BillItemDAO.java              # BillItem DAO interface
│   │   └── impl/
│   │       ├── ProductDAOImpl.java        # Product DAO JDBC implementation
│   │       ├── UserDAOImpl.java           # User DAO JDBC implementation
│   │       ├── CartDAOImpl.java           # Cart DAO JDBC implementation
│   │       ├── WalletDAOImpl.java         # Wallet DAO JDBC implementation
│   │       ├── BillDAOImpl.java           # Bill DAO JDBC implementation
│   │       └── BillItemDAOImpl.java       # BillItem DAO JDBC implementation
│   ├── factory/
│   │   └── DAOFactory.java               # Factory for creating DAO instances
│   ├── service/
│   │   ├── ProductService.java            # Product business logic
│   │   ├── UserService.java               # User & wallet business logic
│   │   ├── CartService.java               # Cart business logic
│   │   ├── CheckoutService.java           # Checkout transaction logic
│   │   └── BillService.java              # Bill retrieval logic
│   ├── exceptions/
│   │   ├── ShopException.java             # Base custom exception
│   │   ├── InvalidInputException.java     # Invalid input errors
│   │   ├── ProductNotFoundException.java  # Product not found
│   │   ├── UserNotFoundException.java     # User not found
│   │   └── ...                            # Other domain exceptions
│   └── util/
│       ├── DBConnection.java              # Singleton database connection manager
│       └── TransactionManager.java        # Transaction helper (begin/commit/rollback)
├── db.properties                          # Database connection configuration
├── bin/                                   # Compiled .class files (generated)
└── README.md                              # This file
```

---

## 📦 Prerequisites

- **Java JDK** 11 or higher
- **MySQL** 8.0 or higher
- **MySQL Connector/J** 9.6.0 (included in `lib/`)

---

## 🗄 Database Setup

1. Open MySQL and create the database:

```sql
CREATE DATABASE IF NOT EXISTS java_shop;
USE java_shop;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE wallets (
    wallet_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    count INT DEFAULT 0
);

CREATE TABLE cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    bill_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'COMPLETED',
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE bill_items (
    bill_item_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price_at_purchase DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(bill_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
```

---

## ⚙ Configuration

Edit the `db.properties` file in the project root with your MySQL credentials:

```properties
db.url=jdbc:mysql://localhost:3306/java_shop
db.user=root
db.password=your_password_here
```

---

## 🚀 Build & Run

### Compile (Windows)

```bash
javac -cp "lib/mysql-connector-j-9.6.0.jar" -d bin src/model/*.java src/dao/*.java src/dao/impl/*.java src/factory/*.java src/util/*.java src/service/*.java src/exceptions/*.java src/controller/*.java src/Main.java
```

### Run (Windows)

```bash
java -cp "bin;lib/mysql-connector-j-9.6.0.jar" Main
```

---

## 📖 Menu Structure

```
WELCOME TO JAVA SHOP SYSTEM
├── 1. Product Management
│   ├── 1. View All Products
│   ├── 2. View Product by ID
│   ├── 3. Add New Product
│   ├── 4. Update Product Stock
│   ├── 5. Delete Product
│   └── 0. Back
├── 2. User Management
│   ├── 1. View All Users
│   ├── 2. Add New User (auto-creates wallet)
│   ├── 3. View Wallet Balance
│   ├── 4. Add Wallet Balance
│   └── 0. Back
├── 3. Cart Operations
│   ├── 1. View Cart
│   ├── 2. Add to Cart (shows available products)
│   ├── 3. Remove Item from Cart
│   ├── 4. Clear Cart
│   └── 0. Back
├── 4. Checkout
│   └── (Processes cart → creates bill → deducts wallet → updates stock)
├── 5. View Bills
│   ├── 1. View Bills by User
│   ├── 2. View Bill Details (with line items)
│   └── 0. Back
└── 0. Exit
```

---

## 🧩 Design Patterns

| Pattern              | Where Used                          | Purpose                                              |
|----------------------|-------------------------------------|------------------------------------------------------|
| **DAO Pattern**      | `dao/` + `dao/impl/`               | Separates data access from business logic            |
| **Factory Pattern**  | `factory/DAOFactory.java`           | Centralizes DAO object creation                      |
| **Singleton**        | `util/DBConnection.java`            | Single shared database connection instance            |
| **MVC (Console)**    | `controller/` + `service/` + `model/` | Separates UI, business logic, and data              |
| **Service Layer**    | `service/`                          | Encapsulates business rules and coordinates DAOs     |
| **Transaction Mgmt** | `util/TransactionManager.java`     | Ensures atomicity for multi-step operations          |
| **Custom Exceptions**| `exceptions/`                       | Domain-specific error handling with meaningful messages |

---

## 🔄 Transaction Flows

### Add to Cart Flow

```
User Request → CartService.addToCart(userId, productId, qty)
    ├── Validate user exists (UserDAO)
    ├── Validate product exists (ProductDAO)
    ├── Check stock availability
    └── Insert cart entry (CartDAO)
```

### Checkout Flow

```
User Request → CheckoutService.checkout(userId)
    ├── BEGIN TRANSACTION
    ├── Fetch cart items (CartDAO)
    ├── Validate cart is not empty
    ├── For each cart item:
    │   ├── Fetch product details & price (ProductDAO)
    │   ├── Validate stock availability
    │   └── Calculate line total
    ├── Calculate grand total
    ├── Fetch wallet balance (WalletDAO)
    ├── Validate sufficient funds
    ├── Deduct wallet balance (WalletDAO)
    ├── Create bill record (BillDAO) → returns billId
    ├── Create bill items (BillItemDAO)
    ├── Update product stock for each item (ProductDAO)
    ├── Clear cart (CartDAO)
    ├── COMMIT TRANSACTION
    └── Return billId
    
    On any failure → ROLLBACK TRANSACTION
```

---

## 🗃 Database Schema

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│    users     │     │   wallets    │     │   products   │
├──────────────┤     ├──────────────┤     ├──────────────┤
│ user_id (PK) │◄───►│ user_id (FK) │     │product_id(PK)│
│ name         │     │ wallet_id(PK)│     │ name         │
│ email        │     │ balance      │     │ price        │
└──────┬───────┘     └──────────────┘     │ count        │
       │                                   └──────┬───────┘
       │                                          │
       ▼                                          ▼
┌──────────────┐                          ┌──────────────┐
│    cart      │                          │  bill_items  │
├──────────────┤                          ├──────────────┤
│ cart_id (PK) │                          │bill_item_id  │
│ user_id (FK) │                          │ bill_id (FK) │
│product_id(FK)│                          │product_id(FK)│
│ quantity     │                          │ quantity     │
└──────────────┘                          │price_at_     │
       │                                  │  purchase    │
       ▼                                  └──────┬───────┘
┌──────────────┐                                 │
│    bills     │◄────────────────────────────────┘
├──────────────┤
│ bill_id (PK) │
│ user_id (FK) │
│ total_amount │
│ bill_date    │
│ status       │
└──────────────┘
```

---

## ⚠ Important Notes

1. **Java Version**: This project uses `var` (local variable type inference) in `MenuController.java`, which requires **Java 10+**. If using Java 8, replace `var` with explicit types.

2. **Connection Management**: The `DBConnection` class uses a Singleton pattern. The connection is shared across all DAO operations and closed once on application exit.

3. **Transaction Safety**: The checkout operation is wrapped in a database transaction. If any step fails (insufficient funds, out of stock, etc.), the entire operation is rolled back.

4. **Wallet Auto-Creation**: When a new user is registered via `UserService.registerUser()`, a wallet with a zero balance is automatically created.

5. **Input Validation**: The `ConsoleHelper` class handles invalid input gracefully, prompting the user to re-enter values without crashing the application.

6. **Error Messages**: All controllers catch exceptions and display user-friendly error messages prefixed with `[ERROR]`. Service-layer exceptions provide meaningful descriptions.

7. **MySQL Connector**: The MySQL JDBC driver (`mysql-connector-j-9.6.0.jar`) must be present in the `lib/` directory. It is included in the classpath during both compilation and execution.

---

## 📄 License

This project is developed for academic purposes as part of the DBMS Lab coursework.
