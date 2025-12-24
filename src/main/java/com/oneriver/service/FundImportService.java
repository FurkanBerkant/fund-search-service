package com.oneriver.service;

import com.oneriver.entity.Fund;
import com.oneriver.entity.ReturnPeriods;
import com.oneriver.excel.dto.ExcelFunRowDTO;
import com.oneriver.excel.dto.FundImportResponse;
import com.oneriver.repository.FundRepository;
import com.oneriver.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FundImportService {

    private final FundRepository fundRepository;

    @Transactional
    public FundImportResponse process(List<ExcelFunRowDTO> rows) {
        if (rows == null || rows.isEmpty()) {
            return new FundImportResponse(0, 0, 0);
        }

        long start = System.currentTimeMillis();

        List<Fund> toSave = new ArrayList<>(rows.size());
        int mappingErrors = 0;

        for (ExcelFunRowDTO dto : rows) {
            try {
                Fund fund = mapToEntity(dto);
                if (fund != null) {
                    toSave.add(fund);
                }
            } catch (Exception e) {
                mappingErrors++;
                log.warn("Row mapping failed: {}", e.getMessage());
            }
        }

        int saved = 0;
        try {
            if (!toSave.isEmpty()) {
                List<Fund> savedFunds = fundRepository.saveAll(toSave);
                saved = savedFunds.size();
            }
        } catch (Exception e) {
            log.error("Failed to save funds", e);
            return FundImportResponse.builder()
                    .total(rows.size())
                    .success(0)
                    .failed(rows.size())
                    .build();
        }

        long elapsed = System.currentTimeMillis() - start;
        log.info("Import completed in {} ms. Parsed: {}, Saved: {}, Failed: {}",
                elapsed, rows.size(), saved, mappingErrors);

        return FundImportResponse.builder()
                .total(rows.size())
                .success(saved)
                .failed(rows.size() - saved)
                .build();
    }

    private Fund mapToEntity(ExcelFunRowDTO dto) {
        if (dto.getFundCode() == null || dto.getFundCode().isBlank()) {
            return null;
        }

        ReturnPeriods returns = ReturnPeriods.builder()
                .oneMonth(NumberUtils.parsePercentage(dto.getOneMonth()))
                .threeMonths(NumberUtils.parsePercentage(dto.getThreeMonth()))
                .sixMonths(NumberUtils.parsePercentage(dto.getSixMonth()))
                .yearToDate(NumberUtils.parsePercentage(dto.getYearToDate()))
                .oneYear(NumberUtils.parsePercentage(dto.getOneYear()))
                .threeYears(NumberUtils.parsePercentage(dto.getThreeYear()))
                .fiveYears(NumberUtils.parsePercentage(dto.getFiveYear()))
                .build();

        return Fund.builder()
                .fundCode(dto.getFundCode().trim().toUpperCase())
                .fundName(dto.getFundName())
                .umbrellaFundType(dto.getUmbrellaFundType())
                .returnPeriods(returns)
                .build();
    }
}