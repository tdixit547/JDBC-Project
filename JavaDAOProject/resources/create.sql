-- =============================================
--  Database Schema
-- DBMS Lab | DAO Pattern with JDBC
-- Mini E-Commerce System (Refactored)
-- =============================================

-- Create the database (if not exists)
CREATE DATABASE IF NOT EXISTS ecommerce_dao;
USE ecommerce_dao;

-- Drop tables in reverse dependency order (for re-runs)
DROP TABLE IF EXISTS bill_items;
DROP TABLE IF EXISTS bill;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS wallet;
DROP TABLE IF EXISTS users;

-- =============================================
-- Table: users
-- Stores registered user information
-- =============================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

-- =============================================
-- Table: wallet (one per user)
-- Stores the wallet balance for each user
-- =============================================
CREATE TABLE wallet (
    wallet_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE NOT NULL,
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- =============================================
-- Table: product
-- Stores product catalog with stock count
-- =============================================
CREATE TABLE product (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    count INT NOT NULL DEFAULT 0
);

-- =============================================
-- Table: cart
-- Stores items added to a user's cart
-- Unique constraint prevents duplicate product entries per user
-- =============================================
CREATE TABLE cart (
    cart_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id),
    UNIQUE KEY uk_cart_user_product (user_id, product_id)
);

-- =============================================
-- Table: bill (one bill per checkout)
-- Stores the final bill generated at checkout
-- =============================================
CREATE TABLE bill (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('COMPLETED', 'CANCELLED') DEFAULT 'COMPLETED',
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =============================================
-- Table: bill_items (one row per product in a bill)
-- Stores individual items within a bill
-- =============================================
CREATE TABLE bill_items (
    bill_item_id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price_at_purchase DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bill(bill_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- =============================================
-- SEED DATA
-- =============================================

-- 3 Users
INSERT INTO users (name, email) VALUES
    ('Alice Johnson', 'alice@example.com'),
    ('Bob Smith', 'bob@example.com'),
    ('Charlie Brown', 'charlie@example.com');

-- 3 Wallet entries (one per user)
INSERT INTO wallet (user_id, balance) VALUES
    (1, 5000.00),
    (2, 3000.00),
    (3, 1500.00);

-- 5 Products
INSERT INTO product (name, price, count) VALUES
    ('Wireless Mouse', 499.99, 50),
    ('Mechanical Keyboard', 1299.00, 30),
    ('USB-C Hub', 799.50, 25),
    ('Monitor Stand', 1599.00, 15),
    ('Webcam HD', 2499.99, 10);
