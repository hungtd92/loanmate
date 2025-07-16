package com.hungtd.loanmate.dto;

import lombok.Data;

@Data
public class PrepaymentRequestDto {
    private String loanId;
    private int prepayMonthIndex; // người dùng muốn tất toán vào tháng thứ mấy
}

