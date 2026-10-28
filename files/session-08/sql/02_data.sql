INSERT INTO customers (name, city, email) VALUES
    ('Sara',  'Cairo',      'sara@example.com'),
    ('Omar',  'Giza',       'omar@example.com'),
    ('Mona',  'Cairo',      'mona@example.com'),
    ('Ali',   'Alexandria', 'ali@example.com'),
    ('Nour',  'Giza',       NULL);

INSERT INTO products (name, category, price) VALUES
    ('Laptop',   'Electronics', 1200.00),
    ('Phone',    'Electronics',  700.00),
    ('Monitor',  'Electronics',  250.00),
    ('Keyboard', 'Accessories',   30.00),
    ('Mouse',    'Accessories',   15.00),
    ('Desk',     'Furniture',    180.00),
    ('Chair',    'Furniture',    120.00);

INSERT INTO orders (customer_id, ordered_on) VALUES
    (1, '2026-01-05'),
    (1, '2026-02-10'),
    (2, '2026-01-20'),
    (3, '2026-02-02'),
    (3, '2026-03-15'),
    (4, '2026-03-01');

INSERT INTO order_items (order_id, product_id, quantity) VALUES
    (1, 1, 1), (1, 5, 2),
    (2, 2, 1), (2, 4, 1),
    (3, 3, 2),
    (4, 6, 1), (4, 7, 2),
    (5, 1, 1), (5, 3, 1),
    (6, 4, 3), (6, 5, 3);
