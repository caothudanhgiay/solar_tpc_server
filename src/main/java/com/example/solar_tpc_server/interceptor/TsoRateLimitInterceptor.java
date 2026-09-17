package com.example.solar_tpc_server.interceptor;

import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.util.TsoMessageUtil;
import com.example.solar_tpc_server.util.TsoRateLimiterUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Chặn brute-force/spam theo IP ở tầng ứng dụng (L7), bổ trợ cho Cloudflare/Nginx
 * (vốn chỉ xử lý DDoS volumetric ở tầng mạng, không biết request nào thuộc endpoint nghiệp vụ nào).
 * Mỗi endpoint nhạy cảm đăng ký 1 instance riêng với maxRequests/windowMillis phù hợp trong TsoWebConfig.
 */
public class TsoRateLimitInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final int maxRequests;
    private final long windowMillis;

    public TsoRateLimitInterceptor(int maxRequests, long windowMillis) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String clientIp = resolveClientIp(request);
        String rateLimitKey = clientIp + ":" + request.getRequestURI();

        if (TsoRateLimiterUtil.isAllowed(rateLimitKey, maxRequests, windowMillis)) {
            return true;
        }

        long retryAfterMinutes = TsoRateLimiterUtil.toMinutesRoundedUp(TsoRateLimiterUtil.getRetryAfterMillis(rateLimitKey, windowMillis));
        TsoApiResponse<Object> apiResponse = TsoApiResponse.error(429, TsoMessageUtil.getMessage("error.too_many_requests", retryAfterMinutes));
        response.setStatus(429);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(apiResponse));
        return false;
    }

    /**
     * Chỉ tin header CF-Connecting-IP (do Cloudflare gắn ở edge, client không tự set được).
     * KHÔNG dùng X-Forwarded-For vì client có thể tự gắn header này với giá trị tùy ý để
     * giả mạo nhiều IP khác nhau mỗi request, vừa bypass rate limit vừa làm phình bộ nhớ đếm.
     */
    private String resolveClientIp(HttpServletRequest request) {
        String cfConnectingIp = request.getHeader("CF-Connecting-IP");
        if (cfConnectingIp != null && !cfConnectingIp.isBlank()) {
            return cfConnectingIp.trim();
        }
        return request.getRemoteAddr();
    }
}
