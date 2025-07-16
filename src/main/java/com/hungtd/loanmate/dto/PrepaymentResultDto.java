package com.hungtd.loanmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrepaymentResultDto {
    private Long remainingPrincipal;
    private Long unpaidInterest;
    private Long prepaymentPenalty; // tiền phạt nếu có
    private Long totalPayableIfPrepaid;
    private Long interestSaved;
}
