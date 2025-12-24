# Fund Search Service

## Project Overview

Fund Search Service is a Spring Boot application for searching, filtering, and analyzing investment funds (Turkey-focused). Data can be imported from an Excel file (Apache POI), persisted to PostgreSQL, and indexed into Elasticsearch for fast search. The project supports batch import, async indexing, paging, sorting and multiple filtering options.

## Quick Start (Setup)

Prerequisites

- Java 21 installed (check with `java -version`)
- Maven 3.8+
- Docker & Docker Compose (optional but recommended)

Steps

1) Clone the repository

```bash
git clone <repository-url>
cd fund-search-service
```

2) Start PostgreSQL and Elasticsearch using Docker Compose (recommended)

```bash
# from project root
docker compose up -d
```

This brings up PostgreSQL on port 5432 and Elasticsearch on port 9200 by default (see `docker/docker-compose.yml`).

3) Build the application

```bash
mvn clean package
```

4) Run the application

```bash
# for development
mvn spring-boot:run
# or run the packaged jar
java -jar target/fund-search-service-0.0.1-SNAPSHOT.jar
```

The application runs by default on http://localhost:8080.

## Configuration

Main configuration is in `src/main/resources/application.yaml`.

Important settings:

- `spring.datasource.*` (JDBC URL, username, password) — when using the provided Docker Compose the defaults are `jdbc:postgresql://localhost:5432/funds_db`, user `postgres`, password `postgres`.
- `spring.elasticsearch.uris` — default `http://localhost:9200`.
- `funds.index.on-startup` — if true the app may import/index sample Excel data at startup.

Note: JPA `ddl-auto` is currently set to `create-drop` in `application.yaml`. Change to `validate` or `none` in production.

## Sample Excel file

A sample Excel file is included in the repository: `src/main/resources/takasbank-tefas-fon-karsilastirma.xlsx`.

- Automatic import on startup: set `funds.index.on-startup: true`.
- Manual import via API: use the `import-and-index` endpoint described below.

## API (actual endpoints in the code)

I inspected the project controllers and documented only the endpoints that exist in the code. The following endpoints are available under the base path `/api/funds`.

1) POST /api/funds/import-and-index
- Purpose: Upload an Excel file and start asynchronous import & indexing.
- Consumes: multipart/form-data
- Form field: `file` (the Excel file)
- Response: HTTP 202 ACCEPTED with a JSON containing import stats and saved codes.

Example:

```bash
curl -X POST http://localhost:8080/api/funds/import-and-index \
  -F "file=@takasbank-tefas-fon-karsilastirma.xlsx"
```

2) GET /api/funds/search
- Purpose: Full-text and filtered search over indexed funds.
- Query parameters:
  - `q` (optional) — free text query
  - `umbrellaType` (optional) — filter by umbrella fund type
  - `minOneYearReturn` (optional, Double) — filter by 1-year return minimum
  - `sortBy` (optional, default: `fundName`)
  - `sortDirection` (optional, default: `asc`)
  - `page` (optional, default: 0)
  - `size` (optional, default: 20)
- Response: JSON with `funds` list and pagination metadata.

Example:

```bash
curl "http://localhost:8080/api/funds/search?q=AKBANK&page=0&size=20"
```

3) GET /api/funds/by-umbrella/{type}
- Purpose: Search funds by umbrella fund type.
- Path variable: `{type}` — umbrella fund type (URL-encoded if contains spaces)
- Query parameters: `page`, `size` (optional)

Example:

```bash
curl "http://localhost:8080/api/funds/by-umbrella/Hisse%20Senedi%20Fonu?page=0&size=10"
```

4) GET /api/funds/top-performers
- Purpose: Retrieve top performing funds for a given period.
- Query parameters:
  - `period` (optional, default: `oneYear`) — the return period to sort by
  - `limit` (optional, default: 10) — number of results

Example:

```bash
curl "http://localhost:8080/api/funds/top-performers?period=oneYear&limit=10"
```

Response DTO (FundSearchResponse)

The search endpoints return items that match the `FundSearchResponse` DTO in the code. Fields:

- `fundCode` (String)
- `fundName` (String)
- `umbrellaFundType` (String)
- `returnPeriods` (Map<String, BigDecimal>) — map of period keys to numeric returns (e.g. `oneYear`, `threeYears`)

Example item:

```json
{
  "fundCode": "AKB",
  "fundName": "AKBANK HİSSE SENEDİ FONU",
  "umbrellaFundType": "Hisse Senedi Fonu",
  "returnPeriods": {
    "oneMonth": 5.23,
    "threeMonths": 12.45,
    "sixMonths": 25.67,
    "yearToDate": 45.89,
    "oneYear": 52.34
  }
}
```

## Troubleshooting

- Elasticsearch connection: `curl http://localhost:9200` to check whether ES is up.
- PostgreSQL connection: `psql -U postgres -d funds_db -c "SELECT 1"` or check Docker logs.
- If import fails: verify Excel format and check application logs for errors.

## Notes

- pom.xml defines Java version 21; ensure local JDK matches.
- The project disables Elasticsearch security in the included `docker-compose.yml` for local development (`xpack.security.enabled=false`). Don't use this configuration in production.

## Packaging / Production

Build and run the packaged jar:

```bash
mvn clean package -DskipTests
java -jar target/fund-search-service-0.0.1-SNAPSHOT.jar
```


---
