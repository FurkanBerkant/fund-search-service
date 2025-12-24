package com.oneriver.service;

import com.oneriver.excel.ExcelRowMapper;
import com.oneriver.excel.dto.ExcelImportResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ExcelImportService {
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(".xlsx", ".xls");

    public <T> ExcelImportResult<T> importFromExcel(MultipartFile file, ExcelRowMapper<T> rowMapper) throws IOException {
        validateFile(file);
        List<T> data = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        long totalStart = System.currentTimeMillis();

        try (InputStream is = file.getInputStream()) {
            long wbStart = System.currentTimeMillis();
            Workbook workbook = WorkbookFactory.create(is);
            long wbElapsed = System.currentTimeMillis() - wbStart;
            log.debug("Workbook created in {} ms", wbElapsed);

            Sheet sheet = workbook.getSheetAt(0);
            int rowIdx = 0;

            long mapStart = System.currentTimeMillis();

            for (Row row : sheet) {
                if (row == null) continue;
                try {
                    T item = rowMapper.mapRow(row, rowIdx++);
                    if (item != null) {
                        data.add(item);
                    }
                } catch (Exception e) {
                    errors.add("Row " + rowIdx + ": " + e.getMessage());
                }
            }

            long mapElapsed = System.currentTimeMillis() - mapStart;
            log.debug("Mapped {} rows in {} ms", Math.max(0, rowIdx - 1), mapElapsed);
        }

        long totalElapsed = System.currentTimeMillis() - totalStart;
        log.info("Excel import parsing completed in {} ms. Rows parsed: {}. Errors: {}",
                totalElapsed, data.size(), errors.size());

        return new ExcelImportResult<>(data, errors);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        boolean isSupported = SUPPORTED_EXTENSIONS.stream()
                .anyMatch(filename::endsWith);

        if (!isSupported) {
            throw new IllegalArgumentException("Unsupported file format. Supported formats: " +
                    String.join(", ", SUPPORTED_EXTENSIONS));
        }
    }
}