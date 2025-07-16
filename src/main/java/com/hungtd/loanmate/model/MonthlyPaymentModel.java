package com.hungtd.loanmate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyPaymentModel {
    private String loanId;
    private int monthIndex;
    private long principal;
    private long interest;
    private long totalPayment;
    private LocalDate paymentDate;
}
