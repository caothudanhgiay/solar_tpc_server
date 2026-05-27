-- ============================================================
-- Bảng: tso_email_config
-- Mục đích: Lưu cấu hình SMTP để server gửi email thông báo
-- Mỗi config_key tương ứng 1 loại email (VD: gửi thông báo yêu cầu khách hàng)
-- ============================================================

CREATE TABLE tso_email_config(
config_id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'Id của yêu cầu',
config_key varchar(100) NOT NULL   COMMENT 'Khóa định danh loại email',
smtp_host varchar(255) NOT NULL   COMMENT 'SMTP host',
smtp_port INT NOT NULL   COMMENT 'SMTP port',
smtp_email varchar(255) NOT NULL   COMMENT 'Tài khoản Gmail dùng để gửi',
smtp_password varchar(255) NOT NULL   COMMENT 'Password của tài khoản Gmail',
mail_from varchar(255) NOT NULL   COMMENT 'Địa chỉ hiển thị ở trường From',
mail_to varchar(255) NOT NULL   COMMENT 'Danh sách địa chỉ nhận (phân tách bằng dấu phẩy)',
mail_cc varchar(255) NULL   COMMENT 'Danh sách CC (phân tách bằng dấu phẩy)',
subject_template varchar(255) NOT NULL   COMMENT 'Tiêu đề email',
is_active tinyint(1) NOT NULL   COMMENT '1 = đang bật, 0 = tắt',
created_at varchar(255) NOT NULL   COMMENT 'Người đã tạo',
created_date datetime NOT NULL   COMMENT 'Ngày tạo hiển thị',
updated_date datetime NULL   COMMENT 'Ngày cập nhật gần nhất'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- INSERT mẫu: Cấu hình gửi thông báo khi có yêu cầu khách hàng
-- ⚠️  Thay smtp_username và smtp_password bằng thông tin thật
-- ⚠️  smtp_password là Gmail App Password (16 ký tự, không có dấu cách)
--     Tạo tại: https://myaccount.google.com/apppasswords
-- ============================================================

INSERT INTO tso_email_config (
    config_key,
    smtp_host,
    smtp_port,
    smtp_email,
    smtp_password,
    mail_from,
    mail_to,
    mail_cc,
    subject_template,
    is_active,
    created_at,
    created_date,
    updated_date
) VALUES (
    'CUSTOMER_REQUEST_NOTIFY',              -- config_key (code sẽ dùng key này để tra cứu)
    'smtp.gmail.com',                       -- smtp_host
    587,                                    -- smtp_port (STARTTLS)
    'solar.tpcgr@gmail.com',               -- smtp_username ← Điền email Gmail của bạn
    '',              -- smtp_password ← Điền Gmail App Password (16 ký tự)
    'Solar TPC Group <solar.tpcgr@gmail.com>',  -- mail_from (tên hiển thị + email)
    '',               -- mail_to (nhận thông báo)
    NULL,                                   -- mail_cc (để NULL nếu không cần)
    '[Solar TPC] Yêu cầu tư vấn mới từ {customerName}',  -- subject_template
    1,                                      -- is_active = 1 (bật)
    'SYSTEM',                               -- created_at
    NOW(),                                  -- created_date
    NULL                                    -- updated_date
);


-- ============================================================
-- Kiểm tra kết quả
-- ============================================================

SELECT * FROM tso_email_config WHERE config_key = 'CUSTOMER_REQUEST_NOTIFY';
