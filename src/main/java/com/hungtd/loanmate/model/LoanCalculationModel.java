package com.hungtd.loanmate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanCalculationModel {
    private String id;
    private String userId;
    private Long amount;
    private Integer termMonths;
    private BigDecimal annualInterestRate;
    private RepaymentMethod method;
    private Long totalInterest;
    private Long totalPayable;
}
