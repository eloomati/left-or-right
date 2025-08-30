#!/bin/bash
# run_qwen_ollama_podman.sh

CONTAINER_NAME="ollama-qwen"
MODEL_NAME="qwen2.5"
OLLAMA_PORT=11434

# 1. Pobierz obraz Ollama, jeśli nie ma
if ! podman image exists ollama/ollama; then
  podman pull docker.io/ollama/ollama
fi

# 2. Uruchom kontener Ollama (jeśli nie działa)
if ! podman ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
  podman run -d \
    --name ${CONTAINER_NAME} \
    -p ${OLLAMA_PORT}:11434 \
    --pull=always \
    --restart=unless-stopped \
    --memory=8g --memory-swap=10g \
    ollama/ollama
  echo "⏳ Uruchamianie kontenera Ollama z limitem 8GB RAM..."
else
  echo "✅ Kontener Ollama już działa."
fi

# 3. Poczekaj na gotowość API
echo "⏳ Czekam na gotowość API Ollama..."
until curl -s http://localhost:${OLLAMA_PORT}/api/tags > /dev/null; do
  sleep 2
done

# 4. Pobierz model Qwen 2.7B
echo "⏳ Pobieram model ${MODEL_NAME}..."
podman exec ${CONTAINER_NAME} ollama pull ${MODEL_NAME}

echo "✅ Model Qwen 2.7B jest gotowy i serwer działa na http://localhost:${OLLAMA_PORT}"
