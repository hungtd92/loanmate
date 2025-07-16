package com.hungtd.loanmate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "monthly_payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyPaymentEntity {
    @Id
    private String id;
    private String loanId;
    private Integer monthIndex;
    private Long principal;
    private Long interest;
    private Long totalPayment;
    private LocalDate paymentDate;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
    }
}
