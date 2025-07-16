package com.hungtd.loanmate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "floating_rate_period")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloatingRatePeriodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer fromMonth;
    private Integer toMonth;

    @Column(precision = 5, scale = 2)
    private BigDecimal annualRate;

    private String loanId;

}

