package com.example.solar_tpc_server.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class TSOValidation {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final String NUMBER_REGEX = "^-?\\d+(\\.\\d+)?$";
    private static final Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_REGEX);

    public static boolean isRequired(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean maxLength(String value, int max) {
        if (value == null)
            return true;
        return value.length() <= max;
    }

    public static boolean minLength(String value, int min) {
        if (value == null)
            return false;
        return value.length() >= min;
    }

    public static boolean isEmail(String email) {
        if (email == null || email.trim().isEmpty())
            return true;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isNumber(String value) {
        if (value == null || value.trim().isEmpty())
            return true;
        return NUMBER_PATTERN.matcher(value).matches();
    }

    public static boolean isDate(String dateStr, String format) {
        if (dateStr == null || dateStr.trim().isEmpty())
            return true;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
