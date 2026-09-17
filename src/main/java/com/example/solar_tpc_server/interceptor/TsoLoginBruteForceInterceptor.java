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
 * Chặn brute-force login theo IP dựa trên SỐ LẦN THẤT BẠI liên tiếp — KHÔNG tính lần login
 * thành công. Nhờ vậy nhiều user dùng chung 1 IP (vd: cùng mạng văn phòng) không bị khóa lẫn
 * nhau: người login đúng luôn đi qua bình thường và reset bộ đếm của IP đó, chỉ IP nào liên
 * tục sai mật khẩu mới bị tạm khóa.
 */
public class TsoLoginBruteForceInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final int maxFailures;
    private final long windowMillis;

    public TsoLoginBruteForceInterceptor(int maxFailures, long windowMillis) {
        this.maxFailures = maxFailures;
        this.windowMillis = windowMillis;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String clientIp = resolveClientIp(request);

        if (!TsoRateLimiterUtil.isBlocked(clientIp, maxFailures, windowMillis)) {
            return true;
        }

        long retryAfterMinutes = TsoRateLimiterUtil.toMinutesRoundedUp(TsoRateLimiterUtil.getBlockedRetryAfterMillis(clientIp, windowMillis));
        TsoApiResponse<Object> apiResponse = TsoApiResponse.error(429, TsoMessageUtil.getMessage("error.too_many_requests", retryAfterMinutes));
        response.setStatus(429);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(apiResponse));
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String clientIp = resolveClientIp(request);
        if (response.getStatus() == HttpServletResponse.SC_OK) {
            TsoRateLimiterUtil.reset(clientIp);
        } else {
            TsoRateLimiterUtil.recordFailure(clientIp, windowMillis);
        }
    }

    /**
     * Chỉ tin header CF-Connecting-IP (do Cloudflare gắn ở edge, client không tự set được).
     * KHÔNG dùng X-Forwarded-For vì client có thể tự gắn header này để giả mạo IP, bypass rate limit.
     */
    private String resolveClientIp(HttpServletRequest request) {
        String cfConnectingIp = request.getHeader("CF-Connecting-IP");
        if (cfConnectingIp != null && !cfConnectingIp.isBlank()) {
            return cfConnectingIp.trim();
        }
        return request.getRemoteAddr();
    }
}
