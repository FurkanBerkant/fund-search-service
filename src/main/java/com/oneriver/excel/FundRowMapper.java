package com.oneriver.excel;

import com.oneriver.enums.FundExcelColumn;
import com.oneriver.excel.dto.ExcelFunRowDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
public class FundRowMapper implements ExcelRowMapper<ExcelFunRowDTO> {
    private final Map<FundExcelColumn, Integer> columnMapping = new EnumMap<>(FundExcelColumn.class);
    private FormulaEvaluator cachedEvaluator;

    @Override
    public ExcelFunRowDTO mapRow(Row row, int rowNumber) {
        if (columnMapping.isEmpty()) {
            initializeColumnMapping(row);
            return null;
        }

        ExcelFunRowDTO.ExcelFunRowDTOBuilder builder = ExcelFunRowDTO.builder();

        set(builder::fundCode, row, FundExcelColumn.FUND_CODE);
        set(builder::fundName, row, FundExcelColumn.FUND_NAME);
        set(builder::umbrellaFundType, row, FundExcelColumn.UMBRELLA_FUND_TYPE);
        set(builder::oneMonth, row, FundExcelColumn.ONE_MONTH);
        set(builder::threeMonth, row, FundExcelColumn.THREE_MONTH);
        set(builder::sixMonth, row, FundExcelColumn.SIX_MONTH);
        set(builder::yearToDate, row, FundExcelColumn.YTD);
        set(builder::oneYear, row, FundExcelColumn.ONE_YEAR);
        set(builder::threeYear, row, FundExcelColumn.THREE_YEAR);
        set(builder::fiveYear, row, FundExcelColumn.FIVE_YEAR);

        return builder.build();
    }

    private void initializeColumnMapping(Row row) {
        columnMapping.clear();

        cachedEvaluator = row.getSheet().getWorkbook()
                .getCreationHelper()
                .createFormulaEvaluator();

        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null) continue;

            String header = getCellValueAsString(cell);
            FundExcelColumn column = FundExcelColumn.fromHeader(header);

            if (column != null) {
                columnMapping.put(column, i);
            }
        }

        log.debug("Mapped {} columns", columnMapping.size());
    }

    private void set(Consumer<String> setter, Row row, FundExcelColumn column) {
        Integer index = columnMapping.get(column);
        if (index == null) return;

        Cell cell = row.getCell(index);
        if (cell == null) return;

        String value = getCellValueAsString(cell);
        if (value != null && !value.isBlank()) {
            setter.accept(value.trim());
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();

            case NUMERIC:
                double v = cell.getNumericCellValue();
                return (v == (long) v) ? String.valueOf((long) v) : String.valueOf(v);

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case FORMULA:
                if (cachedEvaluator == null) {
                    cachedEvaluator = cell.getSheet().getWorkbook()
                            .getCreationHelper()
                            .createFormulaEvaluator();
                }

                try {
                    CellValue cv = cachedEvaluator.evaluate(cell);
                    if (cv == null) return null;

                    return switch (cv.getCellType()) {
                        case STRING -> cv.getStringValue();
                        case NUMERIC -> {
                            double nv = cv.getNumberValue();
                            yield (nv == (long) nv) ? String.valueOf((long) nv) : String.valueOf(nv);
                        }
                        case BOOLEAN -> String.valueOf(cv.getBooleanValue());
                        default -> null;
                    };
                } catch (Exception e) {
                    log.debug("Formula eval failed: {}", e.getMessage());
                    return null;
                }

            case BLANK:
            case ERROR:
            default:
                return null;
        }
    }
}

