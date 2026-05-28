package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.dto.TsoChatbotDto.TsoChatbotRequestDto;
import com.example.solar_tpc_server.dto.TsoChatbotDto.TsoChatbotResponseDto;
import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.service.TsoChatbotService;
import com.example.solar_tpc_server.util.TsoApiConstant;
import com.example.solar_tpc_server.util.TsoMessageUtil;
import com.example.solar_tpc_server.validation.TsoChatBotValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

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
     * Nhận câu hỏi từ khách hàng, trả về câu trả lời AI (blocking, non-streaming).
     * Giữ lại làm fallback cho client chưa hỗ trợ SSE.
     */
    @PostMapping("/ask")
    public ResponseEntity<TsoApiResponse<TsoChatbotResponseDto>> ask(@RequestBody TsoChatbotRequestDto request) {
        // Validate request sử dụng TsoChatBotValidation
        Map<String, String> errors = TsoChatBotValidation.validateAsk(request);
        if (!errors.isEmpty()) {
            String errorMsg = errors.values().iterator().next();
            return ResponseEntity.badRequest().body(
                    TsoApiResponse.error(400, errorMsg)
            );
        }

        TsoChatbotResponseDto response = chatbotService.ask(request);
        return ResponseEntity.ok(TsoApiResponse.success(response));
    }

    /**
     * POST /api/chatbot/ask/stream
     * SSE Streaming — phản hồi từng phần ngay lập tức, giống ChatGPT.
     * <p>
     * SSE Events:
     * - "session": {"sessionId":"..."} — gửi ngay đầu tiên
     * - "chunk": {"text":"..."} — từng đoạn text từ Gemini
     * - "done": {"status":"completed"} — khi hoàn tất
     * - "error": {"message":"..."} — khi có lỗi
     */
    @PostMapping(value = "/ask/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter askStream(@RequestBody TsoChatbotRequestDto request) {
        // Validate request sử dụng TsoChatBotValidation
        Map<String, String> errors = TsoChatBotValidation.validateAsk(request);
        if (!errors.isEmpty()) {
            SseEmitter emitter = new SseEmitter(0L);
            try {
                String errorMsg = errors.values().iterator().next();
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"message\":\"" + errorMsg + "\"}"));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }

        // Timeout 60 giây cho streaming
        SseEmitter emitter = new SseEmitter(60_000L);

        // Xử lý khi client disconnect
        emitter.onTimeout(emitter::complete);
        emitter.onError(e -> emitter.complete());

        chatbotService.askStream(request, emitter);

        return emitter;
    }
}
