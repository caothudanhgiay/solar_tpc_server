package com.example.solar_tpc_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class TsoWebConfig implements WebMvcConfigurer {

    @Value("${upload.dir:./uploads/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadDir).toFile().getAbsolutePath();
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath += File.separator;
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///" + absolutePath.replace("\\", "/"));
    }
}
