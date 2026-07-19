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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(TsoApiConstant.API_PROJECTS)
@RequiredArgsConstructor
public class TsoProjectController {

    private final TsoProjectService projectService;
    private final com.example.solar_tpc_server.util.TsoFileUtil tsoFileUtil;

    @Value("${upload.dir:./uploads/}")
    private String uploadDir;

    @GetMapping("/page")
    public ResponseEntity<TsoApiResponse<Object>> getProjectsPage(Pageable pageable) {
        Page<TsoProjectDto> page = projectService.getProjectsPage(pageable);
        var statuses = java.util.Arrays.stream(com.example.solar_tpc_server.enums.TsoProjectStatusEnum.values())
            .map(status -> java.util.Map.of(
                "value", status.getStatusId(),
                "labelKey", status.getName()
            ))
            .toList();
        
        var data = java.util.Map.of(
            "page", page,
            "statuses", statuses
        );
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


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TsoApiResponse<Object>> createProject(
            @RequestParam("project") String projectJson,
            @RequestParam(value = "mainFile", required = false) MultipartFile mainFile,
            HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            TsoProjectDto dto = objectMapper.readValue(projectJson, TsoProjectDto.class);
            var data = projectService.saveProjectWithFiles(dto, mainFile, multipartRequest);
            return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(TsoApiResponse.error(400, "Invalid data: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TsoApiResponse<Object>> updateProject(
            @PathVariable Long projectId,
            @RequestParam("project") String projectJson,
            @RequestParam(value = "mainFile", required = false) MultipartFile mainFile,
            HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            TsoProjectDto dto = objectMapper.readValue(projectJson, TsoProjectDto.class);
            dto.setProjectId(projectId);
            var data = projectService.saveProjectWithFiles(dto, mainFile, multipartRequest);
            return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(TsoApiResponse.error(400, "Invalid data: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<TsoApiResponse<Object>> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok(TsoApiResponse.success(null, TsoMessageUtil.getMessage("message.success")));
    }

    @PostMapping("/upload")
    public ResponseEntity<TsoApiResponse<Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectCode") String projectCode,
            @RequestParam(value = "isDetail", defaultValue = "false") boolean isDetail) {
        try {
            String imageUrl = tsoFileUtil.saveImage(file, projectCode, isDetail);
            return ResponseEntity.ok(TsoApiResponse.success(imageUrl, TsoMessageUtil.getMessage("message.success")));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(TsoApiResponse.error(500, "Failed to upload file: " + e.getMessage()));
        }
    }
}
