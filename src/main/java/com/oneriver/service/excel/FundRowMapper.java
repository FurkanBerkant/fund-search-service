package com.oneriver.service.excel;

import com.oneriver.dto.ExcelFundRowDTO;
import com.oneriver.enums.FundExcelColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
public class FundRowMapper implements ExcelRowMapper<ExcelFundRowDTO> {

    private Map<FundExcelColumn, Integer> columnMapping;

    @Override
    public void setColumnMapping(Map<FundExcelColumn, Integer> mapping) {
        this.columnMapping = mapping;
    }

    @Override
    public ExcelFundRowDTO mapRow(Row row, int rowNumber) {
        if (row == null || columnMapping == null || isRowEmpty(row)) {
            return null;
        }

        try {
            ExcelFundRowDTO.ExcelFundRowDTOBuilder builder = ExcelFundRowDTO.builder();

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
        } catch (Exception e) {
            log.warn("Failed to map row {}: {}", rowNumber, e.getMessage());
            return null;
        }
    }

    private void set(Consumer<String> setter, Row row, FundExcelColumn column) {
        Integer index = columnMapping.get(column);
        if (index != null) {
            Cell cell = row.getCell(index);
            String value = getCellValueAsString(cell);
            if (value != null && !value.isBlank()) {
                setter.accept(value.trim());
            }
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        try {
            CellType type = cell.getCellType();
            if (type == CellType.STRING) {
                return cell.getStringCellValue();
            }
            if (type == CellType.NUMERIC) {
                DataFormatter formatter = new DataFormatter();
                return formatter.formatCellValue(cell);
            }
            if (type == CellType.FORMULA) {
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                CellValue cv = evaluator.evaluate(cell);
                if (cv == null) return null;
                CellType cvType = cv.getCellType();
                if (cvType == CellType.STRING) return cv.getStringValue();
                if (cvType == CellType.NUMERIC) return BigDecimal.valueOf(cv.getNumberValue()).toPlainString();
                return null;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        Cell cell = row.getCell(row.getFirstCellNum());
        return cell == null || cell.getCellType() == CellType.BLANK;
    }
}