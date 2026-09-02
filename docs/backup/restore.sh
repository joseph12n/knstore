#!/usr/bin/env bash
# Restauración de backup MongoDB KN-Store (replica set rs0 en el puerto 27018)
# Uso: ./restore.sh <archivo.gz>
set -euo pipefail

URI='mongodb://localhost:27018/knstore?replicaSet=rs0&directConnection=true'
BACKUP_FILE="${1:-}"

if [ -z "$BACKUP_FILE" ]; then
  echo "Uso: ./restore.sh <archivo.gz>" >&2
  exit 1
fi

mongorestore --uri "$URI" --archive="$BACKUP_FILE" --gzip --drop
echo "Restauración completada desde: $BACKUP_FILE"
