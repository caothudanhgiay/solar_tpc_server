package com.example.solar_tpc_server.service;

import com.example.solar_tpc_server.dto.TsoServiceManagementDto;
import com.example.solar_tpc_server.entity.TsoServiceManagement;
import com.example.solar_tpc_server.enums.TsoServiceStatusEnum;
import com.example.solar_tpc_server.exception.TsoAppException;
import com.example.solar_tpc_server.exception.TsoErrorCode;
import com.example.solar_tpc_server.repository.TsoServiceManagementRepository;
import com.example.solar_tpc_server.util.TsoConstant;
import com.example.solar_tpc_server.util.TsoFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TsoServiceManagementService {

    private final TsoServiceManagementRepository serviceManagementRepository;
    private final TsoFileUtil fileUtil;

    public Page<TsoServiceManagementDto> getServicesPage(String keyword, Pageable pageable) {
        Page<TsoServiceManagement> page = serviceManagementRepository.searchServices(keyword, pageable);
        return page.map(this::convertToDto);
    }

    public List<TsoServiceManagementDto> getAllServices() {
        return serviceManagementRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TsoServiceManagementDto> getActiveServices() {
        return serviceManagementRepository.findByServiceStatus(TsoServiceStatusEnum.ACTIVE.getStatusId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TsoServiceManagementDto getServiceById(Long serviceId) {
        TsoServiceManagement service = serviceManagementRepository.findById(serviceId)
                .orElseThrow(() -> new TsoAppException(TsoErrorCode.NOT_FOUND, com.example.solar_tpc_server.util.TsoMessageUtil.getMessage("service.error.not_found")));
        return convertToDto(service);
    }

    @Transactional
    public TsoServiceManagementDto saveService(TsoServiceManagementDto dto, MultipartFile file) {
        TsoServiceManagement entity = new TsoServiceManagement();
        if (dto.getServiceId() != null) {
            entity = serviceManagementRepository.findById(dto.getServiceId())
                    .orElseThrow(() -> new TsoAppException(TsoErrorCode.NOT_FOUND, com.example.solar_tpc_server.util.TsoMessageUtil.getMessage("service.error.not_found")));
        } else {
            entity.setServiceImage(""); // Tránh lỗi NOT NULL khi chưa lưu file
        }
        
        BeanUtils.copyProperties(dto, entity, "serviceId", "createdAt", "createdDate", "updatedDate", "serviceImage");
        
        TsoServiceManagement savedEntity = serviceManagementRepository.saveAndFlush(entity);

        if (file != null && !file.isEmpty()) {
            try {
                String fileName = fileUtil.saveImage(file, "services", false);
                savedEntity.setServiceImage(fileName);
                savedEntity = serviceManagementRepository.save(savedEntity);
            } catch (java.io.IOException e) {
                throw new TsoAppException(TsoErrorCode.INTERNAL_SERVER_ERROR, "Lỗi lưu file ảnh");
            }
        }
        
        return convertToDto(savedEntity);
    }

    @Transactional
    public void deleteService(Long serviceId) {
        TsoServiceManagement service = serviceManagementRepository.findById(serviceId)
                .orElseThrow(() -> new TsoAppException(TsoErrorCode.NOT_FOUND, com.example.solar_tpc_server.util.TsoMessageUtil.getMessage("service.error.not_found")));
        serviceManagementRepository.delete(service);
    }

    private TsoServiceManagementDto convertToDto(TsoServiceManagement entity) {
        TsoServiceManagementDto dto = new TsoServiceManagementDto();
        BeanUtils.copyProperties(entity, dto);
        dto.setServiceImage(buildImageUrl(entity.getServiceImage()));
        if (entity.getServiceStatus() != null) {
            dto.setServiceStatusName(TsoServiceStatusEnum.getDisplayName(entity.getServiceStatus()));
        }
        return dto;
    }

    private String buildImageUrl(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return fileName;
        }
        String prefix = TsoConstant.UPLOAD_PROJECT_DIR;
        if (fileName.startsWith("http") || fileName.startsWith(prefix)) {
            return fileName;
        }
        return prefix + "/services/" + fileName;
    }
}
