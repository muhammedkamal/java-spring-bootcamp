-- Lab 2 (reference solution): 15 SQL queries on the shop schema (PostgreSQL).
-- Reset first: run sql/01_schema.sql and sql/02_data.sql. Write one query under each question.
-- The comment after each question is the expected result (PostgreSQL prints prices with 2 decimals, e.g. 1200.00).

-- 1. All customers from Cairo, sorted by name.
-- Expected (2 rows):
--   Mona
--   Sara
SELECT name FROM customers WHERE city = 'Cairo' ORDER BY name;

-- 2. Products that cost more than 200, most expensive first (name and price).
-- Expected (3 rows):
--   Laptop, 1200
--   Phone, 700
--   Monitor, 250
SELECT name, price FROM products WHERE price > 200 ORDER BY price DESC;

-- 3. The 3 cheapest products (name and price).
-- Expected (3 rows):
--   Mouse, 15
--   Keyboard, 30
--   Chair, 120
SELECT name, price FROM products ORDER BY price ASC LIMIT 3;

-- 4. Customers that have no email.
-- Expected (1 rows):
--   Nour
SELECT name FROM customers WHERE email IS NULL;

-- 5. How many products are in each category? (category, count), sorted by category.
-- Expected (3 rows):
--   Accessories, 2
--   Electronics, 3
--   Furniture, 2
SELECT category, COUNT(*) AS products FROM products GROUP BY category ORDER BY category;

-- 6. Each order with the customer name (order id, name, date), sorted by order id.
-- Expected (6 rows):
--   1, Sara, 2026-01-05
--   2, Sara, 2026-02-10
--   3, Omar, 2026-01-20
--   4, Mona, 2026-02-02
--   5, Mona, 2026-03-15
--   6, Ali, 2026-03-01
SELECT o.id, c.name, o.ordered_on FROM orders o JOIN customers c ON c.id = o.customer_id ORDER BY o.id;

-- 7. Every customer with the number of orders they placed, including customers with 0 (name, orders), sorted by name.
-- Expected (5 rows):
--   Ali, 1
--   Mona, 2
--   Nour, 0
--   Omar, 1
--   Sara, 2
SELECT c.name, COUNT(o.id) AS orders FROM customers c LEFT JOIN orders o ON o.customer_id = c.id GROUP BY c.name ORDER BY c.name;

-- 8. Customers who never ordered anything.
-- Expected (1 rows):
--   Nour
SELECT c.name FROM customers c LEFT JOIN orders o ON o.customer_id = c.id WHERE o.id IS NULL;

-- 9. The lines of order 4 (product name, quantity, line total = price * quantity), sorted by product name.
-- Expected (2 rows):
--   Chair, 2, 240
--   Desk, 1, 180
SELECT p.name, i.quantity, p.price * i.quantity AS line_total FROM order_items i JOIN products p ON p.id = i.product_id WHERE i.order_id = 4 ORDER BY p.name;

-- 10. Total spent per customer (name, total), highest first. Customers without orders are not needed.
-- Expected (4 rows):
--   Sara, 1960
--   Mona, 1870
--   Omar, 500
--   Ali, 135
SELECT c.name, SUM(p.price * i.quantity) AS total FROM customers c JOIN orders o ON o.customer_id = c.id JOIN order_items i ON i.order_id = o.id JOIN products p ON p.id = i.product_id GROUP BY c.name ORDER BY total DESC;

-- 11. Customers who spent more than 1000 in total (name, total), highest first. Use HAVING.
-- Expected (2 rows):
--   Sara, 1960
--   Mona, 1870
SELECT c.name, SUM(p.price * i.quantity) AS total FROM customers c JOIN orders o ON o.customer_id = c.id JOIN order_items i ON i.order_id = o.id JOIN products p ON p.id = i.product_id GROUP BY c.name HAVING SUM(p.price * i.quantity) > 1000 ORDER BY total DESC;

-- 12. Units sold per product (name, units), best seller first; products never sold are not needed. Ties by name.
-- Expected (7 rows):
--   Mouse, 5
--   Keyboard, 4
--   Monitor, 3
--   Chair, 2
--   Laptop, 2
--   Desk, 1
--   Phone, 1
SELECT p.name, SUM(i.quantity) AS units FROM products p JOIN order_items i ON i.product_id = p.id GROUP BY p.name ORDER BY units DESC, p.name;

-- 13. Products that were never ordered.
-- Expected (0 rows):

SELECT p.name FROM products p LEFT JOIN order_items i ON i.product_id = p.id WHERE i.order_id IS NULL;

-- 14. Average product price per category (category, avg_price), sorted by category.
-- Expected (3 rows):
--   Accessories, 22.5
--   Electronics, 716.6666666666666
--   Furniture, 150.0
SELECT category, AVG(price) AS avg_price FROM products GROUP BY category ORDER BY category;

-- 15. Rank the products inside each category by price, highest first (name, category, price, rank). Use DENSE_RANK() OVER (...). Sort by category, rank.
-- Expected (7 rows):
--   Keyboard, Accessories, 30, 1
--   Mouse, Accessories, 15, 2
--   Laptop, Electronics, 1200, 1
--   Phone, Electronics, 700, 2
--   Monitor, Electronics, 250, 3
--   Desk, Furniture, 180, 1
--   Chair, Furniture, 120, 2
SELECT name, category, price, DENSE_RANK() OVER (PARTITION BY category ORDER BY price DESC) AS rank_in_category FROM products ORDER BY category, rank_in_category;

