package com.example.solar_tpc_server;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class TsoSolarTpcServerApplication {

    @PostConstruct
    public void init() {
        // Cấu hình timezone mặc định của JVM là giờ Việt Nam (UTC+7)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }

    public static void main(String[] args) {
        SpringApplication.run(TsoSolarTpcServerApplication.class, args);
    }

}

