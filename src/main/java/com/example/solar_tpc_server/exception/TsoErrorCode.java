package com.example.solar_tpc_server.exception;

import lombok.Getter;

/**
 * Enum định nghĩa toàn bộ mã lỗi (Error Code) trong hệ thống
 */
@Getter
public enum TsoErrorCode {

    // Nhóm 4xx - Lỗi từ phía Client
    BAD_REQUEST(400, "Dữ liệu yêu cầu không hợp lệ (Bad Request)"),
    UNAUTHORIZED(401, "Chưa xác thực hoặc phiên đăng nhập hết hạn (Unauthorized)"),
    FORBIDDEN(403, "Bạn không có quyền truy cập chức năng này (Forbidden)"),
    NOT_FOUND(404, "Không tìm thấy tài nguyên yêu cầu (Not Found)"),
    USER_NOT_EXISTED(404, "Tài khoản người dùng không tồn tại"),
    
    // Nhóm 5xx - Lỗi từ phía Server
    INTERNAL_SERVER_ERROR(500, "Lỗi hệ thống không xác định (Internal Server Error)"),
    BAD_GATEWAY(502, "Cổng kết nối không hợp lệ (Bad Gateway)"),
    SERVICE_UNAVAILABLE(503, "Dịch vụ tạm thời không hoạt động (Service Unavailable)");

    private final int code;
    private final String message;

    TsoErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

