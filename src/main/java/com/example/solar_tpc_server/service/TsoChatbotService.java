package com.example.solar_tpc_server.service;

import com.example.solar_tpc_server.dto.TsoChatbotDto.TsoChatbotRequestDto;
import com.example.solar_tpc_server.dto.TsoChatbotDto.TsoChatbotResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service chatbot AI sử dụng Google Gemini API.
 * <p>
 * - Gọi Gemini REST API với system prompt chuyên về Solar TPC
 * - Quản lý lịch sử hội thoại in-memory theo sessionId (TTL 30 phút)
 * - System prompt được đọc từ file: resources/chatbot-system-prompt.md
 */
@Slf4j
@Service
public class TsoChatbotService {

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";
    private static final int MAX_HISTORY_SIZE = 20; // Giới hạn số lượt hội thoại mỗi session
    private static final long SESSION_TTL_MS = 30 * 60 * 1000L; // 30 phút
    private static final String SYSTEM_PROMPT_FILE = "chatbot-system-prompt.md";

    @Value("${app.gemini.api-key}")
    private String apiKey;

    @Value("${app.gemini.model}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // In-memory session store: sessionId -> ChatSession
    private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();

    /**
     * System prompt được load từ file khi khởi động.
     */
    private String systemPrompt;

    /**
     * Đọc system prompt từ file resources/chatbot-system-prompt.md khi khởi động.
     */
    @PostConstruct
    private void loadSystemPrompt() {
        try {
            ClassPathResource resource = new ClassPathResource(SYSTEM_PROMPT_FILE);
            try (InputStream is = resource.getInputStream()) {
                this.systemPrompt = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                log.info("[TsoChatbotService] Đã load system prompt từ file {} ({} ký tự)", SYSTEM_PROMPT_FILE, systemPrompt.length());
            }
        } catch (Exception e) {
            log.error("[TsoChatbotService] Không thể đọc file {}: {}", SYSTEM_PROMPT_FILE, e.getMessage());
            // Fallback: prompt mặc định tối thiểu
            this.systemPrompt = "Bạn là trợ lý tư vấn AI của Solar TPC Group. Trả lời lịch sự, chuyên nghiệp về năng lượng mặt trời.";
        }
    }

    /**
     * Xử lý câu hỏi của khách hàng và trả về câu trả lời từ Gemini AI.
     */
    public TsoChatbotResponseDto ask(TsoChatbotRequestDto request) {
        // Tạo hoặc lấy session
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();
        }

        ChatSession session = sessions.compute(sessionId, (key, existing) -> {
            if (existing == null || existing.isExpired()) {
                return new ChatSession();
            }
            existing.touch();
            return existing;
        });

        // Thêm tin nhắn user vào history
        session.addMessage("user", request.getMessage());

        try {
            // Gọi Gemini API
            String reply = callGeminiApi(session);

            // Thêm response AI vào history
            session.addMessage("model", reply);

            return new TsoChatbotResponseDto(reply, sessionId);
        } catch (Exception e) {
            log.error("[TsoChatbotService] Lỗi khi gọi Gemini API: {}", e.getMessage(), e);
            // Xóa tin nhắn user vừa thêm nếu lỗi
            session.removeLastMessage();
            
            if ("RATE_LIMIT_EXCEEDED".equals(e.getMessage())) {
                return new TsoChatbotResponseDto(
                        "Hệ thống AI đang quá tải do nhận được quá nhiều yêu cầu. Vui lòng chờ khoảng 20 giây và thử lại. ⏳",
                        sessionId
                );
            }
            
            return new TsoChatbotResponseDto(
                    "Xin lỗi, hệ thống đang bận. Vui lòng thử lại sau hoặc liên hệ hotline để được hỗ trợ. ☎️",
                    sessionId
            );
        }
    }

    /**
     * Dọn dẹp các session hết hạn (có thể gọi từ @Scheduled).
     */
    public void cleanExpiredSessions() {
        sessions.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    /**
     * Gọi Gemini REST API với lịch sử hội thoại.
     */
    private String callGeminiApi(ChatSession session) throws Exception {
        String url = String.format(GEMINI_API_URL, model, apiKey);

        // Build request body
        ObjectNode requestBody = objectMapper.createObjectNode();

        // System instruction
        ObjectNode systemInstruction = objectMapper.createObjectNode();
        ObjectNode systemPart = objectMapper.createObjectNode();
        systemPart.put("text", systemPrompt);
        ArrayNode systemParts = objectMapper.createArrayNode();
        systemParts.add(systemPart);
        systemInstruction.set("parts", systemParts);
        requestBody.set("system_instruction", systemInstruction);

        // Contents (conversation history)
        ArrayNode contents = objectMapper.createArrayNode();
        for (ChatMessage msg : session.getMessages()) {
            ObjectNode content = objectMapper.createObjectNode();
            content.put("role", msg.role());
            ObjectNode part = objectMapper.createObjectNode();
            part.put("text", msg.text());
            ArrayNode parts = objectMapper.createArrayNode();
            parts.add(part);
            content.set("parts", parts);
            contents.add(content);
        }
        requestBody.set("contents", contents);

        // Generation config
        ObjectNode generationConfig = objectMapper.createObjectNode();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 1024);
        requestBody.set("generationConfig", generationConfig);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 429) {
            log.warn("[TsoChatbotService] Gemini API quá tải (429 Too Many Requests): {}", response.body());
            throw new RuntimeException("RATE_LIMIT_EXCEEDED");
        }

        if (response.statusCode() != 200) {
            log.error("[TsoChatbotService] Gemini API trả về lỗi {}: {}", response.statusCode(), response.body());
            throw new RuntimeException("Gemini API error: " + response.statusCode());
        }

        // Parse response
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode candidates = root.path("candidates");
        if (candidates.isArray() && !candidates.isEmpty()) {
            JsonNode firstCandidate = candidates.get(0);
            JsonNode contentParts = firstCandidate.path("content").path("parts");
            if (contentParts.isArray() && !contentParts.isEmpty()) {
                // Ghép tất cả parts để không bỏ sót nội dung
                StringBuilder sb = new StringBuilder();
                for (JsonNode part : contentParts) {
                    String text = part.path("text").asText("");
                    if (!text.isEmpty()) {
                        sb.append(text);
                    }
                }
                if (!sb.isEmpty()) {
                    return sb.toString();
                }
            }
        }

        throw new RuntimeException("Không thể parse response từ Gemini API");
    }

    // =========================================================================
    // Inner classes
    // =========================================================================

    /**
     * Lưu lịch sử hội thoại cho mỗi session.
     */
    private static class ChatSession {
        private final List<ChatMessage> messages = new ArrayList<>();
        private long lastAccessTime = System.currentTimeMillis();

        void addMessage(String role, String text) {
            messages.add(new ChatMessage(role, text));
            // Giới hạn lịch sử
            while (messages.size() > MAX_HISTORY_SIZE * 2) {
                messages.remove(0);
            }
        }

        void removeLastMessage() {
            if (!messages.isEmpty()) {
                messages.remove(messages.size() - 1);
            }
        }

        List<ChatMessage> getMessages() {
            return messages;
        }

        void touch() {
            this.lastAccessTime = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - lastAccessTime > SESSION_TTL_MS;
        }
    }

    private record ChatMessage(String role, String text) {}
}
