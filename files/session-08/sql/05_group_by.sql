-- Aggregates: COUNT, SUM, AVG, MIN, MAX
SELECT COUNT(*) AS products, MIN(price), MAX(price), AVG(price) FROM products;

-- GROUP BY: one row per group
SELECT category, COUNT(*) AS products, AVG(price) AS avg_price
FROM products
GROUP BY category
ORDER BY category;

-- WHERE filters rows before grouping. HAVING filters groups after.
SELECT c.name, SUM(p.price * i.quantity) AS total
FROM customers c
JOIN orders o      ON o.customer_id = c.id
JOIN order_items i ON i.order_id = o.id
JOIN products p    ON p.id = i.product_id
GROUP BY c.name
HAVING SUM(p.price * i.quantity) > 1000
ORDER BY total DESC;

-- Every selected column must be in GROUP BY or inside an aggregate.
