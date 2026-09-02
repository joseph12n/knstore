#!/usr/bin/env bash
# Backup de MongoDB KN-Store (replica set rs0 en el puerto 27018)
# Uso: ./backup.sh
set -euo pipefail

URI='mongodb://localhost:27018/knstore?replicaSet=rs0&directConnection=true'
BACKUP_FILE="knstore-backup-$(date +%Y%m%d-%H%M%S).gz"

mongodump --uri "$URI" --archive="$BACKUP_FILE" --gzip
echo "Backup creado: $(pwd)/$BACKUP_FILE"
