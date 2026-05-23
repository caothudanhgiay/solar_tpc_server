package com.example.solar_tpc_server.config;

import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình Distributed Tracing với OpenTelemetry + Zipkin cho Spring Boot 4.
 *
 * <p>Spring Boot 4's {@code spring-boot-starter-opentelemetry} sẽ tự động
 * phát hiện bean {@link ZipkinSpanExporter} này và đăng ký nó vào OTel
 * SdkTracerProvider thông qua cơ chế {@code SdkTracerProviderBuilderCustomizer}.</p>
 *
 * <p>Trace sẽ được gửi đến Zipkin server tại endpoint được cấu hình
 * trong {@code management.zipkin.tracing.endpoint}.</p>
 */
@Configuration
public class TsoTracingConfig {

    @Value("${management.zipkin.tracing.endpoint:http://localhost:9411/api/v2/spans}")
    private String zipkinEndpoint;

    /**
     * Tạo ZipkinSpanExporter bean để gửi traces tới Zipkin server.
     * Spring Boot 4 OTel auto-configuration sẽ tự động wire bean này
     * vào SdkTracerProvider.
     *
     * @return ZipkinSpanExporter đã được cấu hình với endpoint từ properties
     */
    @Bean
    public ZipkinSpanExporter zipkinSpanExporter() {
        return ZipkinSpanExporter.builder()
                .setEndpoint(zipkinEndpoint)
                .build();
    }
}
