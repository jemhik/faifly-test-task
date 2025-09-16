# Getting Started

### Quick start with Docker Compose (auto-load sample data)
- Run: `docker compose up -d`
- What happens on first start:
  - A MySQL 8 container named `mysql8` starts.
  - It creates a database named `faifly` and automatically executes the SQL dump at `src/main/resources/db/mysql-dump.sql`.
    - We mount that dump into the container as `/docker-entrypoint-initdb.d/mysql-dump.sql` and MySQL auto-runs any .sql files there.
  - Data persists in the `mysql8_data` volume; the dump is auto-applied only on the very first start (or after `down -v`).
- To reset and re-seed: `docker compose down -v` then `docker compose up -d`.

Start the app (already configured to use MySQL by default):
- `mvn spring-boot:run`

---

## Loading the MySQL dump manually (optional)
The SQL dump is at `src/main/resources/db/mysql-dump.sql`.

Prerequisites:
- MySQL 8 client (the `mysql` CLI) OR a MySQL Docker container.
- A running MySQL server you can connect to.

### Option A: Import using local MySQL client
1) Create DB if needed:
   - `mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS faifly CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;"`
2) Import (from project root):
   - `mysql -u root -p faifly < src\main\resources\db\mysql-dump.sql`

### Option B: Import using Dockerized MySQL
If your container is named `mysql8` and root password is `secret`:
- Create DB (no password prompt warning):
  - `docker exec -e MYSQL_PWD=secret -it mysql8 mysql -u root -e "CREATE DATABASE IF NOT EXISTS faifly;"`
- Import dump:
  - `docker exec -e MYSQL_PWD=secret -i mysql8 mysql -u root faifly < src/main/resources/db/mysql-dump.sql`
  - If the path isn’t accessible, copy first: `docker cp src/main/resources/db/mysql-dump.sql mysql8:/mysql-dump.sql` then `docker exec -e MYSQL_PWD=secret -i mysql8 mysql -u root faifly < /mysql-dump.sql`

### Verify the import
- `mysql -u root -p -e "USE faifly; SHOW TABLES; SELECT COUNT(*) FROM doctor; SELECT COUNT(*) FROM patient; SELECT COUNT(*) FROM visit;"`

### Run the app
- The app reads configuration from `src/main/resources/application.properties` (MySQL URL/user/password already set).
- Start: `mvn spring-boot:run`
- Endpoints:
  - POST `/visits` — create a visit
  - GET `/patients` — list patients with optional `page`, `size`, `search`, `doctorIds`

> Note: The `docker/` directory is no longer used by docker-compose. You can safely delete it from your repository if you prefer a leaner tree.

