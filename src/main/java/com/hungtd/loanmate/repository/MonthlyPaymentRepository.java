package com.hungtd.loanmate.repository;

import com.hungtd.loanmate.entity.MonthlyPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlyPaymentRepository extends JpaRepository<MonthlyPaymentEntity, String> {
    List<MonthlyPaymentEntity> findByLoanId(String loanId);
}
