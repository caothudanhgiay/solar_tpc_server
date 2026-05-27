package com.example.solar_tpc_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity ánh xạ bảng tso_email_config.
 * Lưu cấu hình SMTP cho từng loại email thông báo trong hệ thống.
 */
@Entity
@Table(name = "tso_email_config")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TsoEmailConfig extends TsoMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Long configId;

    /** Khóa định danh loại email, VD: CUSTOMER_REQUEST_NOTIFY */
    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;

    /** SMTP host, VD: smtp.gmail.com */
    @Column(name = "smtp_host", nullable = false)
    private String smtpHost;

    /** SMTP port (587 = STARTTLS, 465 = SSL) */
    @Column(name = "smtp_port", nullable = false)
    private Integer smtpPort;

    /** Tài khoản Gmail dùng để gửi */
    @Column(name = "smtp_email", nullable = false)
    private String smtpEmail;

    /** Gmail App Password (16 ký tự) */
    @Column(name = "smtp_password", nullable = false, length = 500)
    private String smtpPassword;

    /** Địa chỉ hiển thị ở trường From, VD: Solar TPC Group <solar.tpcgr@gmail.com> */
    @Column(name = "mail_from", nullable = false)
    private String mailFrom;

    /** Danh sách địa chỉ nhận, phân tách bằng dấu phẩy */
    @Column(name = "mail_to", nullable = false, length = 1000)
    private String mailTo;

    /** Danh sách CC, phân tách bằng dấu phẩy (tuỳ chọn) */
    @Column(name = "mail_cc", length = 1000)
    private String mailCc;

    /** Tiêu đề email, hỗ trợ placeholder VD: [Solar TPC] Yêu cầu từ {customerName} */
    @Column(name = "subject_template", nullable = false, length = 500)
    private String subjectTemplate;

    /** 1 = bật, 0 = tắt */
    @Column(name = "is_active", nullable = false)
    private Integer isActive;
}
