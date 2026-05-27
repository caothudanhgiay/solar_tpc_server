package com.example.solar_tpc_server.exception;

import lombok.Getter;
import com.example.solar_tpc_server.util.TsoMessageUtil;

/**
 * Enum định nghĩa toàn bộ mã lỗi (Error Code) trong hệ thống
 */
@Getter
public enum TsoErrorCode {

    // Nhóm 4xx - Lỗi từ phía Client
    BAD_REQUEST(400, "error.bad_request"),
    UNAUTHORIZED(401, "error.unauthorized"),
    FORBIDDEN(403, "error.forbidden"),
    NOT_FOUND(404, "error.not_found"),
    USER_NOT_EXISTED(404, "error.user_not_existed"),
    INVALID_CREDENTIALS(401, "error.invalid_credentials"),
    
    // Nhóm 5xx - Lỗi từ phía Server
    INTERNAL_SERVER_ERROR(500, "error.internal_server_error"),
    BAD_GATEWAY(502, "error.bad_gateway"),
    SERVICE_UNAVAILABLE(503, "error.service_unavailable");

    private final int code;
    private final String messageKey;

    TsoErrorCode(int code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    public String getMessage() {
        return TsoMessageUtil.getMessage(messageKey);
    }
}

