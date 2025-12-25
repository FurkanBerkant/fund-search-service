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
        if (r == null) {
            return createEmptyReturnPeriodsMap();
        }

        return Map.of(
                "oneMonth", getOrZero(r.getOneMonth()),
                "threeMonths", getOrZero(r.getThreeMonths()),
                "sixMonths", getOrZero(r.getSixMonths()),
                "yearToDate", getOrZero(r.getYearToDate()),
                "oneYear", getOrZero(r.getOneYear()),
                "threeYears", getOrZero(r.getThreeYears()),
                "fiveYears", getOrZero(r.getFiveYears())
        );
    }

    private BigDecimal getOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private Map<String, BigDecimal> createEmptyReturnPeriodsMap() {
        return Map.of(
                "oneMonth", BigDecimal.ZERO,
                "threeMonths", BigDecimal.ZERO,
                "sixMonths", BigDecimal.ZERO,
                "yearToDate", BigDecimal.ZERO,
                "oneYear", BigDecimal.ZERO,
                "threeYears", BigDecimal.ZERO,
                "fiveYears", BigDecimal.ZERO
        );
    }
}