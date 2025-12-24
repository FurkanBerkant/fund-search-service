package com.oneriver.mapper;

import com.oneriver.dto.ExcelFundRowDTO;
import com.oneriver.dto.FundSearchResponse;
import com.oneriver.entity.Fund;
import com.oneriver.entity.ReturnPeriods;
import com.oneriver.entity.document.FundDocument;
import com.oneriver.utils.NumberUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Mapper(componentModel = "spring", imports = {NumberUtils.class})
public interface FundMapper {

    @Mapping(target = "fundCode", expression = "java(dto.getFundCode().trim().toUpperCase())")
    @Mapping(target = "returnPeriods.oneMonth", source = "oneMonth", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.threeMonths", source = "threeMonth", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.sixMonths", source = "sixMonth", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.yearToDate", source = "yearToDate", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.oneYear", source = "oneYear", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.threeYears", source = "threeYear", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.fiveYears", source = "fiveYear", qualifiedByName = "parsePct")
    Fund toEntity(ExcelFundRowDTO dto);

    @Mapping(target = "id", source = "fundCode")
    @Mapping(target = "returnPeriods", source = "returnPeriods", qualifiedByName = "mapReturnPeriods")
    FundDocument toDocument(Fund fund);

    FundSearchResponse toSearchResponse(FundDocument document);

    @Named("parsePct")
    default BigDecimal parsePct(String value) {
        return NumberUtils.parsePercentage(value);
    }

    @Named("mapReturnPeriods")
    default Map<String, BigDecimal> mapReturnPeriods(ReturnPeriods r) {
        if (r == null) return Map.of();
        Map<String, BigDecimal> map = new HashMap<>();
        if (r.getOneMonth() != null) map.put("oneMonth", r.getOneMonth());
        if (r.getThreeMonths() != null) map.put("threeMonths", r.getThreeMonths());
        if (r.getSixMonths() != null) map.put("sixMonths", r.getSixMonths());
        if (r.getYearToDate() != null) map.put("yearToDate", r.getYearToDate());
        if (r.getOneYear() != null) map.put("oneYear", r.getOneYear());
        if (r.getThreeYears() != null) map.put("threeYears", r.getThreeYears());
        if (r.getFiveYears() != null) map.put("fiveYears", r.getFiveYears());
        return map;
    }
}