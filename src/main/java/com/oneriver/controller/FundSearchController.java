package com.oneriver.controller;

import com.oneriver.dto.FundSearchResponse;
import com.oneriver.entity.document.FundDocument;
import com.oneriver.service.FundSearchService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funds")
@RequiredArgsConstructor
@Slf4j
@Validated
public class FundSearchController {

    private final FundSearchService fundSearchService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String umbrellaType,
            @RequestParam(required = false, defaultValue = "fundName") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        log.info("Search request - query: {}, umbrellaType: {}, page: {}, size: {}",
                query, umbrellaType, page, size);

        Pageable pageable = PageRequest.of(page, size);

        SearchHits<FundDocument> searchHits = fundSearchService.searchFunds(
                query, umbrellaType, sortBy, sortDirection, pageable);

        return buildSearchResponse(searchHits, page, size, Map.of());
    }

    @GetMapping("/by-umbrella/{type}")
    public ResponseEntity<Map<String, Object>> getByUmbrellaType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        log.info("Search by umbrella type: {}, page: {}, size: {}", type, page, size);

        Pageable pageable = PageRequest.of(page, size);

        SearchHits<FundDocument> searchHits = fundSearchService.searchFunds(
                null, type, null, null, pageable);

        Map<String, Object> response = buildSearchResponse(searchHits, page, size,
                Map.of("umbrellaType", type)).getBody();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-performers")
    public ResponseEntity<Map<String, Object>> getTopPerformers(
            @RequestParam(required = false, defaultValue = "oneYear") String period,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {

        log.info("Getting top performers for period: {}, limit: {}", period, limit);

        Pageable pageable = PageRequest.of(0, limit);

        SearchHits<FundDocument> searchHits = fundSearchService.searchFunds(
                null, null, period, "desc", pageable);

        List<FundSearchResponse> results = fundSearchService.toResponseList(searchHits);

        Map<String, Object> response = new HashMap<>();
        response.put("funds", results);
        response.put("period", period);
        response.put("count", results.size());

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> buildSearchResponse(
            SearchHits<FundDocument> searchHits,
            int page,
            int size,
            Map<String, Object> additionalInfo) {

        List<FundSearchResponse> results = fundSearchService.toResponseList(searchHits);

        Map<String, Object> response = new HashMap<>();
        response.put("funds", results);
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("totalElements", searchHits.getTotalHits());
        response.put("totalPages", (int) Math.ceil((double) searchHits.getTotalHits() / size));

        if (!additionalInfo.isEmpty()) {
            response.put("filters", additionalInfo);
        }

        log.info("Search completed - returned {} results out of {} total",
                results.size(), searchHits.getTotalHits());

        return ResponseEntity.ok(response);
    }
}