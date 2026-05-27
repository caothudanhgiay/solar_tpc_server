package com.example.solar_tpc_server.util;

import java.util.ArrayList;

public final class TsoApiConstant {

    private TsoApiConstant() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Base API
    public static final String API_BASE = "/api";

    // Endpoints
    public static final String API_MENUS = API_BASE + "/menus";
    public static final String API_CUSTOMER_REQUESTS = API_BASE + "/customer-requests";
    public static final String API_USERS = API_BASE + "/users";
    public static final String API_CHATBOT = API_BASE + "/chatbot";

    // HTTP Methods
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    // Tên các method/operation (thường dùng cho logging hoặc phân quyền)
    public static final String METHOD_GET_ALL_ENABLED_MENUS = "getAllEnabledMenus";

    // Bạn có thể thêm các endpoint và tên method khác ở đây
}

