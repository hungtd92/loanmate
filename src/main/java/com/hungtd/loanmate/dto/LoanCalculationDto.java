package com.hungtd.loanmate.dto;

import com.hungtd.loanmate.model.RepaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanCalculationDto {
    private String id;
    private Long amount;
    private Integer termMonths;
    private BigDecimal annualInterestRate;
    private RepaymentMethod method;
    private Long totalInterest;
    private Long totalPayable;
    private LocalDateTime createdAt;
}

