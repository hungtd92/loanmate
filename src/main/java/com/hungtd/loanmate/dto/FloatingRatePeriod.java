package com.hungtd.loanmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FloatingRatePeriod {
    private int fromMonth; // bắt đầu từ tháng mấy
    private int toMonth;   // đến tháng mấy
    private BigDecimal annualRate; // %/năm
}

