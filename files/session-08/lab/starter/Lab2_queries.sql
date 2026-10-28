-- Lab 2: 15 SQL queries on the shop schema (PostgreSQL).
-- Reset first: run sql/01_schema.sql and sql/02_data.sql. Write one query under each question.
-- The comment after each question is the expected result (PostgreSQL prints prices with 2 decimals, e.g. 1200.00).

-- 1. All customers from Cairo, sorted by name.
-- Expected (2 rows):
--   Mona
--   Sara
-- TODO

-- 2. Products that cost more than 200, most expensive first (name and price).
-- Expected (3 rows):
--   Laptop, 1200
--   Phone, 700
--   Monitor, 250
-- TODO

-- 3. The 3 cheapest products (name and price).
-- Expected (3 rows):
--   Mouse, 15
--   Keyboard, 30
--   Chair, 120
-- TODO

-- 4. Customers that have no email.
-- Expected (1 rows):
--   Nour
-- TODO

-- 5. How many products are in each category? (category, count), sorted by category.
-- Expected (3 rows):
--   Accessories, 2
--   Electronics, 3
--   Furniture, 2
-- TODO

-- 6. Each order with the customer name (order id, name, date), sorted by order id.
-- Expected (6 rows):
--   1, Sara, 2026-01-05
--   2, Sara, 2026-02-10
--   3, Omar, 2026-01-20
--   4, Mona, 2026-02-02
--   5, Mona, 2026-03-15
--   6, Ali, 2026-03-01
-- TODO

-- 7. Every customer with the number of orders they placed, including customers with 0 (name, orders), sorted by name.
-- Expected (5 rows):
--   Ali, 1
--   Mona, 2
--   Nour, 0
--   Omar, 1
--   Sara, 2
-- TODO

-- 8. Customers who never ordered anything.
-- Expected (1 rows):
--   Nour
-- TODO

-- 9. The lines of order 4 (product name, quantity, line total = price * quantity), sorted by product name.
-- Expected (2 rows):
--   Chair, 2, 240
--   Desk, 1, 180
-- TODO

-- 10. Total spent per customer (name, total), highest first. Customers without orders are not needed.
-- Expected (4 rows):
--   Sara, 1960
--   Mona, 1870
--   Omar, 500
--   Ali, 135
-- TODO

-- 11. Customers who spent more than 1000 in total (name, total), highest first. Use HAVING.
-- Expected (2 rows):
--   Sara, 1960
--   Mona, 1870
-- TODO

-- 12. Units sold per product (name, units), best seller first; products never sold are not needed. Ties by name.
-- Expected (7 rows):
--   Mouse, 5
--   Keyboard, 4
--   Monitor, 3
--   Chair, 2
--   Laptop, 2
--   Desk, 1
--   Phone, 1
-- TODO

-- 13. Products that were never ordered.
-- Expected (0 rows):

-- TODO

-- 14. Average product price per category (category, avg_price), sorted by category.
-- Expected (3 rows):
--   Accessories, 22.5
--   Electronics, 716.6666666666666
--   Furniture, 150.0
-- TODO

-- 15. Rank the products inside each category by price, highest first (name, category, price, rank). Use DENSE_RANK() OVER (...). Sort by category, rank.
-- Expected (7 rows):
--   Keyboard, Accessories, 30, 1
--   Mouse, Accessories, 15, 2
--   Laptop, Electronics, 1200, 1
--   Phone, Electronics, 700, 2
--   Monitor, Electronics, 250, 3
--   Desk, Furniture, 180, 1
--   Chair, Furniture, 120, 2
-- TODO

