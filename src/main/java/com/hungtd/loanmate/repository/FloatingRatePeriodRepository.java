package com.hungtd.loanmate.repository;

import com.hungtd.loanmate.entity.FloatingRatePeriodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FloatingRatePeriodRepository extends JpaRepository<FloatingRatePeriodEntity, Long> {
    List<FloatingRatePeriodEntity> findByLoanId(String loanId);
}

