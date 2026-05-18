package com.example.solar_tpc_server.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TSODateUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // date now
    public static LocalDate dateNow() {
        return LocalDate.now();
    }

    // datetime now
    public static LocalDateTime datetimeNow() {
        return LocalDateTime.now();
    }

    // convert date ra string
    public static String dateToString(LocalDate date) {
        if (date == null) return null;
        return date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    // convert datetime ra string
    public static String datetimeToString(LocalDateTime datetime) {
        if (datetime == null) return null;
        return datetime.format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }

    // format từ date ra string theo format
    public static String dateToString(LocalDate date, String format) {
        if (date == null || format == null) return null;
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    // format từ datetime ra string theo format
    public static String datetimeToString(LocalDateTime datetime, String format) {
        if (datetime == null || format == null) return null;
        return datetime.format(DateTimeFormatter.ofPattern(format));
    }

    // convert string ra date
    public static LocalDate stringToDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) return null;
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static LocalDate stringToDate(String dateString, String format) {
        if (dateString == null || dateString.trim().isEmpty()) return null;
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format));
    }

    // convert string ra datetime
    public static LocalDateTime stringToDatetime(String datetimeString) {
        if (datetimeString == null || datetimeString.trim().isEmpty()) return null;
        return LocalDateTime.parse(datetimeString, DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }

    public static LocalDateTime stringToDatetime(String datetimeString, String format) {
        if (datetimeString == null || datetimeString.trim().isEmpty()) return null;
        return LocalDateTime.parse(datetimeString, DateTimeFormatter.ofPattern(format));
    }

    // lấy ngày từ date
    public static int getDay(LocalDate date) {
        if (date == null) return 0;
        return date.getDayOfMonth();
    }

    // lấy tháng từ date
    public static int getMonth(LocalDate date) {
        if (date == null) return 0;
        return date.getMonthValue();
    }

    // lấy năm từ date
    public static int getYear(LocalDate date) {
        if (date == null) return 0;
        return date.getYear();
    }
    
    // lấy ngày từ datetime
    public static int getDay(LocalDateTime datetime) {
        if (datetime == null) return 0;
        return datetime.getDayOfMonth();
    }

    // lấy tháng từ datetime
    public static int getMonth(LocalDateTime datetime) {
        if (datetime == null) return 0;
        return datetime.getMonthValue();
    }

    // lấy năm từ datetime
    public static int getYear(LocalDateTime datetime) {
        if (datetime == null) return 0;
        return datetime.getYear();
    }

    // cộng số ngày vào date
    public static LocalDate addDays(LocalDate date, int days) {
        if (date == null) return null;
        return date.plusDays(days);
    }

    // trừ số ngày vào date
    public static LocalDate subtractDays(LocalDate date, int days) {
        if (date == null) return null;
        return date.minusDays(days);
    }

    // cộng số tháng vào date
    public static LocalDate addMonths(LocalDate date, int months) {
        if (date == null) return null;
        return date.plusMonths(months);
    }

    // cộng số năm vào date
    public static LocalDate addYears(LocalDate date, int years) {
        if (date == null) return null;
        return date.plusYears(years);
    }
    
    // cộng số ngày vào datetime
    public static LocalDateTime addDays(LocalDateTime datetime, int days) {
        if (datetime == null) return null;
        return datetime.plusDays(days);
    }

    // trừ số ngày vào datetime
    public static LocalDateTime subtractDays(LocalDateTime datetime, int days) {
        if (datetime == null) return null;
        return datetime.minusDays(days);
    }

    // cộng số tháng vào datetime
    public static LocalDateTime addMonths(LocalDateTime datetime, int months) {
        if (datetime == null) return null;
        return datetime.plusMonths(months);
    }

    // cộng số năm vào datetime
    public static LocalDateTime addYears(LocalDateTime datetime, int years) {
        if (datetime == null) return null;
        return datetime.plusYears(years);
    }
}
