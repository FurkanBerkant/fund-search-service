# 🏦 Fund Search Service

Spring Boot application to search, filter and analyze Turkish investment funds.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.x-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)

## ✨ Features

*   **Excel Import:** Load Tefas/Takasbank data into the system.
*   **Search:** Fast fuzzy search by fund code and name.
*   **Filtering & Sorting:** Advanced listing by return rates and fund types.
*   **Performance:** Asynchronous indexing using Elasticsearch.

## 🚀 Quick Start

### Requirements
*   Java 21
*   Docker & Docker Compose

### Step 1: Clone
```bash
git clone <repository-url>
cd fund-search-service
```

### Step 2: Start infrastructure
```bash
docker compose -f docker/docker-compose.yml up -d
```
*Wait for Elasticsearch and PostgreSQL to become ready.*

### Step 3: Run the application
```bash
# Run with Maven
./mvnw spring-boot:run
```

### Step 4: Load initial data
Upload the sample Excel file required for the application:
```bash
curl -X POST http://localhost:8080/api/funds/import-and-index \
  -F "file=@src/main/resources/takasbank-tefas-fon-karsilastirma.xlsx"
```

## 📚 Documentation

For detailed usage and troubleshooting guides, see the `docs/` folder:

*   [🧪 Test Scenarios and API Examples](docs/TEST_SCENARIOS.md)
*   [🔧 Troubleshooting](docs/TROUBLESHOOTING.md)

## 🔗 APIs and Interfaces

*   **Swagger UI:** `http://localhost:8080/swagger-ui.html`
*   **Elasticsearch:** `http://localhost:9200`
*   **Kibana (optional):** `http://localhost:5601`

## 🛠 Technologies

*   **Core:** Spring Boot 3.x, Java 21
*   **Data:** PostgreSQL, Elasticsearch 8.x
*   **Tools:** Apache POI, MapStruct, Lombok, Docker
