package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.dto.TsoAssetManagementDto;
import com.example.solar_tpc_server.enums.TsoAssetStatusEnum;
import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.service.TsoAssetManagementService;
import com.example.solar_tpc_server.util.TsoMessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/assets") // Or use TsoApiConstant if defined
@RequiredArgsConstructor
public class TsoAssetManagementController {

    private final TsoAssetManagementService assetManagementService;

    @GetMapping("/page")
    public ResponseEntity<TsoApiResponse<Object>> getAssetsPage(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Page<TsoAssetManagementDto> page = assetManagementService.getAssetsPage(keyword, pageable);
        
        List<Map<String, Object>> statuses = Arrays.stream(TsoAssetStatusEnum.values())
                .map(status -> Map.of(
                        "value", (Object) status.getStatusId(),
                        "labelKey", (Object) status.getNameKey()
                ))
                .collect(Collectors.toList());
        
        Map<String, Object> data = Map.of(
                "page", page,
                "statuses", statuses
        );
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @GetMapping
    public ResponseEntity<TsoApiResponse<Object>> getAllAssets() {
        List<TsoAssetManagementDto> data = assetManagementService.getAllAssets();
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @GetMapping("/{assetId}")
    public ResponseEntity<TsoApiResponse<Object>> getAssetById(@PathVariable Long assetId) {
        TsoAssetManagementDto data = assetManagementService.getAssetById(assetId);
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @PostMapping
    public ResponseEntity<TsoApiResponse<Object>> createAsset(@Valid @RequestBody TsoAssetManagementDto dto) {
        TsoAssetManagementDto data = assetManagementService.saveAsset(dto);
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @PutMapping("/{assetId}")
    public ResponseEntity<TsoApiResponse<Object>> updateAsset(
            @PathVariable Long assetId,
            @Valid @RequestBody TsoAssetManagementDto dto) {
        dto.setAssetId(assetId);
        TsoAssetManagementDto data = assetManagementService.saveAsset(dto);
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @DeleteMapping("/{assetId}")
    public ResponseEntity<TsoApiResponse<Object>> deleteAsset(@PathVariable Long assetId) {
        assetManagementService.deleteAsset(assetId);
        return ResponseEntity.ok(TsoApiResponse.success(null, TsoMessageUtil.getMessage("message.success")));
    }
}
