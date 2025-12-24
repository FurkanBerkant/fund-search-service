package com.oneriver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "funds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fund {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fund_seq")
    @SequenceGenerator(name = "fund_seq", sequenceName = "fund_seq")
    private Long id;

    @Column(unique = true, nullable = false)
    private String fundCode;

    @Column(nullable = false)
    private String fundName;

    private String fundType;

    @Embedded
    private ReturnPeriods returnPeriods;
}