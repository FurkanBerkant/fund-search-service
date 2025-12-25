package com.oneriver.service;

import com.oneriver.entity.Fund;
import com.oneriver.entity.document.FundDocument;
import com.oneriver.mapper.FundMapper;
import com.oneriver.repository.FundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FundIndexService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final FundRepository fundRepository;
    private final FundMapper fundMapper;
    @Value("${funds.index.name}")
    private String indexName;
    @Value("${funds.index.batch-size:500}")
    private int batchSize;

    @Transactional(readOnly = true)
    public int indexAllFromDb() {

        long startTime = System.currentTimeMillis();
        List<Fund> allFunds = fundRepository.findAll();

        if (allFunds.isEmpty()) {
            log.warn("No funds found in database to index");
            return 0;
        }

        int indexed = indexFunds(allFunds);
        long duration = System.currentTimeMillis() - startTime;

        log.info("Full re-indexing completed. Indexed {} funds in {} ms", indexed, duration);
        return indexed;
    }

    public void ensureIndexWithMapping() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));

        if (indexOps.exists()) {
            log.debug("Elasticsearch index '{}' already exists", indexName);
            return;
        }

        try {
            Map<String, Object> settings = createIndexSettings();

            indexOps.create(Document.from(settings));
            Document mapping = indexOps.createMapping(FundDocument.class);
            indexOps.putMapping(mapping);

            log.info("Created Elasticsearch index '{}' with mapping and settings", indexName);
        } catch (Exception e) {
            log.error("Failed to create Elasticsearch index", e);
            throw new RuntimeException("Index creation failed", e);
        }
    }

    public int indexFunds(List<Fund> funds) {
        if (funds == null || funds.isEmpty()) {
            log.warn("No funds to index");
            return 0;
        }

        ensureIndexWithMapping();

        int totalIndexed = 0;
        List<FundDocument> allDocs = funds.stream()
                .map(fundMapper::toDocument)
                .toList();

        for (int i = 0; i < allDocs.size(); i += batchSize) {
            int end = Math.min(i + batchSize, allDocs.size());
            List<FundDocument> batch = allDocs.subList(i, end);

            try {
                elasticsearchOperations.save(batch, IndexCoordinates.of(indexName));
                totalIndexed += batch.size();
                log.debug("Indexed batch {}-{} of {} documents", i, end, allDocs.size());
            } catch (Exception e) {
                log.error("Failed to index batch {}-{}", i, end, e);
                throw new RuntimeException("Batch indexing failed", e);
            }
        }

        log.info("Successfully indexed {} documents in {} batches",
                totalIndexed, (allDocs.size() + batchSize - 1) / batchSize);

        return totalIndexed;
    }

    @Async("taskExecutor")
    public void indexByCodesAsync(List<String> fundCodes) {
        if (fundCodes == null || fundCodes.isEmpty()) {
            log.warn("No fund codes provided for async indexing");
            return;
        }

        try {
            List<Fund> funds = fundRepository.findAllByFundCodeIn(fundCodes);

            if (funds.isEmpty()) {
                log.warn("No funds found for provided codes: {}", fundCodes);
                return;
            }

            indexFunds(funds);

        } catch (Exception e) {
            log.error("Async indexing failed for fund codes: {}", fundCodes, e);
        }
    }

    private Map<String, Object> createIndexSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("number_of_shards", 1);
        settings.put("number_of_replicas", 1);

        Map<String, Object> analysis = new HashMap<>();
        Map<String, Object> analyzer = new HashMap<>();
        analyzer.put("turkish", Map.of(
                "type", "turkish",
                "stopwords", "_turkish_"
        ));
        analysis.put("analyzer", analyzer);
        settings.put("analysis", analysis);

        return Map.of("index", settings);
    }

}
