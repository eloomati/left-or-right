#!/bin/bash
# podman_start_postgres.sh

CONTAINER_NAME="lor-postgres"
POSTGRES_PASSWORD="${1:-admin}"
POSTGRES_IMAGE="docker.io/library/postgres:15.5"
POSTGRES_PORT=5432

OS_TYPE="$(uname -s)"

if [[ "$OS_TYPE" == "Darwin" ]]; then
  # macOS
  if ! podman info &>/dev/null; then
    echo "🔄 Uruchamiam maszynę Podman (macOS)..."
    podman machine start
    if [ $? -ne 0 ]; then
      echo "❌ Nie udało się uruchomić maszyny Podman!"
      exit 1
    fi
  fi
elif [[ "$OS_TYPE" == "Linux" ]]; then
  echo "🟢 Wykryto Linux - nie trzeba uruchamiać maszyny Podman."
elif [[ "$OS_TYPE" == "MINGW"* || "$OS_TYPE" == "CYGWIN"* || "$OS_TYPE" == "MSYS"* ]]; then
  echo "🟢 Wykryto Windows - załóż, że Podman działa jako usługa."
else
  echo "⚠️  Nierozpoznany system operacyjny: $OS_TYPE"
fi

if podman container exists "$CONTAINER_NAME"; then
  echo "ℹ️  Kontener $CONTAINER_NAME już istnieje. Restartuję..."
  podman restart "$CONTAINER_NAME"
else
  if podman run --name "$CONTAINER_NAME" -e POSTGRES_PASSWORD="$POSTGRES_PASSWORD" -p $POSTGRES_PORT:5432 -d "$POSTGRES_IMAGE"; then
    echo "✅ Kontener PostgreSQL uruchomiony jako $CONTAINER_NAME na porcie $POSTGRES_PORT."
  else
    echo "❌ Błąd podczas uruchamiania kontenera PostgreSQL!"
    exit 1
  fi
fi