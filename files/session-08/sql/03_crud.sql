-- CRUD: Create, Read, Update, Delete
INSERT INTO customers (name, city, email) VALUES ('Hana', 'Cairo', 'hana@example.com');

SELECT * FROM customers;
SELECT name, city FROM customers WHERE city = 'Cairo' ORDER BY name;
SELECT name, price FROM products WHERE price BETWEEN 100 AND 800 ORDER BY price DESC;
SELECT name FROM customers WHERE name LIKE 'M%';
SELECT name FROM customers WHERE email IS NULL;           -- NULL needs IS NULL, never = NULL
SELECT DISTINCT category FROM products;
SELECT name, price FROM products ORDER BY price DESC LIMIT 3;

UPDATE products SET price = price * 1.10 WHERE category = 'Accessories';
DELETE FROM customers WHERE name = 'Hana';

-- Always check a WHERE clause before UPDATE or DELETE: run it as a SELECT first.
