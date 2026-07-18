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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TsoProjectService {

    private final TsoProjectRepository projectRepository;
    private final TsoProjectDetailRepository projectDetailRepository;

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
        project.setFeaturedImage(dto.getFeaturedImage());
        project.setIsFeatured(dto.getIsFeatured());

        project = projectRepository.save(project);

        if (dto.getDetails() != null) {
            projectDetailRepository.deleteByProjectId(project.getProjectId());
            for (TsoProjectDetailDto detailDto : dto.getDetails()) {
                TsoProjectDetail detail = new TsoProjectDetail();
                detail.setProjectId(project.getProjectId());
                detail.setImageUrl(detailDto.getImageUrl());
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
        dto.setProjectTitle(entity.getProjectTitle());
        dto.setProjectName(entity.getProjectName());
        dto.setDescription(entity.getDescription());
        dto.setProjectAddress(entity.getProjectAddress());
        dto.setSolarPower(entity.getSolarPower());
        dto.setSavingPower(entity.getSavingPower());
        dto.setProcessStatus(entity.getProcessStatus());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setFeaturedImage(entity.getFeaturedImage());
        dto.setIsFeatured(entity.getIsFeatured());
        return dto;
    }

    private TsoProjectDetailDto mapDetailToDto(TsoProjectDetail entity) {
        TsoProjectDetailDto dto = new TsoProjectDetailDto();
        dto.setProjectDetailId(entity.getProjectDetailId());
        dto.setProjectId(entity.getProjectId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setContent(entity.getContent());
        dto.setConstructionDate(entity.getConstructionDate());
        return dto;
    }
}
