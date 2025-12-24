package com.oneriver.mapper;

import com.oneriver.dto.ExcelFundRowDTO;
import com.oneriver.dto.FundSearchResponse;
import com.oneriver.entity.Fund;
import com.oneriver.entity.ReturnPeriods;
import com.oneriver.entity.document.FundDocument;
import com.oneriver.utils.NumberUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Mapper(componentModel = "spring", imports = {NumberUtils.class})
public interface FundMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fundCode", expression = "java(dto.getFundCode().trim().toUpperCase())")
    @Mapping(target = "fundType", source = "umbrellaFundType")
    @Mapping(target = "returnPeriods.oneMonth", source = "oneMonth", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.threeMonths", source = "threeMonth", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.sixMonths", source = "sixMonth", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.yearToDate", source = "yearToDate", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.oneYear", source = "oneYear", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.threeYears", source = "threeYear", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.fiveYears", source = "fiveYear", qualifiedByName = "parsePct")
    Fund toEntity(ExcelFundRowDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fundCode", ignore = true)
    @Mapping(target = "fundType", source = "umbrellaFundType")
    @Mapping(target = "returnPeriods.oneMonth", source = "oneMonth", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.threeMonths", source = "threeMonth", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.sixMonths", source = "sixMonth", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.yearToDate", source = "yearToDate", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.oneYear", source = "oneYear", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.threeYears", source = "threeYear", qualifiedByName = "parsePct")
    @Mapping(target = "returnPeriods.fiveYears", source = "fiveYear", qualifiedByName = "parsePct")
    void updateEntity(@MappingTarget Fund existingFund, ExcelFundRowDTO dto);

    @Mapping(target = "id", source = "fundCode")
    @Mapping(target = "umbrellaFundType", source = "fundType")
    @Mapping(target = "returnPeriods", source = "returnPeriods", qualifiedByName = "mapReturnPeriods")
    FundDocument toDocument(Fund fund);

    FundSearchResponse toSearchResponse(FundDocument document);

    @Named("parsePct")
    default BigDecimal parsePct(String value) {
        return NumberUtils.parsePercentage(value);
    }

    @Named("mapReturnPeriods")
    default Map<String, BigDecimal> mapReturnPeriods(ReturnPeriods r) {
        Map<String, BigDecimal> map = new HashMap<>();

        if (r == null) {
            map.put("oneMonth", BigDecimal.ZERO);
            map.put("threeMonths", BigDecimal.ZERO);
            map.put("sixMonths", BigDecimal.ZERO);
            map.put("yearToDate", BigDecimal.ZERO);
            map.put("oneYear", BigDecimal.ZERO);
            map.put("threeYears", BigDecimal.ZERO);
            map.put("fiveYears", BigDecimal.ZERO);
            return Collections.unmodifiableMap(map);
        }

        map.put("oneMonth", r.getOneMonth() != null ? r.getOneMonth() : BigDecimal.ZERO);
        map.put("threeMonths", r.getThreeMonths() != null ? r.getThreeMonths() : BigDecimal.ZERO);
        map.put("sixMonths", r.getSixMonths() != null ? r.getSixMonths() : BigDecimal.ZERO);
        map.put("yearToDate", r.getYearToDate() != null ? r.getYearToDate() : BigDecimal.ZERO);
        map.put("oneYear", r.getOneYear() != null ? r.getOneYear() : BigDecimal.ZERO);
        map.put("threeYears", r.getThreeYears() != null ? r.getThreeYears() : BigDecimal.ZERO);
        map.put("fiveYears", r.getFiveYears() != null ? r.getFiveYears() : BigDecimal.ZERO);

        return Collections.unmodifiableMap(map);
    }
}