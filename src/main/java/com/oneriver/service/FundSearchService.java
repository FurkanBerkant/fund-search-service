package com.oneriver.service;

import com.oneriver.dto.FundSearchResponse;
import com.oneriver.entity.document.FundDocument;
import com.oneriver.mapper.FundMapper;
import com.oneriver.utils.FundConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FundSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final FundMapper fundMapper;

    public SearchHits<FundDocument> searchFunds(String query, String umbrellaType,
                                                String returnPeriod, Double minReturn, Double maxReturn,
                                                String sortBy, String sortDirection, Pageable pageable) {

        NativeQueryBuilder queryBuilder = NativeQuery.builder();

        queryBuilder.withQuery(q -> q.bool(b -> {
            if (query != null && !query.isBlank()) {
                b.must(m -> m.bool(sb -> sb
                        .should(s -> s.match(ma -> ma.field(FundConstants.ES_FIELD_FUND_CODE)
                                .query(query).fuzziness("AUTO")))
                        .should(s -> s.match(ma -> ma.field(FundConstants.ES_FIELD_FUND_NAME)
                                .query(query).fuzziness("AUTO")))
                ));
            }

            if (umbrellaType != null && !umbrellaType.isBlank()) {
                b.filter(f -> f.term(t -> t.field(FundConstants.ES_FIELD_UMBRELLA_FUND_TYPE).value(umbrellaType)));
            }

            if ((minReturn != null || maxReturn != null) && returnPeriod != null) {
                String fieldName = mapReturnPeriodField(returnPeriod);
                final Double min = minReturn;
                final Double max = maxReturn;
                b.filter(f -> f.range(r -> r
                        .number(n -> {
                            n.field(fieldName);
                            if (min != null) n.gte(min);
                            if (max != null) n.lte(max);
                            return n;
                        })
                ));
            }

            return b;
        }));

        queryBuilder.withPageable(pageable);
        if (sortBy != null && !sortBy.isBlank()) {
            Sort.Direction dir = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
            queryBuilder.withSort(Sort.by(dir, mapSortField(sortBy)));
        }

        return elasticsearchOperations.search(queryBuilder.build(), FundDocument.class);
    }

    public List<FundSearchResponse> toResponseList(SearchHits<FundDocument> searchHits) {
        return searchHits.stream()
                .map(SearchHit::getContent)
                .map(fundMapper::toSearchResponse)
                .toList();
    }

    private String mapSortField(String sortBy) {
        String lowerSortBy = sortBy.toLowerCase();

        return switch (lowerSortBy) {
            case FundConstants.SORT_ONE_YEAR -> FundConstants.ES_FIELD_ONE_YEAR;
            case FundConstants.SORT_ONE_MONTH -> FundConstants.ES_FIELD_ONE_MONTH;
            case FundConstants.SORT_THREE_MONTHS -> FundConstants.ES_FIELD_THREE_MONTHS;
            case FundConstants.SORT_SIX_MONTHS -> FundConstants.ES_FIELD_SIX_MONTHS;
            case FundConstants.SORT_YEAR_CHANGE, FundConstants.SORT_YTD -> FundConstants.ES_FIELD_YEAR_TO_DATE;
            case FundConstants.SORT_THREE_YEARS -> FundConstants.ES_FIELD_THREE_YEARS;
            case FundConstants.SORT_FIVE_YEARS -> FundConstants.ES_FIELD_FIVE_YEARS;
            case FundConstants.SORT_FUND_CODE -> FundConstants.ES_FIELD_FUND_CODE;
            case FundConstants.SORT_FUND_NAME -> FundConstants.ES_FIELD_FUND_NAME_KEYWORD;
            case FundConstants.SORT_UMBRELLA_FUND_TYPE -> FundConstants.ES_FIELD_UMBRELLA_FUND_TYPE;
            default -> sortBy;
        };
    }

    private String mapReturnPeriodField(String period) {
        if (period == null) return FundConstants.ES_FIELD_ONE_YEAR;

        return switch (period.toLowerCase()) {
            case "onemonth", "1month" -> FundConstants.ES_FIELD_ONE_MONTH;
            case "threemonths", "3months" -> FundConstants.ES_FIELD_THREE_MONTHS;
            case "sixmonths", "6months" -> FundConstants.ES_FIELD_SIX_MONTHS;
            case "yeartodate", "ytd" -> FundConstants.ES_FIELD_YEAR_TO_DATE;
            case "oneyear", "1year" -> FundConstants.ES_FIELD_ONE_YEAR;
            case "threeyears", "3years" -> FundConstants.ES_FIELD_THREE_YEARS;
            case "fiveyears", "5years" -> FundConstants.ES_FIELD_FIVE_YEARS;
            default -> FundConstants.ES_FIELD_ONE_YEAR;
        };
    }
}