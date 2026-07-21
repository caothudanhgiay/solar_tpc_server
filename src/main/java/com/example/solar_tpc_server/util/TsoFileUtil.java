package com.example.solar_tpc_server.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TsoFileUtil {

    public String getBaseUploadDir() {
        String envDir = System.getenv("UPLOAD_PROJECT_DIR");
        if (envDir == null || envDir.trim().isEmpty()) {
            return "D:/my_work/git/solar_tpcgr/solar-tpcgr" + TsoConstant.UPLOAD_PROJECT_DIR;
        }
        
        envDir = envDir.trim();
        // Remove trailing slashes
        while (envDir.endsWith("/") || envDir.endsWith("\\")) {
            envDir = envDir.substring(0, envDir.length() - 1);
        }
        // Prevent double "/upload" if the user accidentally included it in the environment variable
        if (envDir.endsWith("/upload")) {
            envDir = envDir.substring(0, envDir.length() - 7);
        }
        return envDir + TsoConstant.UPLOAD_PROJECT_DIR;
    }

    /**
     * Saves the uploaded image file based on project code and type.
     * Returns the relative URL for accessing the image.
     */
    public String saveImage(MultipartFile file, String projectCode, boolean isDetail) throws IOException {
        // Construct the target directory
        String targetDirPath = getBaseUploadDir();
        if (projectCode != null && !projectCode.trim().isEmpty()) {
            targetDirPath += File.separator + projectCode;
        }
        if (isDetail) {
            targetDirPath += File.separator + "product_detail";
        }
        
        File dir = new File(targetDirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "image.jpg";
        }
        
        String nameWithoutExt = originalFilename;
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            nameWithoutExt = originalFilename.substring(0, dotIndex);
            extension = originalFilename.substring(dotIndex);
        }
        
        String timeSuffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = nameWithoutExt + "_" + timeSuffix + extension;
        Path filePath = Paths.get(targetDirPath, fileName);
        
        // Copy the file to the target location
        Files.copy(file.getInputStream(), filePath);
        
        // Return only the filename, the DB will store just this.
        // URLs will be dynamically built during DTO mapping.
        return fileName;
    }
}
