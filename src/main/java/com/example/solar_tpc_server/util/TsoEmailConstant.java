package com.example.solar_tpc_server.util;

/**
 * Hằng số liên quan đến chức năng gửi email.
 * Gom tập trung để dễ quản lý và tránh hardcode rải rác trong code.
 */
public final class TsoEmailConstant {

    // Private constructor ngăn khởi tạo
    private TsoEmailConstant() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Email config customer request
    public static final String CONFIG_KEY_CUSTOMER_REQUEST_NOTIFY = "CUSTOMER_REQUEST_NOTIFY";
    public static final String TEMPLATE_CUSTOMER_REQUEST_NOTIFY = "customer-request-notify";
    public static final String LOGO_CONTENT_ID = "logo_tpc";
    public static final String LOGO_CLASSPATH = "static/images/logo_tpc.png";
    public static final String PLACEHOLDER_CUSTOMER_NAME = "{customerName}";
    public static final int IS_ACTIVE = 1;
}
