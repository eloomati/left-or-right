#!/bin/bash
# init_db.sh

DB_NAME=${1:-"lor_test"}
FLYWAY_PASSWORD=${2:-"silne_haslo"}
APP_PASSWORD=${3:-"silne_haslo"}
POSTGRES_PASSWORD=${4:-"admin"}

export PGPASSWORD="$POSTGRES_PASSWORD"
set -e

set -e

# Tworzenie bazy danych, jeśli nie istnieje
psql -h localhost -p 5432 -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = '${DB_NAME}'" | grep -q 1 || \
  psql -h localhost -p 5432 -U postgres -c "CREATE DATABASE ${DB_NAME} ENCODING 'UTF8' LC_COLLATE='en_US.utf8' LC_CTYPE='en_US.utf8' TEMPLATE=template0;"

# Tworzenie użytkowników i nadawanie uprawnień
psql -h localhost -p 5432 -U postgres <<EOF
DO
\$do\$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'lor_flyway') THEN
      CREATE USER lor_flyway WITH PASSWORD '${FLYWAY_PASSWORD}';
      ALTER USER lor_flyway WITH CREATEDB CREATEROLE;
      GRANT ALL PRIVILEGES ON DATABASE ${DB_NAME} TO lor_flyway;
   END IF;
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'lor_app') THEN
      CREATE USER lor_app WITH PASSWORD '${APP_PASSWORD}';
   END IF;
   GRANT CONNECT ON DATABASE ${DB_NAME} TO lor_app;
END
\$do\$;
EOF

# Nadanie uprawnień do schematu public dla lor_flyway
psql -h localhost -p 5432 -U postgres -d "${DB_NAME}" <<EOF
GRANT USAGE ON SCHEMA public TO lor_flyway;
GRANT CREATE ON SCHEMA public TO lor_flyway;
EOF

if [ $? -eq 0 ]; then
  echo "✅ Baza ${DB_NAME}, użytkownicy lor_flyway i lor_app utworzeni lub już istnieją, uprawnienia nadane."
else
  echo "❌ Błąd podczas inicjalizacji bazy danych lub użytkowników!"
  exit 1
fi