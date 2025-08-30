#!/bin/bash
set -e

# 0️⃣ Ustaw RAM VM Podman na 8GB przed startem kontenerów
echo "🔧 Konfiguruję maszynę Podman na 8GB RAM..."
podman machine stop || true
podman machine set --memory=8192
podman machine start

# 1️⃣ Hasła i dane
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

echo "🔄 Uruchamiam model Qwen (Ollama)..."
./scripts/run_qwen_ollama_podman.sh

echo "✅ Wszystkie usługi uruchomione i zainicjalizowane!"