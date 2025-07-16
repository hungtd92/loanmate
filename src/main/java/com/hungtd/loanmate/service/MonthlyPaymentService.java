package com.hungtd.loanmate.service;

import com.hungtd.loanmate.entity.MonthlyPaymentEntity;
import com.hungtd.loanmate.mapper.MonthlyPaymentMapper;
import com.hungtd.loanmate.model.MonthlyPaymentModel;
import com.hungtd.loanmate.repository.MonthlyPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonthlyPaymentService {
    private final MonthlyPaymentRepository repository;
    private final MonthlyPaymentMapper monthlyPaymentMapper;

    public List<MonthlyPaymentModel> getPaymentsByLoanId(String loanId) {
        return monthlyPaymentMapper.toModel(repository.findByLoanId(loanId));
    }

    public void saveAll(List<MonthlyPaymentEntity> payments) {
        repository.saveAll(payments);
    }
}

