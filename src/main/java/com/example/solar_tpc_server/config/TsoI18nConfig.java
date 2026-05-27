package com.example.solar_tpc_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import com.example.solar_tpc_server.util.TsoConstant;

import java.util.Arrays;
import java.util.Locale;

@Configuration
public class TsoI18nConfig {

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(new Locale(TsoConstant.LANG_VI));
        resolver.setSupportedLocales(Arrays.asList(new Locale(TsoConstant.LANG_VI), new Locale(TsoConstant.LANG_EN)));
        return resolver;
    }
}
