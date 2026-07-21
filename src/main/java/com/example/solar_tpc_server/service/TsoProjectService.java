package com.example.solar_tpc_server.service;

import com.example.solar_tpc_server.dto.TsoProjectDetailDto;
import com.example.solar_tpc_server.dto.TsoProjectDto;
import com.example.solar_tpc_server.entity.TsoProject;
import com.example.solar_tpc_server.entity.TsoProjectDetail;
import com.example.solar_tpc_server.repository.TsoProjectDetailRepository;
import com.example.solar_tpc_server.repository.TsoProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.example.solar_tpc_server.enums.TsoProjectStatusEnum;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TsoProjectService {

    private final TsoProjectRepository projectRepository;
    private final TsoProjectDetailRepository projectDetailRepository;
    private final com.example.solar_tpc_server.util.TsoFileUtil tsoFileUtil;

    // Removed configurable URL prefix to strictly use TsoConstant.UPLOAD_PROJECT_DIR
    // which aligns with Nginx /upload/ location.

    public List<TsoProjectDto> getAllProjects() {
        return projectRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<TsoProjectDto> getFeaturedProjects() {
        return projectRepository.findByIsFeatured(1).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public TsoProjectDto getProjectById(Long projectId) {
        TsoProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        TsoProjectDto dto = mapToDto(project);
        
        List<TsoProjectDetail> details = projectDetailRepository.findByProjectId(projectId);
        dto.setDetails(details.stream().map(this::mapDetailToDto).collect(Collectors.toList()));
        return dto;
    }

    public Page<TsoProjectDto> getProjectsPage(Pageable pageable) {
        return projectRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional
    public TsoProjectDto saveProjectWithFiles(TsoProjectDto dto, org.springframework.web.multipart.MultipartFile mainFile, org.springframework.web.multipart.MultipartHttpServletRequest request) throws java.io.IOException {
        TsoProject project;
        if (dto.getProjectId() != null) {
            project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
        } else {
            project = new TsoProject();
        }

        project.setProjectTitle(dto.getProjectTitle());
        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setProjectAddress(dto.getProjectAddress());
        project.setSolarPower(dto.getSolarPower());
        project.setSavingPower(dto.getSavingPower());
        project.setProcessStatus(dto.getProcessStatus());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        
        String featuredFileName = extractFileName(dto.getProjectCode(), dto.getFeaturedImage(), false);
        project.setFeaturedImage(featuredFileName);
        
        project.setIsFeatured(dto.getIsFeatured());
        project.setProjectCode(dto.getProjectCode());

        // Save data to DB first and flush
        project = projectRepository.saveAndFlush(project);

        if (dto.getDetails() != null) {
            projectDetailRepository.deleteByProjectId(project.getProjectId());
            projectDetailRepository.flush();
            for (int i = 0; i < dto.getDetails().size(); i++) {
                TsoProjectDetailDto detailDto = dto.getDetails().get(i);
                TsoProjectDetail detail = new TsoProjectDetail();
                detail.setProjectId(project.getProjectId());
                detail.setProjectCode(project.getProjectCode());
                
                String detailFileName = extractFileName(project.getProjectCode(), detailDto.getImageUrl(), true);
                detail.setImageUrl(detailFileName);
                
                detail.setContent(detailDto.getContent());
                detail.setConstructionDate(detailDto.getConstructionDate());
                
                // Save detail data
                detail = projectDetailRepository.saveAndFlush(detail);
                
                // If there's a new file for this detail, save it
                org.springframework.web.multipart.MultipartFile detailFile = request.getFile("detailFile_" + i);
                if (detailFile != null && !detailFile.isEmpty()) {
                    String savedFileName = tsoFileUtil.saveImage(detailFile, project.getProjectCode(), true);
                    detail.setImageUrl(savedFileName);
                    projectDetailRepository.save(detail);
                }
            }
        }
        
        // If there's a new main file, save it and update entity
        if (mainFile != null && !mainFile.isEmpty()) {
            String savedMainFileName = tsoFileUtil.saveImage(mainFile, project.getProjectCode(), false);
            project.setFeaturedImage(savedMainFileName);
            projectRepository.save(project);
        }

        return getProjectById(project.getProjectId());
    }

    @Transactional
    public TsoProjectDto saveProject(TsoProjectDto dto) {
        TsoProject project;
        if (dto.getProjectId() != null) {
            project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
        } else {
            project = new TsoProject();
        }

        project.setProjectTitle(dto.getProjectTitle());
        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setProjectAddress(dto.getProjectAddress());
        project.setSolarPower(dto.getSolarPower());
        project.setSavingPower(dto.getSavingPower());
        project.setProcessStatus(dto.getProcessStatus());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        
        String featuredFileName = extractFileName(dto.getProjectCode(), dto.getFeaturedImage(), false);
        project.setFeaturedImage(featuredFileName);
        
        project.setIsFeatured(dto.getIsFeatured());

        project = projectRepository.save(project);

        if (dto.getDetails() != null) {
            projectDetailRepository.deleteByProjectId(project.getProjectId());
            for (TsoProjectDetailDto detailDto : dto.getDetails()) {
                TsoProjectDetail detail = new TsoProjectDetail();
                detail.setProjectId(project.getProjectId());
                detail.setProjectCode(project.getProjectCode());
                
                String detailFileName = extractFileName(project.getProjectCode(), detailDto.getImageUrl(), true);
                detail.setImageUrl(detailFileName);
                
                detail.setContent(detailDto.getContent());
                detail.setConstructionDate(detailDto.getConstructionDate());
                projectDetailRepository.save(detail);
            }
        }

        return getProjectById(project.getProjectId());
    }

    @Transactional
    public void deleteProject(Long projectId) {
        projectDetailRepository.deleteByProjectId(projectId);
        projectRepository.deleteById(projectId);
    }

    private TsoProjectDto mapToDto(TsoProject entity) {
        TsoProjectDto dto = new TsoProjectDto();
        dto.setProjectId(entity.getProjectId());
        dto.setProjectCode(entity.getProjectCode());
        dto.setProjectTitle(entity.getProjectTitle());
        dto.setProjectName(entity.getProjectName());
        dto.setDescription(entity.getDescription());
        dto.setProjectAddress(entity.getProjectAddress());
        dto.setSolarPower(entity.getSolarPower());
        dto.setSavingPower(entity.getSavingPower());
        dto.setProcessStatus(entity.getProcessStatus());
        dto.setProcessStatusName(TsoProjectStatusEnum.getDisplayName(entity.getProcessStatus()));
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setFeaturedImage(buildImageUrl(entity.getProjectCode(), entity.getFeaturedImage(), false));
        dto.setIsFeatured(entity.getIsFeatured());
        return dto;
    }

    private TsoProjectDetailDto mapDetailToDto(TsoProjectDetail entity) {
        TsoProjectDetailDto dto = new TsoProjectDetailDto();
        dto.setProjectDetailId(entity.getProjectDetailId());
        dto.setProjectId(entity.getProjectId());
        dto.setProjectCode(entity.getProjectCode());
        dto.setImageUrl(buildImageUrl(entity.getProjectCode(), entity.getImageUrl(), true));
        dto.setContent(entity.getContent());
        dto.setConstructionDate(entity.getConstructionDate());
        return dto;
    }

    private String buildImageUrl(String projectCode, String fileName, boolean isDetail) {
        if (!StringUtils.hasText(fileName)) {
            return fileName;
        }
        String prefix = com.example.solar_tpc_server.util.TsoConstant.UPLOAD_PROJECT_DIR;
        if (fileName.startsWith("http") || fileName.startsWith(prefix)) {
            return fileName;
        }
        // Also handle legacy or accidental /images/products/ in the database
        if (fileName.startsWith("/images/products")) {
             fileName = fileName.substring("/images/products".length());
             if (fileName.startsWith("/")) {
                 fileName = fileName.substring(1);
             }
        }
        String path = prefix + "/" + projectCode + "/";
        if (isDetail) {
            path += "product_detail/";
        }
        return path + fileName;
    }

    private String extractFileName(String projectCode, String url, boolean isDetail) {
        if (!StringUtils.hasText(url)) {
            return url;
        }
        String prefix = com.example.solar_tpc_server.util.TsoConstant.UPLOAD_PROJECT_DIR;
        String fullPrefix = prefix + "/" + projectCode + "/";
        if (isDetail) {
            fullPrefix += "product_detail/";
        }
        if (url.startsWith(fullPrefix)) {
            return url.substring(fullPrefix.length());
        }
        // Extract from legacy URLs just in case
        if (url.startsWith("/images/products/" + projectCode + "/")) {
            String legacyPrefix = "/images/products/" + projectCode + "/";
            if (isDetail) {
                 legacyPrefix += "product_detail/";
            }
            if (url.startsWith(legacyPrefix)) {
                 return url.substring(legacyPrefix.length());
            }
        }
        if (url.contains("/")) {
            return url.substring(url.lastIndexOf('/') + 1);
        }
        return url;
    }
}
