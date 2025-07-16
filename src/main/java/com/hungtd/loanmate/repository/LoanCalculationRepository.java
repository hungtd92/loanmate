package com.hungtd.loanmate.repository;

import com.hungtd.loanmate.entity.LoanCalculationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanCalculationRepository extends JpaRepository<LoanCalculationEntity, String> {
    List<LoanCalculationEntity> findByUserId(String userId);
}
