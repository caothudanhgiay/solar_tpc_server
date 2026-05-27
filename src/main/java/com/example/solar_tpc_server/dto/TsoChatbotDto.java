package com.example.solar_tpc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho chức năng chatbot AI.
 * Chứa cả Request và Response dưới dạng inner class.
 */
public class TsoChatbotDto {

    private TsoChatbotDto() {}

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TsoChatbotRequestDto {
        /** Nội dung câu hỏi của khách hàng */
        private String message;
        /** Session ID để duy trì lịch sử hội thoại */
        private String sessionId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TsoChatbotResponseDto {
        /** Câu trả lời từ AI */
        private String reply;
        /** Session ID để client gửi lại ở lần tiếp theo */
        private String sessionId;
    }
}
