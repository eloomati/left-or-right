#!/bin/bash
# run_qwen_ollama_podman.sh

CONTAINER_NAME="ollama-qwen"
MODEL_NAME="qwen2:0.5b"
OLLAMA_PORT=11434

# 1. Pobierz obraz ollama, jeśli nie ma
if ! podman image exists ollama/ollama; then
  podman pull docker.io/ollama/ollama
fi

# 2. Uruchom kontener ollama (jeśli nie działa)
if ! podman ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
  podman run -d \
    --name ${CONTAINER_NAME} \
    -p ${OLLAMA_PORT}:11434 \
    --pull=always \
    --restart=unless-stopped \
    --memory=1g \
    ollama/ollama
  echo "⏳ Uruchamianie kontenera ollama z limitem 1GB RAM..."
else
  echo "✅ Kontener ollama już działa."
fi

# 3. Poczekaj na gotowość API
echo "⏳ Czekam na gotowość API ollama..."
until curl -s http://localhost:${OLLAMA_PORT}/api/tags > /dev/null; do
  sleep 2
done

# 4. Pobierz model Qwen
echo "⏳ Pobieram model ${MODEL_NAME}..."
podman exec ${CONTAINER_NAME} ollama pull ${MODEL_NAME}

echo "✅ Model Qwen jest gotowy i serwer działa na http://localhost:${OLLAMA_PORT}"