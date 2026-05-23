package com.example.solar_tpc_server.util;

import java.security.SecureRandom;
import java.util.regex.Pattern;

public final class TsoCommonUtil {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final String PHONE_REGEX = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private TsoCommonUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ==========================================
    // STRING UTILS
    // ==========================================

    /**
     * Kiểm tra chuỗi có null hoặc rỗng ("") hay không.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Kiểm tra chuỗi không null và không rỗng.
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Kiểm tra chuỗi có null, rỗng ("") hoặc chỉ chứa khoảng trắng hay không.
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Kiểm tra chuỗi không null, không rỗng và không chỉ chứa khoảng trắng.
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Cắt khoảng trắng đầu cuối an toàn (tránh NullPointerException).
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * Cắt chuỗi an toàn, tự động kiểm tra biên giới hạn để không bị IndexOutOfBoundsException.
     */
    public static String substring(String str, int start, int end) {
        if (str == null) return null;
        if (start < 0) start = 0;
        if (end > str.length()) end = str.length();
        if (start > end) return "";
        return str.substring(start, end);
    }

    /**
     * Viết gọn chuỗi nếu vượt quá số ký tự quy định và thêm dấu ba chấm "...".
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) return null;
        if (str.length() <= maxLength) return str;
        return substring(str, 0, maxLength) + "...";
    }

    // ==========================================
    // NUMBER UTILS
    // ==========================================

    /**
     * Kiểm tra chuỗi có phải số nguyên hợp lệ hay không.
     */
    public static boolean isInteger(String str) {
        if (isBlank(str)) return false;
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Kiểm tra chuỗi có phải số thực (Double) hợp lệ hay không.
     */
    public static boolean isDouble(String str) {
        if (isBlank(str)) return false;
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Chuyển đổi chuỗi sang Integer an toàn, trả về giá trị mặc định nếu lỗi.
     */
    public static Integer toInteger(String str, Integer defaultValue) {
        if (isBlank(str)) return defaultValue;
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Chuyển đổi chuỗi sang Long an toàn, trả về giá trị mặc định nếu lỗi.
     */
    public static Long toLong(String str, Long defaultValue) {
        if (isBlank(str)) return defaultValue;
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Chuyển đổi chuỗi sang Double an toàn, trả về giá trị mặc định nếu lỗi.
     */
    public static Double toDouble(String str, Double defaultValue) {
        if (isBlank(str)) return defaultValue;
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // ==========================================
    // OTHER COMMON UTILS
    // ==========================================

    /**
     * Kiểm tra định dạng Email hợp lệ.
     */
    public static boolean isValidEmail(String email) {
        if (isBlank(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Kiểm tra định dạng số điện thoại Việt Nam hợp lệ.
     */
    public static boolean isValidPhone(String phone) {
        if (isBlank(phone)) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Tạo chuỗi ngẫu nhiên (ví dụ: dùng cho mã OTP, mã kích hoạt, token tạm thời).
     */
    public static String generateRandomString(int length) {
        if (length <= 0) return "";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
