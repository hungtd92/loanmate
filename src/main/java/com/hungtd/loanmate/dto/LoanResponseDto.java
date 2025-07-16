package com.hungtd.loanmate.dto;

import com.hungtd.loanmate.model.MonthlyPaymentModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoanResponseDto {
    private Long totalInterest;
    private Long totalPayable;
    private List<MonthlyPaymentModel> schedule;
}
