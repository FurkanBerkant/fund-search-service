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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        Map<String, Fund> existingFundsMap = fetchExistingFunds(rows);
        List<Fund> toSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (ExcelFundRowDTO dto : rows) {
            if (dto.getFundCode() == null || dto.getFundCode().isBlank()) {
                errors.add("Empty fund code - skipped");
                continue;
            }

            try {
                String normalizedCode = dto.getFundCode().trim().toUpperCase();
                Fund existingFund = existingFundsMap.get(normalizedCode);

                if (existingFund != null) {
                    fundMapper.updateEntity(existingFund, dto);
                    toSave.add(existingFund);
                } else {
                    Fund newFund = fundMapper.toEntity(dto);
                    toSave.add(newFund);
                }
            } catch (Exception e) {
                String errorMsg = String.format("Error processing fund code '%s': %s", dto.getFundCode(), e.getMessage());
                log.warn(errorMsg, e);
                errors.add(errorMsg);
            }
        }

        List<Fund> savedFunds = fundRepository.saveAll(toSave);
        return new FundImportResult(
                rows.size(),
                savedFunds.size(),
                errors.size(),
                savedFunds.stream().map(Fund::getFundCode).toList(),
                errors
        );
    }

    private Map<String, Fund> fetchExistingFunds(List<ExcelFundRowDTO> rows) {
        List<String> codes = rows.stream()
                .map(ExcelFundRowDTO::getFundCode)
                .filter(StringUtils::hasText)
                .map(code -> code.trim().toUpperCase())
                .toList();

        return fundRepository.findAllByFundCodeIn(codes)
                .stream()
                .collect(Collectors.toMap(Fund::getFundCode, Function.identity()));
    }
}