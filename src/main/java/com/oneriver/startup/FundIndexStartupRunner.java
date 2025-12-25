package com.oneriver.startup;

import com.oneriver.service.FundExcelService;
import com.oneriver.service.FundIndexService;
import com.oneriver.utils.FundConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@ConditionalOnProperty(
        prefix = "funds.index",
        name = "on-startup",
        havingValue = "true"
)
@RequiredArgsConstructor
@Slf4j
public class FundIndexStartupRunner implements ApplicationRunner {

    private final FundIndexService fundIndexService;
    private final FundExcelService fundExcelService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting fund indexing startup process...");

        try {
             syncDatabaseToElasticsearch();
             importInitialData();
            log.info("Fund indexing startup process completed successfully");

        } catch (Exception e) {
            log.error("Fatal error during startup indexing process", e);
            throw new RuntimeException("Startup indexing failed", e);
        }
    }

    private void syncDatabaseToElasticsearch() {
        try {
            int indexed = fundIndexService.indexAllFromDb();
            log.info("Successfully synced {} funds from database to Elasticsearch", indexed);
        } catch (Exception e) {
            log.error("Failed to sync database to Elasticsearch", e);
            throw new RuntimeException("Database sync failed", e);
        }
    }

    private void importInitialData() {
        ClassPathResource resource = new ClassPathResource(FundConstants.DEFAULT_EXCEL_FILE);

        if (!resource.exists()) {
            log.warn("Default Excel file '{}' not found in classpath. Skipping initial import.",
                    FundConstants.DEFAULT_EXCEL_FILE);
            log.info("To enable initial data import, place '{}' in src/main/resources",
                    FundConstants.DEFAULT_EXCEL_FILE);
            return;
        }

        try (InputStream is = resource.getInputStream()) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    FundConstants.DEFAULT_EXCEL_FILE,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    is
            );

            log.info("Importing initial data from '{}'", FundConstants.DEFAULT_EXCEL_FILE);
            var result = fundExcelService.importAndIndexAsync(file);

            log.info("Initial import completed. Total: {}, Success: {}, Failed: {}",
                    result.total(),
                    result.success(),
                    result.failed());
        } catch (Exception e) {
            log.error("Initial Excel import failed", e);
            throw new RuntimeException("Initial data import failed", e);
        }
    }
}