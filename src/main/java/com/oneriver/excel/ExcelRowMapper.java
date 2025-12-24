package com.oneriver.excel;


import org.apache.poi.ss.usermodel.Row;

public interface ExcelRowMapper<T> {
    T mapRow(Row row, int rowNum) throws Exception;
}
