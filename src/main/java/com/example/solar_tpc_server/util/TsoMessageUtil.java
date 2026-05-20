package com.example.solar_tpc_server.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class TsoMessageUtil {

    private static MessageSource messageSource;

    public TsoMessageUtil(MessageSource messageSource) {
        TsoMessageUtil.messageSource = messageSource;
    }

    public static String getMessage(String code) {
        if (messageSource == null) {
            return code;
        }
        try {
            return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return code;
        }
    }

    public static String getMessage(String code, Object... args) {
        if (messageSource == null) {
            return code;
        }
        try {
            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return code;
        }
    }
}
