package com.hungtd.loanmate.service;

import com.hungtd.loanmate.common.context.LogContext;
import com.hungtd.loanmate.common.exception.NotFoundException;
import com.hungtd.loanmate.config.PrepaymentConfig;
import com.hungtd.loanmate.dto.FloatingRatePeriod;
import com.hungtd.loanmate.dto.LoanRequestDto;
import com.hungtd.loanmate.dto.LoanResponseDto;
import com.hungtd.loanmate.dto.PrepaymentResultDto;
import com.hungtd.loanmate.entity.FloatingRatePeriodEntity;
import com.hungtd.loanmate.entity.LoanCalculationEntity;
import com.hungtd.loanmate.entity.MonthlyPaymentEntity;
import com.hungtd.loanmate.mapper.MonthlyPaymentMapper;
import com.hungtd.loanmate.model.LoanCalculationModel;
import com.hungtd.loanmate.model.MonthlyPaymentModel;
import com.hungtd.loanmate.model.RepaymentMethod;
import com.hungtd.loanmate.repository.FloatingRatePeriodRepository;
import com.hungtd.loanmate.repository.LoanCalculationRepository;
import com.hungtd.loanmate.repository.MonthlyPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanCalculatorService {
    private final LoanCalculationRepository loanRepository;
    private final FloatingRatePeriodRepository floatingRatePeriodRepository;
    private final MonthlyPaymentRepository monthlyPaymentRepository;
    private final MonthlyPaymentService monthlyPaymentService;
    private final MonthlyPaymentMapper monthlyPaymentMapper;
    private final PrepaymentConfig prepaymentConfig;

    /**
     * Dịch vụ tính toán lịch trả nợ cho khoản vay dựa trên yêu cầu đầu vào.
     * Hỗ trợ 2 phương thức trả nợ:
     * - FLAT (trả góp đều theo dư nợ gốc)
     * - REDUCING (trả góp theo dư nợ giảm dần)
     *
     * @param req thông tin khoản vay: số tiền, kỳ hạn, lãi suất, phương thức
     * @return kết quả bao gồm tổng lãi, tổng tiền phải trả, và lịch thanh toán từng tháng
     */
    public LoanResponseDto calculate(LoanRequestDto req) {
        try {
            List<MonthlyPaymentModel> schedule = new ArrayList<>();

            BigDecimal totalInterest = BigDecimal.ZERO;

            BigDecimal amount = BigDecimal.valueOf(req.getAmount());

            LocalDate startDate = LocalDate.now().plusMonths(1);

            String loanId = UUID.randomUUID().toString();

            if (req.isFloating()) {
                BigDecimal goc = amount.divide(BigDecimal.valueOf(req.getTermMonths()), 10, RoundingMode.HALF_UP);

                for (int i = 1; i <= req.getTermMonths(); i++) {
                    final int monthIndex = i; // để sử dụng trong lambda
                    // Tìm lãi suất phù hợp cho tháng hiện tại
                    BigDecimal annualRate = req.getFloatingRates().stream()
                            .filter(rate -> monthIndex >= rate.getFromMonth() && monthIndex <= rate.getToMonth())
                            .map(FloatingRatePeriod::getAnnualRate)
                            .findFirst()
                            .orElse(BigDecimal.ZERO); // fallback nếu không tìm thấy

                    BigDecimal r = annualRate.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
                    BigDecimal duNoConLai = amount.subtract(goc.multiply(BigDecimal.valueOf(i - 1)));
                    BigDecimal interest = duNoConLai.multiply(r);
                    BigDecimal total = goc.add(interest);

                    totalInterest = totalInterest.add(interest);
                    schedule.add(new MonthlyPaymentModel(loanId, i, goc.longValue(), interest.longValue(), total.longValue(), startDate.plusMonths(i - 1)));
                }
            } else {
                // Tính lãi suất tháng từ lãi suất năm (ví dụ: 14.4% → 0.012 = 1.2%)
                BigDecimal r = req.getFixedInterestRate().divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);

                // ----------------------------------------
                // PHƯƠNG THỨC 1: FLAT (trả góp cố định hàng tháng)
                // ----------------------------------------
                if (req.getMethod() == RepaymentMethod.FLAT) {
                    // Tính số tiền trả mỗi tháng cố định theo công thức annuity
                    BigDecimal monthlyPayment = amount.multiply(r)
                            .multiply((BigDecimal.ONE.add(r)).pow(req.getTermMonths()))
                            .divide((BigDecimal.ONE.add(r)).pow(req.getTermMonths()).subtract(BigDecimal.ONE), 10, RoundingMode.HALF_UP);

                    for (int i = 1; i <= req.getTermMonths(); i++) {
                        // Lãi hàng tháng cố định = dư nợ ban đầu * lãi suất tháng
                        BigDecimal interest = amount.multiply(r);
                        BigDecimal principal = monthlyPayment.subtract(interest);
                        totalInterest = totalInterest.add(interest);

                        schedule.add(new MonthlyPaymentModel(
                                loanId,
                                i,
                                principal.longValue(),
                                interest.longValue(),
                                monthlyPayment.longValue(),
                                startDate.plusMonths(i - 1)
                        ));
                    }

                    // ----------------------------------------
                    // PHƯƠNG THỨC 2: REDUCING (dư nợ giảm dần)
                    // ----------------------------------------
                } else if (req.getMethod() == RepaymentMethod.REDUCING) {
                    // Gốc chia đều hàng tháng
                    BigDecimal goc = amount.divide(BigDecimal.valueOf(req.getTermMonths()), 10, RoundingMode.HALF_UP);

                    for (int i = 1; i <= req.getTermMonths(); i++) {
                        // Dư nợ còn lại = số tiền vay - gốc đã trả
                        BigDecimal duNoConLai = amount.subtract(goc.multiply(BigDecimal.valueOf(i - 1)));

                        // Lãi = dư nợ còn lại * lãi suất tháng
                        BigDecimal interest = duNoConLai.multiply(r);
                        BigDecimal total = goc.add(interest);

                        totalInterest = totalInterest.add(interest);

                        schedule.add(new MonthlyPaymentModel(
                                loanId,
                                i,
                                goc.longValue(),
                                interest.longValue(),
                                total.longValue(),
                                startDate.plusMonths(i - 1)
                        ));
                    }
                }
            }

            LoanCalculationEntity entity = LoanCalculationEntity.builder()
                    .id(loanId)
                    .userId(LogContext.get().getUserId())
                    .amount(req.getAmount())
                    .termMonths(req.getTermMonths())
                    .fixedInterestRate(req.isFloating() ? null : req.getFixedInterestRate())
                    .method(req.getMethod())
                    .totalInterest(totalInterest.longValue())
                    .totalPayable(req.getAmount() + totalInterest.longValue())
                    .createdAt(LocalDateTime.now())
                    .prepaymentPenaltyRate(req.getPrepaymentPenaltyRate() != null
                            ? req.getPrepaymentPenaltyRate()
                            : prepaymentConfig.getPenaltyRateDefault())
                    .build();
            loanRepository.save(entity);

            List<MonthlyPaymentEntity> monthlyPayments = monthlyPaymentMapper.toEntity(schedule);
            monthlyPaymentService.saveAll(monthlyPayments);

            if (req.isFloating() && req.getFloatingRates() != null) {
                List<FloatingRatePeriodEntity> rates = req.getFloatingRates().stream()
                        .map(dto -> {
                            FloatingRatePeriodEntity rate = new FloatingRatePeriodEntity();
                            rate.setFromMonth(dto.getFromMonth());
                            rate.setToMonth(dto.getToMonth());
                            rate.setAnnualRate(dto.getAnnualRate());
                            rate.setLoanId(loanId); // gắn cha
                            return rate;
                        })
                        .collect(Collectors.toList());

                saveAllFloatingRatePeriodEntity(rates);
            }

            return new LoanResponseDto(entity.getTotalInterest(), entity.getTotalPayable(), schedule);
        } catch (Exception e) {
            // Log lỗi và ném ra ngoại lệ để xử lý ở tầng trên
            log.error("Calculate Loan error: " + e.getMessage(), e);
            throw new RuntimeException("Calculate Loan error", e);
        }
    }

    public PrepaymentResultDto calculatePrepayment(String loanId, int prepayMonthIndex) {
        try {
            LoanCalculationEntity loan = loanRepository.findById(loanId)
                    .orElseThrow(() -> new NotFoundException("Loan not found"));

            List<MonthlyPaymentEntity> payments = monthlyPaymentRepository.findByLoanId(loanId);

            // Tổng tiền đã trả đến kỳ trước
            long paidPrincipal = payments.stream()
                    .filter(p -> p.getMonthIndex() < prepayMonthIndex)
                    .mapToLong(MonthlyPaymentEntity::getPrincipal)
                    .sum();

            long paidInterest = payments.stream()
                    .filter(p -> p.getMonthIndex() < prepayMonthIndex)
                    .mapToLong(MonthlyPaymentEntity::getInterest)
                    .sum();

            long remainingPrincipal = loan.getAmount().longValue() - paidPrincipal;

            long unpaidInterest = payments.stream()
                    .filter(p -> p.getMonthIndex() >= prepayMonthIndex)
                    .mapToLong(MonthlyPaymentEntity::getInterest)
                    .sum();

            long interestSaved = unpaidInterest;

            BigDecimal penaltyRate = loan.getPrepaymentPenaltyRate() != null
                    ? loan.getPrepaymentPenaltyRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            long prepaymentPenalty = BigDecimal.valueOf(remainingPrincipal)
                    .multiply(penaltyRate)
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValue();

            long totalPayableIfPrepaid = remainingPrincipal + prepaymentPenalty;

            return new PrepaymentResultDto(
                    remainingPrincipal,
                    unpaidInterest,
                    prepaymentPenalty,
                    totalPayableIfPrepaid,
                    interestSaved
            );
        } catch (Exception e) {
            log.error("Calculate prepayment error: " + e.getMessage(), e);
            throw new RuntimeException("Calculate prepayment error", e);
        }
    }

    public LoanCalculationModel getById(String loanId) {
        LoanCalculationEntity entity = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanId));
        return new LoanCalculationModel(
                entity.getId(),
                entity.getUserId(),
                entity.getAmount(),
                entity.getTermMonths(),
                entity.getFixedInterestRate(),
                entity.getMethod(),
                entity.getTotalInterest(),
                entity.getTotalPayable()
        );
    }

    public void saveAllFloatingRatePeriodEntity(List<FloatingRatePeriodEntity> payments) {
        floatingRatePeriodRepository.saveAll(payments);
    }

}
