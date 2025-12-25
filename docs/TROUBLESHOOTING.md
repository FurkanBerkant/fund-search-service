# 🔧 Troubleshooting

This document lists common issues and solutions for the Fund Search Service.

### Elasticsearch Errors
```bash
curl http://localhost:9200
# ES container logs
docker logs -f elasticsearch
```

### PostgreSQL Errors
```bash
# PostgreSQL container name: postgres
# Basic health check
docker exec -it postgres pg_isready -U postgres

# Or via compose
docker compose -f docker/docker-compose.yml exec postgres pg_isready -U postgres
```

### Kibana Issues
```bash
# Check if Kibana is responding
curl -sS http://localhost:5601/ | head -n 20
# Kibana service logs
docker logs -f kibana
```

### Missing Index Errors
```bash
# If the 'funds' index is missing, check ES and application logs first:
# ES logs
docker logs -f elasticsearch

# Check indices in ES
curl -sS 'http://localhost:9200/_cat/indices?v'

# If the index doesn't exist you can create it manually or use the application's import endpoint (example):
# Create manually (if templates are already loaded this will just create the index)
curl -s -X PUT 'http://localhost:9200/funds' -H 'Content-Type: application/json' -d '{"settings":{"number_of_shards":1}}'

# Import and index via the application (example Excel file)
curl -X POST http://localhost:8080/api/funds/import-and-index -F "file=@src/main/resources/takasbank-tefas-fon-karsilastirma.xlsx"
```

# Restart Services
```bash
docker compose -f docker/docker-compose.yml down
docker compose -f docker/docker-compose.yml up -d
```
