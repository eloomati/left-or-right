#!/bin/bash
# podman_start_redis.sh

CONTAINER_NAME="lor-redis"
REDIS_IMAGE="docker.io/library/redis:7.2"
REDIS_PORT=6379

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
  if podman run --name "$CONTAINER_NAME" -p $REDIS_PORT:6379 -d "$REDIS_IMAGE"; then
    echo "✅ Kontener Redis uruchomiony jako $CONTAINER_NAME na porcie $REDIS_PORT."
  else
    echo "❌ Błąd podczas uruchamiania kontenera Redis!"
    exit 1
  fi
fi