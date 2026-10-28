-- JOINs: combine rows from related tables
-- INNER JOIN: only rows that match on both sides
SELECT o.id, c.name, o.ordered_on
FROM orders o
JOIN customers c ON c.id = o.customer_id
ORDER BY o.id;

-- Three tables: what was in each order?
SELECT o.id AS order_id, c.name AS customer, p.name AS product, i.quantity, p.price * i.quantity AS line_total
FROM orders o
JOIN customers c   ON c.id = o.customer_id
JOIN order_items i ON i.order_id = o.id
JOIN products p    ON p.id = i.product_id
ORDER BY o.id, p.name;

-- LEFT JOIN: keep every customer, even those with no orders (Nour has none)
SELECT c.name, o.id AS order_id
FROM customers c
LEFT JOIN orders o ON o.customer_id = c.id
ORDER BY c.name;

-- Customers who never ordered: LEFT JOIN + IS NULL
SELECT c.name
FROM customers c
LEFT JOIN orders o ON o.customer_id = c.id
WHERE o.id IS NULL;
