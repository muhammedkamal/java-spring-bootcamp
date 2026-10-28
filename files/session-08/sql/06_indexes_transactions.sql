-- Indexes: a B-tree lets the database jump to rows instead of reading the whole table.
CREATE INDEX idx_orders_customer ON orders (customer_id);
CREATE INDEX idx_products_category ON products (category);
EXPLAIN SELECT * FROM orders WHERE customer_id = 1;      -- on a tiny table Postgres may still scan: indexes pay off on big tables

-- Transactions: all or nothing (ACID)
BEGIN;
UPDATE products SET price = price - 100 WHERE name = 'Laptop';
INSERT INTO orders (customer_id, ordered_on) VALUES (4, CURRENT_DATE);
-- Something wrong? Undo both changes:
ROLLBACK;
-- Everything fine? Keep both changes:
-- COMMIT;

-- Window function: rank products inside each category by price
SELECT name, category, price,
       DENSE_RANK() OVER (PARTITION BY category ORDER BY price DESC) AS rank_in_category
FROM products
ORDER BY category, rank_in_category;
