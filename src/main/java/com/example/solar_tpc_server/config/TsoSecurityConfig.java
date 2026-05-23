package com.example.solar_tpc_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.solar_tpc_server.util.TsoApiConstant;
import com.example.solar_tpc_server.util.PropertyUtils;

import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.context.annotation.DependsOn;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
@Configuration
@EnableWebSecurity
@DependsOn("propertyUtils")
public class TsoSecurityConfig {



    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] bytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec originalKey = new SecretKeySpec(bytes, 0, bytes.length, "RSA");
        return NimbusJwtDecoder.withSecretKey(originalKey).macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        byte[] bytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec originalKey = new SecretKeySpec(bytes, 0, bytes.length, "RSA");
        return new NimbusJwtEncoder(new ImmutableSecret<>(originalKey));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Thêm cấu hình CORS
            .csrf(csrf -> csrf.disable()) // Tắt CSRF vì đây là API Stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/login").permitAll() // Đăng nhập
                .requestMatchers("/api/menus").permitAll() // Cho phép truy cập công khai API lấy menu
                .requestMatchers("/api/customer-requests").permitAll() // Cho phép truy cập công khai gửi yêu cầu
                .requestMatchers("/api/users/**").permitAll() // Cho phép truy cập công khai API người dùng để test
                .requestMatchers("/actuator/**").permitAll() // Cho phép truy cập công khai actuator để debug
                .anyRequest().authenticated() // Tất cả các request khác cần xác thực
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(org.springframework.security.config.Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        ArrayList<String> methods = new ArrayList<String>() {{    
            add(TsoApiConstant.GET);
            add(TsoApiConstant.POST);
            add(TsoApiConstant.PUT);
            add(TsoApiConstant.DELETE);
        }};   
        
        // Sử dụng PropertyUtils để lấy cấu hình
        String allowedOrigins = PropertyUtils.getProperty("app.cors.allowed-origins", "http://localhost:3000");
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(","))); 
        configuration.setAllowedMethods(methods);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Accept-Language"));
        configuration.setAllowCredentials(true); // Cho phép gửi kèm cookie/credentials nếu cần
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cho tất cả các endpoint
        return source;
    }
}

