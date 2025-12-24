package com.oneriver.service;

import com.oneriver.dto.ExcelFundRowDTO;
import com.oneriver.dto.FundImportResult;
import com.oneriver.entity.Fund;
import com.oneriver.mapper.FundMapper;
import com.oneriver.repository.FundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class FundImportService {
    private final FundMapper fundMapper;
    private final FundRepository fundRepository;

    @Transactional
    public FundImportResult process(List<ExcelFundRowDTO> rows) {
        if (rows == null || rows.isEmpty()) {
            return new FundImportResult(0, 0, 0, List.of(), List.of());
        }

        List<Fund> toSave = rows.stream()
                .map(fundMapper::toEntity)
                .filter(Objects::nonNull)
                .toList();

        try {
            List<Fund> savedFunds = fundRepository.saveAll(toSave);
            List<String> savedCodes = savedFunds.stream().map(Fund::getFundCode).toList();

            return new FundImportResult(
                    rows.size(),
                    savedFunds.size(),
                    rows.size() - savedFunds.size(),
                    savedCodes,
                    List.of()
            );
        } catch (Exception e) {
            log.error("DB Save Error: ", e);
            throw new RuntimeException("Database error during fund import", e);
        }
    }
}