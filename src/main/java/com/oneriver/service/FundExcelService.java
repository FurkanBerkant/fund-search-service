package com.oneriver.service;

import com.oneriver.excel.FundRowMapper;
import com.oneriver.excel.dto.ExcelFunRowDTO;
import com.oneriver.excel.dto.ExcelImportResult;
import com.oneriver.excel.dto.FundImportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundExcelService {

    private final ExcelImportService excelImportService;
    private final FundRowMapper fundRowMapper;
    private final FundImportService fundImportService;

    public FundImportResponse importFromExcel(MultipartFile file) throws IOException {
        ExcelImportResult<ExcelFunRowDTO> excelResult = excelImportService.importFromExcel(file, fundRowMapper);

        return fundImportService.process(excelResult.data());
    }
}
