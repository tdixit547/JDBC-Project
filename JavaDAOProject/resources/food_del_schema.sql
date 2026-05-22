-- Food Delivery Database Schema for JDBC
-- Run: mysql -u root -p < resources/food_del_schema.sql

CREATE DATABASE IF NOT EXISTS food_delivery;
USE food_delivery;

-- Foods table
CREATE TABLE IF NOT EXISTS foods (
    food_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    image VARCHAR(255),
    category VARCHAR(100),
    rating DECIMAL(3,1) DEFAULT 0,
    total_reviews INT DEFAULT 0
);

-- Food reviews
CREATE TABLE IF NOT EXISTS food_reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    food_id INT NOT NULL,
    user_id INT,
    rating DECIMAL(3,1),
    comment TEXT,
    review_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (food_id) REFERENCES foods(food_id) ON DELETE CASCADE
);

-- Users (with password for auth)
CREATE TABLE IF NOT EXISTS app_users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    password_salt VARCHAR(255) NOT NULL
);

-- Cart items
CREATE TABLE IF NOT EXISTS cart_items (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    food_id INT NOT NULL,
    quantity INT DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES app_users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES foods(food_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_food (user_id, food_id)
);

-- Orders
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    address_firstname VARCHAR(100),
    address_lastname VARCHAR(100),
    address_street VARCHAR(255),
    address_city VARCHAR(100),
    address_state VARCHAR(100),
    address_zipcode VARCHAR(20),
    address_country VARCHAR(100),
    address_phone VARCHAR(20),
    status VARCHAR(50) DEFAULT 'Food Processing',
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    payment BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES app_users(user_id)
);

-- Order line items
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    food_id INT NOT NULL,
    name VARCHAR(255),
    quantity INT,
    price DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

-- Seed 32 food items
INSERT INTO foods (name, description, price, image, category, rating, total_reviews) VALUES
('Greek salad','A vibrant and rustic medley of sun-ripened tomatoes, crisp cucumbers, sharp red onion, briny Kalamata olives, and generous chunks of creamy feta cheese.',12.00,'food_1.png','Salad',4.2,12),
('Veg salad','Bursting with color and freshness, this wholesome vegetable salad combines a crisp array of chopped seasonal vegetables tossed in a zesty dressing.',18.00,'food_2.png','Salad',4.0,8),
('Clover Salad','A delicate and nutritious blend featuring fresh clover leaves and blossoms, offering a mild, sweet flavor and a vibrant pop of color.',16.00,'food_3.png','Salad',3.9,10),
('Chicken Salad','A classic and satisfying choice featuring tender, cooked chicken enveloped in a creamy dressing with crunchy celery and flavorful onion.',24.00,'food_4.png','Salad',4.5,15),
('Lasagna Rolls','Experience the comforting layers of classic lasagna, reimagined into perfectly portioned rolls with savory meat sauce and decadent cheese.',14.00,'food_5.png','Rolls',4.3,11),
('Peri Peri Rolls','Ignite your taste buds with these delicious rolls packed with tender chicken, marinated in a vibrant, zesty peri-peri sauce.',12.00,'food_6.png','Rolls',4.1,9),
('Chicken Rolls','Savor the simple pleasure of succulent, seasoned chicken encased in a crispy exterior, offering a delightful combination of savory flavors.',20.00,'food_7.png','Rolls',4.4,13),
('Veg Rolls','Refresh your palate with our vibrant Veg Rolls, bursting with an array of colorful, crisp vegetables and fresh herbs.',15.00,'food_8.png','Rolls',3.8,7),
('Ripple Ice Cream','Indulge in the luscious swirls of ripple ice cream, where a rich, creamy base meets ribbons of sweet, fruity flavor.',14.00,'food_9.png','Deserts',4.6,18),
('Fruit Ice Cream','Experience a burst of fresh flavor with fruit ice cream, a vibrant and creamy delight that captures the essence of ripe fruits.',22.00,'food_10.png','Deserts',4.3,14),
('Jar Ice Cream','Discover the simple pleasure of jar ice cream, a delightfully creamy treat rich in flavor and perfect for a quick indulgence.',10.00,'food_11.png','Deserts',4.0,9),
('Vanilla Ice Cream','Savor the timeless elegance of vanilla ice cream, a classic, smooth, and sweet frozen dessert perfect on its own.',12.00,'food_12.png','Deserts',4.5,16),
('Chicken Sandwich','Sink your teeth into a perfectly cooked chicken sandwich, featuring tender, juicy chicken layered with fresh toppings on a toasted bun.',12.00,'food_13.png','Sandwich',4.2,11),
('Vegan Sandwich','Discover a vibrant explosion of plant-based goodness with fresh, crisp vegetables and creamy spreads in every bite.',18.00,'food_14.png','Sandwich',3.9,8),
('Grilled Sandwich','Experience the irresistible allure of a grilled sandwich, with its golden, crispy exterior giving way to warm, gooey fillings.',16.00,'food_15.png','Sandwich',4.4,12),
('Bread Sandwich','Savor the simple pleasure of a bread sandwich, where freshly baked bread perfectly cradles comforting fillings.',24.00,'food_16.png','Sandwich',4.1,10),
('Cup Cake','Indulge in our fluffy, moist, and delectable cupcakes, crafted to perfection with tender crumbs and rich, satisfying flavors.',14.00,'food_17.png','Cake',4.7,20),
('Vegan Cake','Experience pure plant-based bliss with our vegan cakes, boasting a soft, tender, and moist crumb that is rich in flavor.',12.00,'food_18.png','Cake',4.0,9),
('Butterscotch Cake','Savor the irresistible charm of our butterscotch cake, a deliciously moist creation with rich caramel toffee notes.',20.00,'food_19.png','Cake',4.5,14),
('Sliced Cake','Enjoy a perfect portion of pure delight with our expertly sliced cakes, featuring tempting layers and exquisite flavors.',15.00,'food_20.png','Cake',4.2,11),
('Garlic Mushroom','Savor the aromatic embrace of tender garlic mushrooms, expertly sauteed to golden perfection.',14.00,'food_21.png','Pure Veg',4.3,13),
('Fried Cauliflower','Exquisite delicate fried cauliflower florets, offering a delightful crispness that is truly satisfying.',22.00,'food_22.png','Pure Veg',4.1,10),
('Mix Veg Pulao','A vibrant mixed vegetable pulao, rich with fragrant spices and wholesome grains.',10.00,'food_23.png','Pure Veg',3.8,7),
('Rice Zucchini','Fluffy rice paired with tender zucchini, creating a truly satisfying and flavorful experience.',12.00,'food_24.png','Pure Veg',4.0,8),
('Cheese Pasta','Indulge in a steaming plate of pasta, glistening with a rich, velvety cheese sauce that lovingly clings to each strand.',12.00,'food_25.png','Pasta',4.4,15),
('Tomato Pasta','Savor the vibrant taste of sun-ripened tomatoes in a perfectly crafted pasta dish with aromatic herbs.',18.00,'food_26.png','Pasta',4.2,12),
('Creamy Pasta','Experience the ultimate comfort with a luxurious and ultra-satisfying creamy pasta featuring a rich, velvety sauce.',16.00,'food_27.png','Pasta',4.6,17),
('Chicken Pasta','Enjoy a hearty and flavorful chicken pasta, where tender pieces of chicken elevate a beloved pasta dish.',24.00,'food_28.png','Pasta',4.3,13),
('Butter Noodles','Tender, al dente noodles, perfectly coated in a rich, glistening butter sauce for a comforting experience.',14.00,'food_29.png','Noodles',4.1,10),
('Veg Noodles','Savor a vibrant medley of crisp, colorful vegetables expertly stir-fried with perfectly cooked noodles.',12.00,'food_30.png','Noodles',3.9,9),
('Somen Noodles','Experience the delicate elegance of somen noodles, known for their remarkably thin strands and distinctively chewy texture.',20.00,'food_31.png','Noodles',4.5,14),
('Cooked Noodles','Hot, fresh, and soft yet firm noodles ready to embrace a myriad of sauces and seasonings.',15.00,'food_32.png','Noodles',4.0,8);
