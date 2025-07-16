package com.hungtd.loanmate.entity;

import com.hungtd.loanmate.model.RepaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_calculation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanCalculationEntity {
    @Id
    private String id;
    private String userId;
    private Long amount;
    private Integer termMonths;
    private BigDecimal fixedInterestRate;

    @Enumerated(EnumType.STRING)
    private RepaymentMethod method;

    private Long totalInterest;
    private Long totalPayable;

    @Column(name = "prepayment_penalty_rate")
    private BigDecimal prepaymentPenaltyRate; // % phí phạt trả trước nếu tất toán sớm (VD: 2%)

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
