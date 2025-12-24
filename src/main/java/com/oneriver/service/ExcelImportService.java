package com.oneriver.service;

import com.oneriver.enums.FundExcelColumn;
import com.oneriver.service.excel.ExcelRowMapper;
import com.oneriver.service.excel.FundRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExcelImportService {

    public <T> List<T> importFromExcel(MultipartFile file, ExcelRowMapper<T> rowMapper) throws IOException {
        List<T> data = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(1);
            if (headerRow == null) throw new IllegalArgumentException("Header row not found at index 1");

            Map<FundExcelColumn, Integer> mapping = new EnumMap<>(FundExcelColumn.class);
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    String header = cell.getStringCellValue().trim();
                    FundExcelColumn column = FundExcelColumn.fromHeader(header);
                    if (column != null) mapping.put(column, i);
                }
            }

            if (rowMapper instanceof FundRowMapper frm) {
                frm.setColumnMapping(mapping);
            }

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                T item = rowMapper.mapRow(row, i);
                if (item != null) data.add(item);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}