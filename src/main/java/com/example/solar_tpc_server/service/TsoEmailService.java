package com.example.solar_tpc_server.service;

import com.example.solar_tpc_server.dto.TsoCustomerRequestDto;
import com.example.solar_tpc_server.entity.TsoEmailConfig;
import com.example.solar_tpc_server.repository.TsoEmailConfigRepository;
import com.example.solar_tpc_server.util.TsoEmailConstant;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Service gửi email thông báo cho khách hàng sau khi submit yêu cầu tư vấn.
 * <p>
 * Cấu hình SMTP được đọc động từ bảng tso_email_config theo config_key,
 * logo chữ ký được đính kèm inline từ resources/static/images/logo_tpc.png.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TsoEmailService {

    // Các hằng số email được tập trung tại TsoEmailConstant

    private final TsoEmailConfigRepository emailConfigRepository;
    private final SpringTemplateEngine     templateEngine;

    /**
     * Gửi email thông báo xác nhận yêu cầu tư vấn tới khách hàng (bất đồng bộ).
     * Nếu khách hàng không cung cấp email thì bỏ qua, không ném exception.
     *
     * @param requestDto dữ liệu yêu cầu khách hàng
     */
    @Async("emailTaskExecutor")
    public void sendCustomerRequestNotify(TsoCustomerRequestDto requestDto) {
        // Bỏ qua nếu khách hàng không có email
        if (requestDto.getCustomerEmail() == null || requestDto.getCustomerEmail().isBlank()) {
            log.debug("[TsoEmailService] Khách hàng không cung cấp email, bỏ qua gửi email.");
            return;
        }

        // Lấy cấu hình email từ DB
        TsoEmailConfig config = emailConfigRepository
                .findByConfigKeyAndIsActive(TsoEmailConstant.CONFIG_KEY_CUSTOMER_REQUEST_NOTIFY, TsoEmailConstant.IS_ACTIVE)
                .orElse(null);

        if (config == null) {
            log.warn("[TsoEmailService] Không tìm thấy cấu hình email với key='{}' hoặc config đã bị tắt.",
                    TsoEmailConstant.CONFIG_KEY_CUSTOMER_REQUEST_NOTIFY);
            return;
        }

        try {
            // Khởi tạo JavaMailSender động từ config trong DB
            JavaMailSenderImpl mailSender = buildMailSender(config);

            // Render nội dung HTML từ Thymeleaf template
            String htmlContent = renderTemplate(requestDto);

            // Tạo tiêu đề email – thay placeholder {customerName}
            String subject = config.getSubjectTemplate()
                    .replace(TsoEmailConstant.PLACEHOLDER_CUSTOMER_NAME, requestDto.getCustomerName());

            // Tạo và gửi MimeMessage (hỗ trợ HTML + inline image)
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage, true, StandardCharsets.UTF_8.name());

            helper.setFrom(config.getMailFrom());
            helper.setTo(requestDto.getCustomerEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Đính kèm logo inline làm chữ ký
            ClassPathResource logoResource = new ClassPathResource(TsoEmailConstant.LOGO_CLASSPATH);
            if (logoResource.exists()) {
                helper.addInline(TsoEmailConstant.LOGO_CONTENT_ID, logoResource);
            } else {
                log.warn("[TsoEmailService] Không tìm thấy file logo tại '{}'.", TsoEmailConstant.LOGO_CLASSPATH);
            }

            mailSender.send(mimeMessage);
            log.info("[TsoEmailService] Đã gửi email thông báo tới khách hàng: {}", requestDto.getCustomerEmail());

        } catch (MessagingException e) {
            log.error("[TsoEmailService] Lỗi khi tạo/gửi email: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[TsoEmailService] Lỗi không xác định khi gửi email: {}", e.getMessage(), e);
        }
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    /**
     * Khởi tạo {@link JavaMailSenderImpl} từ cấu hình SMTP trong bảng tso_email_config.
     */
    private JavaMailSenderImpl buildMailSender(TsoEmailConfig config) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(config.getSmtpHost());
        sender.setPort(config.getSmtpPort());
        sender.setUsername(config.getSmtpEmail());
        sender.setPassword(config.getSmtpPassword());
        sender.setDefaultEncoding(StandardCharsets.UTF_8.name());

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout",           "5000");
        props.put("mail.smtp.writetimeout",      "5000");
        return sender;
    }

    /**
     * Render Thymeleaf template {@link TsoEmailConstant#TEMPLATE_CUSTOMER_REQUEST_NOTIFY} với dữ liệu khách hàng.
     */
    private String renderTemplate(TsoCustomerRequestDto requestDto) {
        Context ctx = new Context();
        ctx.setVariable("customerName",    requestDto.getCustomerName());
        ctx.setVariable("customerPhone",   requestDto.getCustomerPhone());
        ctx.setVariable("customerEmail",   requestDto.getCustomerEmail());
        ctx.setVariable("customerAddress", requestDto.getCustomerAddress());
        ctx.setVariable("requestContent",  requestDto.getRequestContent());
        return templateEngine.process(TsoEmailConstant.TEMPLATE_CUSTOMER_REQUEST_NOTIFY, ctx);
    }
}
