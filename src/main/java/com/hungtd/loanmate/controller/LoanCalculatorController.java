package com.hungtd.loanmate.controller;

import com.hungtd.loanmate.common.response.Response;
import com.hungtd.loanmate.common.response.ResponseBuilder;
import com.hungtd.loanmate.dto.LoanRequestDto;
import com.hungtd.loanmate.dto.LoanResponseDto;
import com.hungtd.loanmate.dto.PrepaymentRequestDto;
import com.hungtd.loanmate.dto.PrepaymentResultDto;
import com.hungtd.loanmate.model.LoanCalculationModel;
import com.hungtd.loanmate.service.LoanCalculatorService;
import com.hungtd.loanmate.service.MonthlyPaymentService;
import com.hungtd.loanmate.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loan")
@RequiredArgsConstructor
public class LoanCalculatorController {
    private final LoanCalculatorService loanCalculatorService;
    private final PdfExportService pdfExportService;

    @PostMapping("/calculate")
    public ResponseEntity<Response<LoanResponseDto>> calculate(@RequestBody LoanRequestDto request) {
        return ResponseEntity.ok(ResponseBuilder.buildSuccessResponse(loanCalculatorService.calculate(request)));
    }

    @GetMapping("/export/{loanId}")
    public ResponseEntity<byte[]> exportToPdf(@PathVariable String loanId) {
        byte[] pdf = pdfExportService.exportScheduleToPdf(loanId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=loan_schedule.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/prepayment")
    public PrepaymentResultDto simulatePrepayment(@RequestBody PrepaymentRequestDto req) {
        return loanCalculatorService.calculatePrepayment(req.getLoanId(), req.getPrepayMonthIndex());
    }
}

