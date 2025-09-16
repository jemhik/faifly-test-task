-- Import the main application dump if present (mounted by docker-compose)
SOURCE /docker-entrypoint-initdb.d/mysql-dump.sql;
