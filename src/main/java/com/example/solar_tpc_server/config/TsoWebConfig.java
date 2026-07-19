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
    
    private final com.example.solar_tpc_server.util.TsoFileUtil tsoFileUtil;
    
    public TsoWebConfig(com.example.solar_tpc_server.util.TsoFileUtil tsoFileUtil) {
        this.tsoFileUtil = tsoFileUtil;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String baseUploadDir = tsoFileUtil.getBaseUploadDir();
        String absolutePath = Paths.get(baseUploadDir).toFile().getAbsolutePath();
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath += File.separator;
        }
        registry.addResourceHandler(com.example.solar_tpc_server.util.TsoConstant.UPLOAD_PROJECT_DIR + "/**")
                .addResourceLocations("file:///" + absolutePath.replace("\\", "/"));
        
        // Keep the old uploads handler just in case
        String oldUploadDirAbs = Paths.get(uploadDir).toFile().getAbsolutePath();
        if (!oldUploadDirAbs.endsWith(File.separator)) {
            oldUploadDirAbs += File.separator;
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///" + oldUploadDirAbs.replace("\\", "/"));
    }
}
