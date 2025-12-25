package com.oneriver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReturnPeriods {
    @Column(precision = 10, scale = 4)
    private BigDecimal oneMonth;

    @Column(precision = 10, scale = 4)
    private BigDecimal threeMonths;

    @Column(precision = 10, scale = 4)
    private BigDecimal sixMonths;

    @Column(precision = 10, scale = 4)
    private BigDecimal yearToDate;

    @Column(precision = 10, scale = 4)
    private BigDecimal oneYear;

    @Column(precision = 10, scale = 4)
    private BigDecimal threeYears;

    @Column(precision = 10, scale = 4)
    private BigDecimal fiveYears;
}