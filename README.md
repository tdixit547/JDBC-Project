# ShopZone E-Commerce Platform

ShopZone is a full-stack, single-page application (SPA) built to demonstrate robust web development and backend engineering using pure Java and JDBC. The project avoids heavy frameworks like Spring Boot, Tomcat, or React to focus heavily on foundational software architecture, including custom HTTP routing, JSON parsing, Data Access Objects (DAO), and ACID transaction management.

##  Features

### Customer Storefront
* **Dynamic Product Catalog:** View premium tech products with custom uploaded photos dynamically fetched from the database.
* **Shopping Cart System:** Add items to cart with live quantity adjustment.
* **Digital Wallet:** Virtual wallet system for simulating real-money transactions.
* **Seamless Checkout:** 1-click checkout that automatically validates stock, checks wallet balance, generates an invoice, and clears the cart inside a secure database transaction.
* **Order History:** View past orders and detailed invoices.

### Admin Dashboard
* **Metrics Overview:** Dashboard displaying total products, registered users, low stock alerts, and overall catalog value.
* **Product Management:** Add, update, and delete products. Edit stock levels in real-time.
* **Image Upload:** Upload product images directly from the dashboard. The images are automatically Base64-encoded, sent to the server, and written to disk.
* **User Management:** Register new users and add balance to user wallets.
* **Cart & Order Tracking:** View active carts and full order history for any user by User ID.

##  Technology Stack

* **Frontend:** HTML5, CSS3 (Custom variables, glassmorphism, flexbox/grid), Vanilla JavaScript (ES6+, Fetch API, async/await).
* **Backend:** Java 11+
* **Server:** Custom built using `com.sun.net.httpserver.HttpServer`.
* **Database Engine:** MySQL 8.0+
* **Database Connectivity:** JDBC (`mysql-connector-j`)

##  Architecture 

### Data Access Object (DAO) Pattern
Instead of mixing SQL queries with business logic, the application uses the DAO pattern. 
* Interfaces define the capabilities (e.g., `ProductDAO`).
* Implementations handle the JDBC specifics (e.g., `ProductDAO_JDBC`).
* The `DAOFactory` provides Singleton instances and injects shared database connections.

### Secure JDBC Operations
The project prevents SQL injection by strictly using parameterized `PreparedStatement`s for all database communication. Java objects are hydrated from `ResultSet`s using custom mapping functions.

### Transaction Management (ACID)
The `TransactionManager` ensures atomicity. During checkout, multiple steps occur: cart verification, total calculation, wallet deduction, bill creation, and cart clearing. 
By utilizing `conn.setAutoCommit(false)` and `conn.commit()`, the system ensures that if any single step fails (e.g., insufficient funds), the entire checkout is rolled back (`conn.rollback()`), preventing data corruption or money loss.

### Custom HTTP Server & API
The server (`AppServer.java`) explicitly registers route handlers (`ProductApiHandler`, `UserApiHandler`). A lightweight `JsonHelper` utility extracts fields from incoming JSON payloads and formats outgoing Java objects, bypassing the need for dependencies like Jackson or Gson.

##  Setup & Installation

### Prerequisites
* Java Development Kit (JDK 11 or higher)
* MySQL Server
* `mysql-connector-j` driver (included in `src/`)

### Database Setup
1. Open your MySQL terminal.
2. Run the provided database creation script:
   ```bash
   mysql -u root -p < create.sql
   ```
3. Update `JavaDAOProject/resources/db.properties` with your database credentials:
   ```properties
   db.url=jdbc:mysql://localhost:3306/ecommerce_dao
   db.user=root
   db.password=your_password
   db.driver=com.mysql.cj.jdbc.Driver
   ```

### Running the Application
1. Compile the Java source code:
   ```bash
   cd JavaDAOProject
   javac -d bin -cp "src;src/mysql-connector-j-9.7.0.jar" src/Main.java
   ```
2. Start the server:
   ```bash
   java -cp "bin;src/mysql-connector-j-9.7.0.jar" Main --web 8081
   ```
3. Open your browser and navigate to:
   ```
   http://localhost:8081/index.html
   ```

##  Screenshots & Design
The UI leverages a deeply aesthetic modern design featuring vibrant gradients, dark mode, smooth transitions, toast notifications, and interactive elements. All navigation is handled purely client-side for a seamless SPA experience.
