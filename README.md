# 🏦 Fund Search Service

Türkiye yatırım fonlarını arama, filtreleme ve analiz etmek için geliştirilmiş Spring Boot uygulaması.

## 📋 İçindekiler

- [Özellikler](#-özellikler)
- [Teknolojiler](#-teknolojiler)
- [Kurulum](#-kurulum)
- [API Dokümantasyonu](#-api-dokümantasyonu)
- [Test Senaryoları](#-test-senaryoları)
- [Swagger UI](#-swagger-ui)
- [Konfigürasyon](#konfigürasyon)
- [Troubleshooting](#-troubleshooting)

---

## ✨ Özellikler

| Özellik | Açıklama |
|---------|----------|
| 📥 Excel Import | Apache POI ile Excel dosyasından fon verisi okuma |
| 🔍 Full-Text Search | Fon kodu ve adına göre fuzzy arama |
| 📄 Sayfalama | Sayfa bazlı sonuç döndürme |
| 🔢 Sıralama | Getiri periyotlarına göre sıralama |
| 🏷️ Filtreleme | Şemsiye fon türüne göre filtreleme |
| 🚀 Async Indexing | Asenkron Elasticsearch indeksleme |
| 🔄 Startup Import | Uygulama başlangıcında otomatik veri yükleme |

---

## 🛠 Teknolojiler

| Teknoloji | Versiyon | Açıklama |
|-----------|----------|----------|
| Java | 21 | JDK |
| Spring Boot | 3.x | Framework |
| PostgreSQL | 15 | İlişkisel veritabanı |
| Elasticsearch | 8.10.2 | Arama motoru |
| Apache POI | 5.4.0 | Excel okuma |
| MapStruct | 1.5.5 | DTO mapping |
| Lombok | 1.18.30 | Boilerplate azaltma |
| Docker Compose | 3.8 | Konteyner yönetimi |

---

## 🚀 Kurulum

### Gereksinimler

```bash
java -version          # Java 21 gerekli
mvn -version           # Maven 3.8+ gerekli
docker --version       # Docker gerekli
docker compose version # Docker Compose gerekli
```

### Adım 1: Repository'yi Klonla

```bash
git clone <repository-url>
cd fund-search-service
```

### Adım 2: Docker ile Servisleri Başlat

Proje içindeki Docker Compose dosyası `docker/docker-compose.yml` olarak yer alır. Yeni `elastic-setup` servisi sayesinde Elasticsearch ve Kibana için gerekli index/template ve Kibana Data View oluşturma adımları otomatik çalıştırılır — manuel işlem yapmanız gerekmez.

Projeyi kök dizinden şu şekilde başlatın:

```bash
# Proje kökünden (fund-search-service) çalıştırın
docker compose -f docker/docker-compose.yml up -d
```

Servislerin hazır olduğunu kontrol et:

```bash
# Elasticsearch
curl http://localhost:9200

# Kibana
# Tarayıcı: http://localhost:5601
```

Otomatik setup servisi loglarını kontrol etmek isterseniz:

```bash
# elastic-setup konteyneri one-shot çalışır; logları kısa süre sonra tamamlanıp çıkar
docker logs -f elastic-setup

# Eğer setup servisini tekrar elle çalıştırmak isterseniz
docker compose -f docker/docker-compose.yml run --rm elastic-setup
```

(Not: `elastic-setup` script'i şu adımları gerçekleştirir: ES hazır olana kadar bekler, `funds` index template/mapping oluşturur, `funds` index'i yoksa oluşturur, Kibana hazır olana kadar bekler ve Kibana API üzerinden `funds` data view oluşturmaya çalışır.)

### Adım 3: Uygulamayı Çalıştır

```bash
# Derleme
mvn clean package -DskipTests

# Çalıştırma
java -jar target/fund-search-service-0.0.1-SNAPSHOT.jar
```

### ✅ Uygulama Hazır!

Uygulama başladıktan sonra aşağıdaki endpoint'lere erişebilirsiniz:

| Endpoint | Açıklama |
|----------|----------|
| `http://localhost:8080/api/funds/search` | Fon arama API |
| `http://localhost:8080/api/funds/top-performers` | En iyi performans |
| `http://localhost:8080/swagger-ui.html` | API Dokümantasyonu |
| `http://localhost:8080/actuator/health` | Sağlık durumu kontrolü |

**Hızlı Test:**
```bash
# Sağlık durumu kontrolü
curl http://localhost:8080/actuator/health

# Tüm fonları listele
curl "http://localhost:8080/api/funds/search"
```

---

## 📚 API Dokümantasyonu

Base URL: `http://localhost:8080/api/funds`

### 1️⃣ Excel Import

```http
POST /api/funds/import-and-index
Content-Type: multipart/form-data
```

| Parametre | Tip | Zorunlu | Açıklama |
|-----------|-----|---------|----------|
| `file` | MultipartFile | ✅ | Excel dosyası (.xlsx) |

**Response:** `202 ACCEPTED`
```json
{
  "status": "accepted",
  "stats": { "total": 150, "success": 148, "failed": 2 },
  "savedCodes": ["ABC", "XYZ"]
}
```

---

### 2️⃣ Fon Arama

```http
GET /api/funds/search
```

| Parametre | Tip | Default | Açıklama |
|-----------|-----|---------|----------|
| `query` | String | - | Arama metni (fon kodu/adı) |
| `umbrellaType` | String | - | Şemsiye fon türü filtresi |
| `returnPeriod` | String | - | Getiri periyodu (filtreleme için) |
| `minReturn` | Double | - | Minimum getiri (%) |
| `maxReturn` | Double | - | Maksimum getiri (%) |
| `sortBy` | String | `fundName` | Sıralama alanı |
| `sortDirection` | String | `asc` | Sıralama yönü |
| `page` | Integer | `0` | Sayfa numarası |
| `size` | Integer | `20` | Sayfa boyutu (max: 100) |

**sortBy değerleri:** `fundCode`, `fundName`, `oneMonth`, `threeMonths`, `sixMonths`, `yearToDate`, `oneYear`, `threeYears`, `fiveYears`

**returnPeriod değerleri:** `oneMonth`, `threeMonths`, `sixMonths`, `yearToDate`, `oneYear`, `threeYears`, `fiveYears`

---

### 3️⃣ Şemsiye Türüne Göre Listeleme

```http
GET /api/funds/by-umbrella/{type}
```

---

### 4️⃣ En İyi Performans Gösteren Fonlar

```http
GET /api/funds/top-performers?period=oneYear&limit=10
```

| Parametre | Default | Açıklama |
|-----------|---------|----------|
| `period` | `oneYear` | Getiri periyodu |
| `limit` | `10` | Sonuç limiti |

---

## 🧪 Test Senaryoları

### Test 1: Excel Import
```bash
curl -X POST http://localhost:8080/api/funds/import-and-index \
  -F "file=@src/main/resources/takasbank-tefas-fon-karsilastirma.xlsx"
```

### Test 2: Tüm Fonları Listele
```bash
curl -s "http://localhost:8080/api/funds/search" | jq
```

### Test 3: Partial Match Arama (AK → AKBANK)
```bash
curl -s "http://localhost:8080/api/funds/search?query=AK" | jq
```

### Test 4: Fon Adı ile Arama
```bash
curl -s "http://localhost:8080/api/funds/search?query=Hisse" | jq
```

### Test 5: Şemsiye Türü Filtreleme
```bash
curl -s "http://localhost:8080/api/funds/search?umbrellaType=Serbest%20%C5%9Eemsiye%20Fonu" | jq
```

### Test 6: Getiriye Göre Sıralama
```bash
# 1 yıllık getiriye göre azalan
curl -s "http://localhost:8080/api/funds/search?sortBy=oneYear&sortDirection=desc" | jq

# 3 aylık getiriye göre artan
curl -s "http://localhost:8080/api/funds/search?sortBy=threeMonths&sortDirection=asc" | jq
```

### Test 7: Sayfalama
```bash
# Sayfa 0, 10 kayıt
curl -s "http://localhost:8080/api/funds/search?page=0&size=10" | jq

# Sayfa 1, 10 kayıt
curl -s "http://localhost:8080/api/funds/search?page=1&size=10" | jq
```

### Test 8: En İyi Performans
```bash
# 1 yıllık en iyi 10 fon
curl -s "http://localhost:8080/api/funds/top-performers?period=oneYear&limit=10" | jq

# 5 yıllık en iyi 5 fon
curl -s "http://localhost:8080/api/funds/top-performers?period=fiveYears&limit=5" | jq
```

### Test 9: Kombine Filtreler
```bash
curl -s "http://localhost:8080/api/funds/search?query=ak&umbrellaType=Serbest%20%C5%9Eemsiye%20Fonu&sortBy=oneYear&sortDirection=desc&page=0&size=10" | jq
```

### Test 10: Getiri Aralığına Göre Filtreleme (BONUS)
```bash
# 1 yıllık getirisi %50'den fazla olan fonlar
curl -s "http://localhost:8080/api/funds/search?returnPeriod=oneYear&minReturn=50" | jq

# 1 yıllık getirisi %20 ile %50 arasında olan fonlar
curl -s "http://localhost:8080/api/funds/search?returnPeriod=oneYear&minReturn=20&maxReturn=50" | jq

# 3 aylık getirisi %10'dan az olan fonlar
curl -s "http://localhost:8080/api/funds/search?returnPeriod=threeMonths&maxReturn=10" | jq

# 5 yıllık getirisi %100'den fazla, azalan sıralı
curl -s "http://localhost:8080/api/funds/search?returnPeriod=fiveYears&minReturn=100&sortBy=fiveYears&sortDirection=desc" | jq

# Kombine: Serbest fonlarda 1 yıllık getirisi %30'dan fazla olanlar
curl -s "http://localhost:8080/api/funds/search?umbrellaType=Serbest%20%C5%9Eemsiye%20Fonu&returnPeriod=oneYear&minReturn=30&sortBy=oneYear&sortDirection=desc" | jq
```

### Test 11: Edge Cases
```bash
# Boş sonuç
curl -s "http://localhost:8080/api/funds/search?query=XYZNONEXISTENT" | jq

# Geçersiz sayfa (validation hatası beklenir)
curl -s "http://localhost:8080/api/funds/search?page=-1"
```

---

## 📖 Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## ⚙️ Konfigürasyon

| Ayar | Değer | Açıklama |
|------|-------|----------|
| `server.port` | 8080 | Uygulama portu |
| `spring.datasource.url` | jdbc:postgresql://localhost:5432/funds_db | PostgreSQL |
| `spring.elasticsearch.uris` | http://localhost:9200 | Elasticsearch |
| `funds.index.on-startup` | false | Başlangıçta otomatik import |

**Startup Import Aktifleştirme:**
```yaml
funds:
  index:
    on-startup: true
```

---

## 🔧 Troubleshooting

### Elasticsearch Hatası
```bash
curl http://localhost:9200
# ES konteyner logları
docker logs -f elasticsearch
```

### PostgreSQL Hatası
```bash
# PostgreSQL container adı: postgres
# Basit sağlık kontrolü
docker exec -it postgres pg_isready -U postgres

# Veya compose üzerinden
docker compose -f docker/docker-compose.yml exec postgres pg_isready -U postgres
```

### Kibana Hatası
```bash
# Kibana çalışıyor mu kontrol et
curl -sS http://localhost:5601/ | head -n 20
# Kibana servis logları
docker logs -f kibana
```

### Index Yok Hatası
```bash
# Eğer 'funds' index'i görünmüyorsa setup servisi atlamış veya uygulama veriyi göndermemiş olabilir.
# Önce otomatik setup loglarını kontrol edin
docker logs fund_search-elastic-setup

# Eğer uygulama veriyi index'lemiyorsa elle import yapabilirsiniz
curl -X POST http://localhost:8080/api/funds/import-and-index \
  -F "file=@src/main/resources/takasbank-tefas-fon-karsilastirma.xlsx"
```

### Servisleri Yeniden Başlat
```bash
docker compose -f docker/docker-compose.yml down
docker compose -f docker/docker-compose.yml up -d
```

---