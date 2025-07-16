package com.hungtd.loanmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyPaymentDto {
    private String id;
    private Integer monthIndex;
    private Long principal;
    private Long interest;
    private Long totalPayment;
    private LocalDate paymentDate;
}

