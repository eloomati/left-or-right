#!/bin/bash

# Tworzenie sieci tylko jeśli nie istnieje
podman network inspect kafka-net >/dev/null 2>&1 || podman network create kafka-net

# Uruchomienie Kafki w trybie KRaft (bez Zookeepera)
podman run -d --name kafka --network kafka-net \
  -e KAFKA_CFG_NODE_ID=1 \
   -e KAFKA_CFG_DELETE_TOPIC_ENABLE=true \
  -e KAFKA_CFG_PROCESS_ROLES=broker,controller \
  -e KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=1@kafka:9093 \
  -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT \
  -e KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e ALLOW_PLAINTEXT_LISTENER=yes \
  -p 9092:9092 \
  docker.io/bitnami/kafka:latest