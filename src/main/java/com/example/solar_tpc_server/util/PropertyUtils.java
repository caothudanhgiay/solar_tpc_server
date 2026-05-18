package com.example.solar_tpc_server.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class PropertyUtils {

    private static Environment environment;

    // Spring sẽ tự động inject Environment vào đây khi ứng dụng khởi động
    @Autowired
    public void setEnvironment(Environment env) {
        PropertyUtils.environment = env;
    }

    /**
     * Lấy giá trị từ file properties dựa vào key
     * @param key Tên key trong file properties (vd: "app.cors.allowed-origins")
     * @return Giá trị tương ứng, hoặc null nếu không tìm thấy
     */
    public static String getProperty(String key) {
        if (environment == null) {
            return null;
        }
        return environment.getProperty(key);
    }

    /**
     * Lấy giá trị từ file properties dựa vào key, có giá trị mặc định
     * @param key Tên key
     * @param defaultValue Giá trị mặc định trả về nếu không tìm thấy key
     * @return Giá trị tương ứng hoặc defaultValue
     */
    public static String getProperty(String key, String defaultValue) {
        if (environment == null) {
            return defaultValue;
        }
        return environment.getProperty(key, defaultValue);
    }
}
