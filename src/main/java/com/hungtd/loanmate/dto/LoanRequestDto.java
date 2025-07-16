package com.hungtd.loanmate.dto;

import com.hungtd.loanmate.model.RepaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LoanRequestDto {
    private Long amount;
    private Integer termMonths;
    private RepaymentMethod method;

    private boolean floating; // true = lãi suất thả nổi
    private BigDecimal fixedInterestRate; // dùng nếu không thả nổi
    private List<FloatingRatePeriod> floatingRates; // danh sách lãi suất theo giai đoạn

    private BigDecimal prepaymentPenaltyRate;
}
