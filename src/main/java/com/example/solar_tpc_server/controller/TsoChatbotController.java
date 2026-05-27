package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.dto.TsoChatbotDto.TsoChatbotRequestDto;
import com.example.solar_tpc_server.dto.TsoChatbotDto.TsoChatbotResponseDto;
import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.service.TsoChatbotService;
import com.example.solar_tpc_server.util.TsoApiConstant;
import com.example.solar_tpc_server.util.TsoMessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller cho chatbot AI tư vấn khách hàng.
 * Endpoint công khai, không yêu cầu authentication.
 */
@RestController
@RequestMapping(TsoApiConstant.API_CHATBOT)
@RequiredArgsConstructor
public class TsoChatbotController {

    private final TsoChatbotService chatbotService;

    /**
     * POST /api/chatbot/ask
     * Nhận câu hỏi từ khách hàng, trả về câu trả lời AI.
     */
    @PostMapping("/ask")
    public ResponseEntity<TsoApiResponse<TsoChatbotResponseDto>> ask(@RequestBody TsoChatbotRequestDto request) {
        // Validate message không rỗng
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return ResponseEntity.badRequest().body(
                    TsoApiResponse.error(400, TsoMessageUtil.getMessage("error.invalid_input"))
            );
        }

        TsoChatbotResponseDto response = chatbotService.ask(request);
        return ResponseEntity.ok(TsoApiResponse.success(response));
    }
}
