package com.oneriver.controller;

import com.oneriver.excel.dto.FundImportResponse;
import com.oneriver.service.FundExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/funds")
@RequiredArgsConstructor
@Slf4j
public class FundController {
    private final FundExcelService fundExcelService;

    @PostMapping("/import")
    public ResponseEntity<FundImportResponse> importFromFile(
            @RequestParam MultipartFile file) throws IOException {

        return ResponseEntity.ok(fundExcelService.importFromExcel(file));
    }
}