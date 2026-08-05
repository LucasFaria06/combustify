#!/bin/bash

# Script para importar postos de gasolina via API
# Uso: ./import_stations.sh [estado]
# Exemplo: ./import_stations.sh sp

API_BASE="http://localhost:8080/api"
ADMIN_KEY="admin-key-development"
STATE=${1:-sp}
STATE_UPPER=$(echo $STATE | tr '[:lower:]' '[:upper:]')

# Arquivo de dados
DATA_FILE="../data/sao_paulo_stations.json"

echo "🚀 Iniciando import de postos para $STATE_UPPER..."
echo ""

# Validar se arquivo existe
if [ ! -f "$DATA_FILE" ]; then
    echo "❌ Arquivo $DATA_FILE não encontrado"
    exit 1
fi

# Contar postos no arquivo
TOTAL=$(jq length "$DATA_FILE")
echo "📊 Total de postos no arquivo: $TOTAL"
echo ""

# Fazer requisição de import
echo "📤 Enviando dados para API..."
RESPONSE=$(curl -s -X POST "$API_BASE/admin/import-stations" \
  -H "Content-Type: application/json" \
  -H "X-Admin-Key: $ADMIN_KEY" \
  -d @"$DATA_FILE")

# Verificar resposta
if echo "$RESPONSE" | jq empty 2>/dev/null; then
    IMPORTED=$(echo "$RESPONSE" | jq '.imported')
    FAILED=$(echo "$RESPONSE" | jq '.failed')
    MESSAGE=$(echo "$RESPONSE" | jq -r '.message')

    echo ""
    echo "="
    echo "✅ Import concluído!"
    echo "📊 Importados: $IMPORTED"
    echo "❌ Falhados: $FAILED"
    echo "="
else
    echo "❌ Erro na resposta da API:"
    echo "$RESPONSE"
    exit 1
fi
