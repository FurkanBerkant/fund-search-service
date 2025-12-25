package com.oneriver.service;

import com.oneriver.dto.FundSearchResponse;
import com.oneriver.entity.document.FundDocument;
import com.oneriver.mapper.FundMapper;
import com.oneriver.utils.FundConstants;
import com.oneriver.enums.SortField;
import com.oneriver.enums.ReturnPeriodType;
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
                String normalizedQuery = query.trim();

                return b.must(m -> m.bool(sb -> {
                    sb.should(s -> s.term(t -> t
                            .field(FundConstants.ES_FIELD_FUND_CODE)
                            .value(normalizedQuery.toUpperCase())
                            .boost(10.0f)));
                    sb.should(s -> s.prefix(p -> p
                            .field(FundConstants.ES_FIELD_FUND_CODE)
                            .value(normalizedQuery.toUpperCase())
                            .boost(5.0f)));
                    sb.should(s -> s.match(ma -> ma
                            .field(FundConstants.ES_FIELD_FUND_NAME)
                            .query(normalizedQuery)
                            .fuzziness("AUTO")
                            .boost(2.0f)));
                    sb.should(s -> s.match(ma -> ma
                            .field(FundConstants.ES_FIELD_UMBRELLA_FUND_TYPE)
                            .query(normalizedQuery)
                            .boost(1.0f)));

                    sb.minimumShouldMatch("1");
                    return sb;
                }));
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
        return SortField.fromString(sortBy)
                .map(SortField::getEsField)
                .orElse(sortBy);
    }

    private String mapReturnPeriodField(String period) {
        return ReturnPeriodType.fromString(period)
                .map(ReturnPeriodType::getEsField)
                .orElse(FundConstants.ES_FIELD_ONE_YEAR);
    }
}