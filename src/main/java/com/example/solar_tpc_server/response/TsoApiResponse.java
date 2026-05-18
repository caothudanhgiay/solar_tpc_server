package com.example.solar_tpc_server.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TsoApiResponse<T> {
    
    private int statusCode;
    private String message;
    private T data;

    /**
     * Tạo một response thành công với thông báo tùy chỉnh.
     * @param data Dữ liệu trả về
     * @param message Thông báo (ví dụ: "Lấy dữ liệu thành công")
     */
    public static <T> TsoApiResponse<T> success(T data, String message) {
        return TsoApiResponse.<T>builder()
                .statusCode(200)
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * Tạo một response thành công với thông báo mặc định là "Success".
     * @param data Dữ liệu trả về
     */
    public static <T> TsoApiResponse<T> success(T data) {
        return success(data, "Success");
    }

    /**
     * Tạo một response báo lỗi.
     * @param statusCode Mã lỗi HTTP hoặc lỗi nghiệp vụ (ví dụ: 400, 404, 500)
     * @param message Thông báo lỗi
     */
    public static <T> TsoApiResponse<T> error(int statusCode, String message) {
        return TsoApiResponse.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .data(null)
                .build();
    }
}

