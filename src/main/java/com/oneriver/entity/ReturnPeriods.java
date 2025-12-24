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
    @Column(scale = 4)
    private BigDecimal oneMonth;

    @Column(scale = 4)
    private BigDecimal threeMonths;

    @Column(scale = 4)
    private BigDecimal sixMonths;

    @Column(scale = 4)
    private BigDecimal yearToDate;

    @Column(scale = 4)
    private BigDecimal oneYear;

    @Column(scale = 4)
    private BigDecimal threeYears;

    @Column(scale = 4)
    private BigDecimal fiveYears;
}