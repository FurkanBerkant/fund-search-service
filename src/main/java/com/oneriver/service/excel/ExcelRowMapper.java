package com.oneriver.service.excel;

import com.oneriver.enums.FundExcelColumn;

import org.apache.poi.ss.usermodel.Row;
import java.util.Map;

public interface ExcelRowMapper<T> {
    T mapRow(Row row, int rowNum) throws Exception;
    default void setColumnMapping(Map<FundExcelColumn, Integer> mapping) {
    }
}
