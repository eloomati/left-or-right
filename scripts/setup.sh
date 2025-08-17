#!/bin/bash
set -e

read -sp "Podaj hasło administratora PostgreSQL (postgres): " POSTGRES_PASSWORD
echo

echo "🔄 Uruchamiam kontener PostgreSQL..."
./scripts/podman_start_postgres.sh "$POSTGRES_PASSWORD"

echo "🔄 Uruchamiam Redisa..."
./scripts/podman_start_redis.sh

echo "🔄 Uruchamiam Kafkę..."
./scripts/podman_start_kafka.sh

read -p "Podaj nazwę bazy danych (ENTER = lor_test): " DB_NAME
DB_NAME=${DB_NAME:-"lor_test"}

read -sp "Podaj hasło dla lor_flyway: " FLYWAY_PASSWORD
echo
read -sp "Podaj hasło dla lor_app: " APP_PASSWORD
echo

echo "🔄 Inicjalizuję bazę danych i użytkowników..."
./scripts/init_db.sh "$DB_NAME" "$FLYWAY_PASSWORD" "$APP_PASSWORD" "$POSTGRES_PASSWORD"

echo "✅ Wszystkie usługi uruchomione i zainicjalizowane!"