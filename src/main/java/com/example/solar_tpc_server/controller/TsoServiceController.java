package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.dto.TsoServiceManagementDto;
import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.service.TsoServiceManagementService;
import com.example.solar_tpc_server.util.TsoApiConstant;
import com.example.solar_tpc_server.util.TsoMessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoint công khai (không cần JWT) để trang client lấy danh sách dịch vụ đang hoạt động.
 * Khác với TsoServiceManagementController (/api/v1/services) dùng cho Admin quản trị — yêu cầu đăng nhập.
 */
@RestController
@RequestMapping(TsoApiConstant.API_SERVICES)
@RequiredArgsConstructor
public class TsoServiceController {

    private final TsoServiceManagementService serviceManagementService;

    @GetMapping
    public ResponseEntity<TsoApiResponse<List<TsoServiceManagementDto>>> getActiveServices() {
        List<TsoServiceManagementDto> services = serviceManagementService.getActiveServices();
        return ResponseEntity.ok(TsoApiResponse.success(services, TsoMessageUtil.getMessage("message.success")));
    }
}
