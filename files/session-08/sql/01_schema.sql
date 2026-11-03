-- Shop schema for PostgreSQL. Run: psql -U postgres -d shop -f sql/01_schema.sql
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
    id    INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    city  VARCHAR(50)  NOT NULL,
    email VARCHAR(150) UNIQUE
);

CREATE TABLE products (
    id       INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name     VARCHAR(100)  NOT NULL,
    category VARCHAR(50)   NOT NULL,
    price    NUMERIC(10,2) NOT NULL CHECK (price >= 0)
);

CREATE TABLE orders (
    id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id INT  NOT NULL REFERENCES customers(id),
    ordered_on  DATE NOT NULL
);

CREATE TABLE order_items (
    order_id   INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(id),
    quantity   INT NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (order_id, product_id)
);
