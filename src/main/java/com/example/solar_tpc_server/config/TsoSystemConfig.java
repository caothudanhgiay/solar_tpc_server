package com.example.solar_tpc_server.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class TsoSystemConfig {

    public static String TIME_ZONE = "Asia/Ho_Chi_Minh";
    public static ZoneId ZONE_ID = ZoneId.of("Asia/Ho_Chi_Minh");

    @Value("${app.timezone:Asia/Ho_Chi_Minh}")
    private String timeZoneConfig;

    @PostConstruct
    public void init() {
        TIME_ZONE = timeZoneConfig;
        ZONE_ID = ZoneId.of(timeZoneConfig);
        
        // Thiết lập TimeZone mặc định cho toàn bộ JVM
        TimeZone.setDefault(TimeZone.getTimeZone(timeZoneConfig));
    }
}
