package com.oneriver.entity.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.util.Map;

@Document(indexName = "funds")
@Data
@Builder
public class FundDocument {
    @Id
    private String id;
    private String fundCode;
    private String fundName;
    private String umbrellaFundType;
    private Map<String, BigDecimal> returnPeriods;
}
