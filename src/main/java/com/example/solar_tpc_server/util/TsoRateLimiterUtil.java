package com.example.solar_tpc_server.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiter dạng sliding-window-log, lưu state trong bộ nhớ (RAM) của instance hiện tại.
 * Chỉ đúng khi backend chạy 1 instance duy nhất — nếu sau này scale nhiều instance/pod,
 * cần thay bằng giải pháp có shared state (vd: Redis) để các instance dùng chung bộ đếm.
 */
public final class TsoRateLimiterUtil {

    private static final Map<String, Deque<Long>> REQUEST_LOG = new ConcurrentHashMap<>();
    private static final Map<String, Deque<Long>> FAILURE_LOG = new ConcurrentHashMap<>();

    private TsoRateLimiterUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Giới hạn theo TỔNG SỐ LẦN GỌI (tính cả thành công lẫn thất bại) — dùng cho các endpoint
     * cần chặn spam theo khối lượng request (vd: form gửi yêu cầu khách hàng).
     *
     * @param key          định danh duy nhất cho đối tượng cần giới hạn (vd: "ip:endpoint")
     * @param maxRequests  số request tối đa cho phép trong khoảng thời gian windowMillis
     * @param windowMillis độ dài cửa sổ thời gian (ms)
     * @return true nếu request được phép đi tiếp, false nếu đã vượt giới hạn
     */
    public static boolean isAllowed(String key, int maxRequests, long windowMillis) {
        long now = System.currentTimeMillis();
        Deque<Long> timestamps = REQUEST_LOG.computeIfAbsent(key, k -> new ArrayDeque<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowMillis) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= maxRequests) {
                return false;
            }

            timestamps.addLast(now);
            return true;
        }
    }

    /**
     * Giới hạn theo SỐ LẦN THẤT BẠI LIÊN TIẾP — dùng cho các endpoint dạng đăng nhập, nơi
     * nhiều user có thể dùng chung 1 IP (vd: mạng văn phòng): chỉ IP nào liên tục thất bại
     * mới bị khóa, gọi {@link #reset(String)} khi có 1 lần thành công để không ảnh hưởng
     * tới những user khác đang dùng chung IP đó.
     *
     * @return true nếu key đang bị khóa (đã đạt/vượt maxFailures trong windowMillis gần nhất)
     */
    public static boolean isBlocked(String key, int maxFailures, long windowMillis) {
        Deque<Long> timestamps = FAILURE_LOG.get(key);
        if (timestamps == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowMillis) {
                timestamps.pollFirst();
            }
            return timestamps.size() >= maxFailures;
        }
    }

    /** Ghi nhận 1 lần thất bại mới cho key, dùng chung windowMillis với {@link #isBlocked}. */
    public static void recordFailure(String key, long windowMillis) {
        long now = System.currentTimeMillis();
        Deque<Long> timestamps = FAILURE_LOG.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowMillis) {
                timestamps.pollFirst();
            }
            timestamps.addLast(now);
        }
    }

    /** Xóa toàn bộ lịch sử thất bại của key — gọi khi thao tác thành công (vd: login đúng). */
    public static void reset(String key) {
        FAILURE_LOG.remove(key);
    }

    /**
     * Số mili-giây còn lại tới khi request tiếp theo được phép — dùng cùng key/windowMillis
     * với {@link #isAllowed}. Trả về 0 nếu key hiện không bị giới hạn.
     */
    public static long getRetryAfterMillis(String key, long windowMillis) {
        return computeRetryAfterMillis(REQUEST_LOG.get(key), windowMillis);
    }

    /**
     * Số mili-giây còn lại tới khi hết bị khóa — dùng cùng key/windowMillis với
     * {@link #isBlocked}. Trả về 0 nếu key hiện không bị khóa.
     */
    public static long getBlockedRetryAfterMillis(String key, long windowMillis) {
        return computeRetryAfterMillis(FAILURE_LOG.get(key), windowMillis);
    }

    /** Quy đổi mili-giây còn lại sang số phút làm tròn lên, tối thiểu 1 phút nếu còn phải chờ. */
    public static long toMinutesRoundedUp(long millis) {
        if (millis <= 0) {
            return 0;
        }
        return Math.max(1, (long) Math.ceil(millis / 60_000.0));
    }

    /** Thời điểm hết hạn = timestamp cũ nhất còn trong cửa sổ + windowMillis. */
    private static long computeRetryAfterMillis(Deque<Long> timestamps, long windowMillis) {
        if (timestamps == null || timestamps.isEmpty()) {
            return 0;
        }
        Long oldest;
        synchronized (timestamps) {
            oldest = timestamps.peekFirst();
        }
        if (oldest == null) {
            return 0;
        }
        long remaining = windowMillis - (System.currentTimeMillis() - oldest);
        return Math.max(remaining, 0);
    }
}
