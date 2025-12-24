package com.oneriver.service;

import com.oneriver.dto.ExcelFundRowDTO;
import com.oneriver.dto.FundImportResult;
import com.oneriver.service.excel.ExcelImportService;
import com.oneriver.service.excel.FundRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundExcelService {

    private final ExcelImportService excelImportService;
    private final FundImportService fundImportService;
    private final FundIndexService fundIndexService;
    private final FundRowMapper fundRowMapper;

    public FundImportResult importAndIndexAsync(MultipartFile file) {
        List<ExcelFundRowDTO> excelData = excelImportService.importFromExcel(file, fundRowMapper);
        FundImportResult result = fundImportService.process(excelData);

        if (!result.savedFundCodes().isEmpty()) {
            fundIndexService.indexByCodesAsync(result.savedFundCodes());
        }

        return result;
    }

}