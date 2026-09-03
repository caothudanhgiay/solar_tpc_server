package com.example.solar_tpc_server.service;

import com.example.solar_tpc_server.dto.TsoAssetManagementDto;
import com.example.solar_tpc_server.entity.TsoAssetManagement;
import com.example.solar_tpc_server.enums.TsoAssetStatusEnum;
import com.example.solar_tpc_server.exception.TsoAppException;
import com.example.solar_tpc_server.exception.TsoErrorCode;
import com.example.solar_tpc_server.repository.TsoAssetManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;
import com.example.solar_tpc_server.util.TsoFileUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TsoAssetManagementService {

    private final TsoAssetManagementRepository assetManagementRepository;
    private final TsoFileUtil fileUtil;

    public Page<TsoAssetManagementDto> getAssetsPage(String keyword, Pageable pageable) {
        Page<TsoAssetManagement> page = assetManagementRepository.searchAssets(keyword, pageable);
        return page.map(this::convertToDto);
    }

    public List<TsoAssetManagementDto> getAllAssets() {
        return assetManagementRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TsoAssetManagementDto getAssetById(Long assetId) {
        TsoAssetManagement asset = assetManagementRepository.findById(assetId)
                .orElseThrow(() -> new TsoAppException(TsoErrorCode.NOT_FOUND, com.example.solar_tpc_server.util.TsoMessageUtil.getMessage("asset.error.not_found")));
        return convertToDto(asset);
    }

    @Transactional
    public TsoAssetManagementDto saveAsset(TsoAssetManagementDto dto, MultipartFile file) {
        TsoAssetManagement entity = new TsoAssetManagement();
        if (dto.getAssetId() != null) {
            entity = assetManagementRepository.findById(dto.getAssetId())
                    .orElseThrow(() -> new TsoAppException(TsoErrorCode.NOT_FOUND, com.example.solar_tpc_server.util.TsoMessageUtil.getMessage("asset.error.not_found")));
        } else {
            // For new asset, initialize assetImage to avoid NOT NULL violation on flush if no file is uploaded
            entity.setAssetImage("");
        }
        
        BeanUtils.copyProperties(dto, entity, "assetId", "createdAt", "createdDate", "updatedDate", "assetImage");
        
        // Atomic Save: save data first, flush to DB
        TsoAssetManagement savedEntity = assetManagementRepository.saveAndFlush(entity);

        // Then save file
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = fileUtil.saveImage(file, "assets", false);
                savedEntity.setAssetImage(fileName);
                savedEntity = assetManagementRepository.save(savedEntity);
            } catch (java.io.IOException e) {
                throw new TsoAppException(TsoErrorCode.INTERNAL_SERVER_ERROR, "Lỗi lưu file ảnh");
            }
        }
        
        return convertToDto(savedEntity);
    }

    @Transactional
    public void deleteAsset(Long assetId) {
        TsoAssetManagement asset = assetManagementRepository.findById(assetId)
                .orElseThrow(() -> new TsoAppException(TsoErrorCode.NOT_FOUND, com.example.solar_tpc_server.util.TsoMessageUtil.getMessage("asset.error.not_found")));
        assetManagementRepository.delete(asset);
    }

    private TsoAssetManagementDto convertToDto(TsoAssetManagement entity) {
        TsoAssetManagementDto dto = new TsoAssetManagementDto();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getAssetStatus() != null) {
            dto.setAssetStatusName(TsoAssetStatusEnum.getDisplayName(entity.getAssetStatus()));
        }
        return dto;
    }
}
