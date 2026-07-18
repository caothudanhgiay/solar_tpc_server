package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.service.TsoProjectService;
import com.example.solar_tpc_server.util.TsoApiConstant;
import com.example.solar_tpc_server.util.TsoMessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import com.example.solar_tpc_server.dto.TsoProjectDto;

@RestController
@RequestMapping(TsoApiConstant.API_PROJECTS)
@RequiredArgsConstructor
public class TsoProjectController {

    private final TsoProjectService projectService;

    @Value("${upload.dir:./uploads/}")
    private String uploadDir;

    @GetMapping("/page")
    public ResponseEntity<TsoApiResponse<Object>> getProjectsPage(Pageable pageable) {
        Page<TsoProjectDto> data = projectService.getProjectsPage(pageable);
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @GetMapping
    public ResponseEntity<TsoApiResponse<Object>> getAllProjects() {
        var data = projectService.getAllProjects();
        // Fallback or handle i18n
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @GetMapping("/featured")
    public ResponseEntity<TsoApiResponse<Object>> getFeaturedProjects() {
        var data = projectService.getFeaturedProjects();
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<TsoApiResponse<Object>> getProjectById(@PathVariable Long projectId) {
        var data = projectService.getProjectById(projectId);
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @PostMapping
    public ResponseEntity<TsoApiResponse<Object>> createProject(@RequestBody TsoProjectDto request) {
        var data = projectService.saveProject(request);
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<TsoApiResponse<Object>> updateProject(@PathVariable Long projectId, @RequestBody TsoProjectDto request) {
        request.setProjectId(projectId);
        var data = projectService.saveProject(request);
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<TsoApiResponse<Object>> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok(TsoApiResponse.success(null, TsoMessageUtil.getMessage("message.success")));
    }

    @PostMapping("/upload")
    public ResponseEntity<TsoApiResponse<Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath);
            
            // Return relative URL that will be served by ResourceHandler
            String imageUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(TsoApiResponse.success(imageUrl, TsoMessageUtil.getMessage("message.success")));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(TsoApiResponse.error(500, "Failed to upload file"));
        }
    }
}
