# 🧪 Test Scenarios

This document contains useful test scenarios and curl examples for the Fund Search Service.

### Test 1: Excel Import
```bash
curl -X POST http://localhost:8080/api/funds/import-and-index \
  -F "file=@src/main/resources/takasbank-tefas-fon-karsilastirma.xlsx"
```

### Test 2: List All Funds
```bash
curl -s "http://localhost:8080/api/funds/search" | jq
```

### Test 3: Partial Match Search (AK → AKBANK)
```bash
curl -s "http://localhost:8080/api/funds/search?query=AK" | jq
```

### Test 4: Search by Fund Name
```bash
curl -s "http://localhost:8080/api/funds/search?query=Hisse" | jq
```

### Test 5: Filter by Umbrella Type
```bash
curl -s "http://localhost:8080/api/funds/search?umbrellaType=Serbest%20%C5%9Eemsiye%20Fonu" | jq
```

### Test 6: Sort by Returns
```bash
# Descending by 1-year return
curl -s "http://localhost:8080/api/funds/search?sortBy=oneYear&sortDirection=desc" | jq

# Ascending by 3-month return
curl -s "http://localhost:8080/api/funds/search?sortBy=threeMonths&sortDirection=asc" | jq
```

### Test 7: Pagination
```bash
# Page 0, 10 records
curl -s "http://localhost:8080/api/funds/search?page=0&size=10" | jq

# Page 1, 10 records
curl -s "http://localhost:8080/api/funds/search?page=1&size=10" | jq
```

### Test 8: Top Performers
```bash
# Top 10 by 1-year return
curl -s "http://localhost:8080/api/funds/top-performers?period=oneYear&limit=10" | jq

# Top 5 by 5-year return
curl -s "http://localhost:8080/api/funds/top-performers?period=fiveYears&limit=5" | jq
```

### Test 9: Combined Filters
```bash
curl -s "http://localhost:8080/api/funds/search?query=ak&umbrellaType=Serbest%20%C5%9Eemsiye%20Fonu&sortBy=oneYear&sortDirection=desc&page=0&size=10" | jq
```

### Test 10: Filter by Return Range (BONUS)
```bash
# Funds with 1-year return greater than 50%
curl -s "http://localhost:8080/api/funds/search?returnPeriod=oneYear&minReturn=50" | jq

# Funds with 1-year return between 20% and 50%
curl -s "http://localhost:8080/api/funds/search?returnPeriod=oneYear&minReturn=20&maxReturn=50" | jq

# Funds with 3-month return less than 10%
curl -s "http://localhost:8080/api/funds/search?returnPeriod=threeMonths&maxReturn=10" | jq

# 5-year return greater than 100%, sorted descending
curl -s "http://localhost:8080/api/funds/search?returnPeriod=fiveYears&minReturn=100&sortBy=fiveYears&sortDirection=desc" | jq

# Combined: umbrella funds with 1-year return greater than 30%
curl -s "http://localhost:8080/api/funds/search?umbrellaType=Serbest%20%C5%9Eemsiye%20Fonu&returnPeriod=oneYear&minReturn=30&sortBy=oneYear&sortDirection=desc" | jq
```

### Test 11: Edge Cases
```bash
# Empty result
curl -s "http://localhost:8080/api/funds/search?query=XYZNONEXISTENT" | jq

# Invalid page (expect validation error)
curl -s "http://localhost:8080/api/funds/search?page=-1"
```
