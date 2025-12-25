package com.oneriver.entity.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.InnerField;

import java.math.BigDecimal;
import java.util.Map;

@Document(indexName = "funds")
@Data
@Builder
public class FundDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String fundCode;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "turkish"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword, ignoreAbove = 256)
            }
    )
    private String fundName;

    @Field(type = FieldType.Keyword)
    private String umbrellaFundType;

    @Field(type = FieldType.Object)
    private Map<String, BigDecimal> returnPeriods;
}
