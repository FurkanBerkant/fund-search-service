#!/bin/sh
set -eu

ES_URL=${ES_URL:-http://localhost:9200}
KIBANA_URL=${KIBANA_URL:-http://localhost:5601}

echo "Waiting for Elasticsearch at $ES_URL..."
until curl -sSf "$ES_URL" >/dev/null 2>&1; do
  printf '.'; sleep 1
done
printf "\nElasticsearch is up\n"

TMP_TEMPLATE="/tmp/funds-index-template.json"
cat > "$TMP_TEMPLATE" <<'JSON'
{
  "index_patterns": ["funds*"],
  "template": {
    "settings": {
      "number_of_shards": 1,
      "number_of_replicas": 0
    },
    "mappings": {
      "properties": {
        "fundCode": { "type": "keyword" },
        "fundName": {
          "type": "text",
          "fields": { "keyword": { "type": "keyword", "ignore_above": 256 } }
        },
        "umbrellaFundType": { "type": "keyword" },
        "returnPeriods": {
          "type": "object",
          "dynamic": true
        }
      }
    }
  }
}
JSON

echo "Creating/Updating index template 'funds_template'..."
curl -s -X PUT "$ES_URL/_index_template/funds_template" -H "Content-Type: application/json" -d @"$TMP_TEMPLATE" || true

echo "Ensuring index 'funds' exists..."
indices_resp=$(curl -s "$ES_URL/_cat/indices/funds?h=index" || true)
case "$indices_resp" in
  *funds*)
    echo "Index 'funds' already exists" ;;
  *)
    curl -s -X PUT "$ES_URL/funds" -H "Content-Type: application/json" -d '{"settings":{"number_of_shards":1}}' || true
    echo "Index 'funds' created" ;;
esac

echo "Waiting for Kibana at $KIBANA_URL..."
until curl -sSf "$KIBANA_URL" >/dev/null 2>&1; do
  printf '.'; sleep 1
done
printf "\nKibana is up\n"
echo "Creating Kibana data view for 'funds'..."
create_response=$(curl -s -X POST "$KIBANA_URL/api/data_views/data_view" \
  -H "kbn-xsrf: true" \
  -H "Content-Type: application/json" \
  -d '{"data_view": {"title": "funds", "timeFieldName": null}}' || true)

case "$create_response" in
  *"id"*)
    echo "Kibana data view created" ;;
  *)
    echo "Kibana data view creation response:" ;
    printf '%s\n' "$create_response" ;;
esac

echo "Done. You can now open Kibana at $KIBANA_URL and use the 'funds' data view."
