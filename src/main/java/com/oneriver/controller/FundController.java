package com.oneriver.controller;

import com.oneriver.dto.FundImportResult;
import com.oneriver.service.FundExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/funds")
@RequiredArgsConstructor
@Slf4j
public class FundController {

    private final FundExcelService fundExcelService;

    @PostMapping(value = "/import-and-index", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importAndIndex(@RequestParam("file") MultipartFile file) {
        FundImportResult result = fundExcelService.importAndIndexAsync(file);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "accepted");
        response.put("stats", Map.of(
                "total", result.total(),
                "success", result.success(),
                "failed", result.failed()
        ));
        response.put("savedCodes", result.savedFundCodes());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}