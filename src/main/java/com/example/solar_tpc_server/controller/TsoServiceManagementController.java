package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.dto.TsoServiceManagementDto;
import com.example.solar_tpc_server.enums.TsoServiceStatusEnum;
import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.service.TsoServiceManagementService;
import com.example.solar_tpc_server.util.TsoMessageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class TsoServiceManagementController {

    private final TsoServiceManagementService serviceManagementService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/page")
    public ResponseEntity<TsoApiResponse<Object>> getServicesPage(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Page<TsoServiceManagementDto> page = serviceManagementService.getServicesPage(keyword, pageable);
        
        List<Map<String, Object>> statuses = Arrays.stream(TsoServiceStatusEnum.values())
                .map(status -> Map.of(
                        "value", (Object) status.getStatusId(),
                        "labelKey", (Object) status.getName()
                ))
                .collect(Collectors.toList());
        
        Map<String, Object> data = Map.of(
                "page", page,
                "statuses", statuses
        );
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @GetMapping
    public ResponseEntity<TsoApiResponse<Object>> getAllServices() {
        List<TsoServiceManagementDto> data = serviceManagementService.getAllServices();
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<TsoApiResponse<Object>> getServiceById(@PathVariable Long serviceId) {
        TsoServiceManagementDto data = serviceManagementService.getServiceById(serviceId);
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TsoApiResponse<Object>> createService(
            @RequestPart("data") String dataStr,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        TsoServiceManagementDto dto = objectMapper.readValue(dataStr, TsoServiceManagementDto.class);
        TsoServiceManagementDto data = serviceManagementService.saveService(dto, file);
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @PutMapping(value = "/{serviceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TsoApiResponse<Object>> updateService(
            @PathVariable Long serviceId,
            @RequestPart("data") String dataStr,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        TsoServiceManagementDto dto = objectMapper.readValue(dataStr, TsoServiceManagementDto.class);
        dto.setServiceId(serviceId);
        TsoServiceManagementDto data = serviceManagementService.saveService(dto, file);
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<TsoApiResponse<Object>> deleteService(@PathVariable Long serviceId) {
        serviceManagementService.deleteService(serviceId);
        return ResponseEntity.ok(TsoApiResponse.success(null, TsoMessageUtil.getMessage("message.success")));
    }
}
