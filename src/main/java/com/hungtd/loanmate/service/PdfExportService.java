package com.hungtd.loanmate.service;

import com.hungtd.loanmate.common.exception.PdfExportException;
import com.hungtd.loanmate.entity.LoanCalculationEntity;
import com.hungtd.loanmate.entity.MonthlyPaymentEntity;
import com.hungtd.loanmate.repository.LoanCalculationRepository;
import com.hungtd.loanmate.repository.MonthlyPaymentRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PdfExportService {
    private final MonthlyPaymentRepository monthlyPaymentRepository;
    private final LoanCalculationRepository loanCalculationRepository;

    private static final NumberFormat VND_FORMAT =
            NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final SpringTemplateEngine templateEngine;

    public byte[] exportScheduleToPdf(LoanCalculationEntity loan, List<MonthlyPaymentEntity> payments) {
        try {
            // Tính tổng
            long totalPrincipal = payments.stream().mapToLong(MonthlyPaymentEntity::getPrincipal).sum();
            long totalInterest = payments.stream().mapToLong(MonthlyPaymentEntity::getInterest).sum();
            long totalAmount = payments.stream().mapToLong(MonthlyPaymentEntity::getTotalPayment).sum();

            // Chuẩn bị context Thymeleaf
            Context context = new Context();
            context.setVariable("amount", loan.getAmount());
            context.setVariable("termMonths", loan.getTermMonths());
            context.setVariable("interestRate", loan.getFixedInterestRate());
            context.setVariable("payments", payments);
            context.setVariable("totalPrincipal", totalPrincipal);
            context.setVariable("totalInterest", totalInterest);
            context.setVariable("totalAmount", totalAmount);

            String html = templateEngine.process("schedule", context);

            // Render PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.useFont(new File("src/main/resources/fonts/Roboto-Regular.ttf"), "Roboto");
            builder.toStream(baos);
            builder.run();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new PdfExportException("Không thể tạo PDF từ Thymeleaf template", e);
        }
    }

    public byte[] exportScheduleToPdf(String loanId) {
        Optional<LoanCalculationEntity> loanEntity = loanCalculationRepository.findById(loanId);
        if (loanEntity.isEmpty()) {
            throw new PdfExportException("Khoản vay không tồn tại.", null);
        }
        List<MonthlyPaymentEntity> schedule = monthlyPaymentRepository.findByLoanId(loanId);

        return exportScheduleToPdf(loanEntity.get(), schedule);
    }

//    public byte[] exportScheduleToPdf(List<MonthlyPaymentEntity> schedule, LoanCalculationEntity loan) {
//        try {
//            StringBuilder html = new StringBuilder();
//            html.append("<html><head><style>table { width:100%; border-collapse: collapse } th, td { border:1px solid #ccc; padding:5px; }</style></head><body>");
//            html.append("<h2>Lịch thanh toán khoản vay</h2>");
//            html.append("<p>Số tiền vay: ").append(loan.getAmount()).append(" VND</p>");
//            html.append("<p>Thời hạn: ").append(loan.getTermMonths()).append(" tháng</p>");
//            html.append("<p>Lãi suất: ").append(loan.getAnnualInterestRate()).append("%/năm</p>");
//
//            html.append("<table><thead><tr><th>Tháng</th><th>Gốc</th><th>Lãi</th><th>Tổng</th><th>Ngày thanh toán</th></tr></thead><tbody>");
//
//            for (MonthlyPaymentEntity m : schedule) {
//                html.append("<tr>")
//                        .append("<td>").append(m.getMonthIndex()).append("</td>")
//                        .append("<td>").append(format(m.getPrincipal())).append("</td>")
//                        .append("<td>").append(format(m.getInterest())).append("</td>")
//                        .append("<td>").append(format(m.getTotalPayment())).append("</td>")
//                        .append("<td>").append(m.getPaymentDate().format(DATE_FORMAT)).append("</td>")
//                        .append("</tr>");
//            }
//
//            html.append("</tbody></table></body></html>");
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            PdfRendererBuilder builder = new PdfRendererBuilder();
//            builder.withHtmlContent(html.toString(), null);
//            builder.toStream(baos);
//            builder.run();
//            return baos.toByteArray();
//        } catch (Exception e) {
//            // Ghi log lỗi đầy đủ
//            System.err.println("❌ Lỗi khi xuất PDF: " + e.getMessage());
//            e.printStackTrace();
//
//            // Ném lỗi rõ ràng cho controller bắt được
//            throw new PdfExportException("Không thể xuất file PDF cho khoản vay.", e);
//        }
//    }

    String format(long value) {
        return VND_FORMAT.format(value) + " VND";
    }
}
