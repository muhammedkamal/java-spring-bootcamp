# Week 4, Session 4B: HTTP, REST and SQL

Slides: `../Session08_HTTP_REST_and_SQL.pptx`

## Start PostgreSQL

```bash
cd files/session-08
docker compose up -d
```

This starts PostgreSQL 17 with a database called `shop` (user `postgres`, password `postgres`, port 5432). Connect with DBeaver or the IntelliJ Database tool, or:

```bash
docker compose exec db psql -U postgres -d shop
```

Load the data by running `sql/01_schema.sql` and then `sql/02_data.sql`. Re-run both any time to reset the data.

## Demos

| File | Topic |
| --- | --- |
| `demos/HttpBasics.java` | A tiny server and client: GET, POST, DELETE, 405, 401 and status codes (`java demos/HttpBasics.java`) |
| `demos/JdbcIdea.java` | Plain JDBC: `PreparedStatement`, `ResultSet`, a transaction |
| `sql/01_schema.sql` | Shop schema: customers, products, orders, order_items |
| `sql/02_data.sql` | Sample data |
| `sql/03_crud.sql` | `INSERT`, `SELECT`, `UPDATE`, `DELETE` |
| `sql/04_joins.sql` | `INNER JOIN`, `LEFT JOIN`, finding rows with no match |
| `sql/05_group_by.sql` | Aggregates, `GROUP BY`, `HAVING` |
| `sql/06_indexes_transactions.sql` | Index, `BEGIN` / `ROLLBACK`, `DENSE_RANK` |

`03_crud.sql` changes the data (prices go up 10 percent). Reset with `01` and `02` before the lab.

## JDBC needs the driver

Download `postgresql-42.7.4.jar` from <https://jdbc.postgresql.org/download/> into this folder, then:

```bash
java -cp postgresql-42.7.4.jar demos/JdbcIdea.java
```

## Labs

| Lab | Task |
| --- | --- |
| 1 | Postman: GET and POST against a public API, trigger a 404 and a 400 (no file) |
| 2 | 15 SQL queries on the shop schema (`lab/starter/Lab2_queries.sql`) |
| 3 | Intern A: JDBC CRUD for products (`lab/starter/Lab3JdbcCrud.java`) |

Each query in Lab 2 lists its expected rows. PostgreSQL prints prices with 2 decimals (`1200.00`). Intern B does the concurrency exercise on Tuesday and Wednesday instead of Lab 3.

## Before the next session

1. Finish Lab 2 and commit your answers. Merge the Library v2 pull request.
2. Solve 5 problems from LeetCode SQL 50, and HackerRank SQL Basic Join. LeetCode: Best Time to Buy and Sell Stock.
3. Thursday is the mid-course checkpoint: 90 minutes, practical.
