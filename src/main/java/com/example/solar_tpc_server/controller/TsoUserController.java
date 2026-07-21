package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.dto.TsoUserDto;
import com.example.solar_tpc_server.dto.TsoChangePasswordDto;
import com.example.solar_tpc_server.enums.TsoAccessEnum;
import com.example.solar_tpc_server.enums.TsoRoleEnum;
import com.example.solar_tpc_server.repository.TsoUserRepository;
import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.service.TsoUserService;
import com.example.solar_tpc_server.util.TsoApiConstant;
// import com.example.solar_tpc_server.util.TsoExcelExportUtil;
import com.example.solar_tpc_server.util.TsoMessageUtil;
import com.example.solar_tpc_server.validation.TSOUserValidation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(TsoApiConstant.API_USERS)
@RequiredArgsConstructor
public class TsoUserController {

    private final TsoUserService tsoUserService;
    private final TsoUserRepository tsoUserRepository;

    @GetMapping
    public ResponseEntity<TsoApiResponse<Object>> getAllUsers() {
        List<TsoUserDto> users = tsoUserService.getAllUsers();
        
        var roles = java.util.Arrays.stream(com.example.solar_tpc_server.enums.TsoRoleEnum.values())
                .map(r -> Map.of(
                        "value", r.getRoleId(),
                        "labelKey", r.getName()))
                .collect(java.util.stream.Collectors.toList());
                
        var accesses = java.util.Arrays.stream(com.example.solar_tpc_server.enums.TsoAccessEnum.values())
                .map(a -> Map.of(
                        "value", a.getRoleId(),
                        "labelKey", a.getName()))
                .collect(java.util.stream.Collectors.toList());
                
        var data = Map.of(
                "page", Map.of("content", users),
                "roles", roles, 
                "accesses", accesses
        );
                
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("user.fetch_success")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TsoApiResponse<TsoUserDto>> getUserById(@PathVariable Long id) {
        TsoUserDto user = tsoUserService.getUserById(id);
        return ResponseEntity.ok(TsoApiResponse.success(user, TsoMessageUtil.getMessage("user.get_success")));
    }

    @PostMapping
    public ResponseEntity<TsoApiResponse<Object>> createUser(@RequestBody TsoUserDto dto) {
        Map<String, String> validationErrors = TSOUserValidation.validateForCreate(dto, tsoUserRepository);
        if (!validationErrors.isEmpty()) {
            TsoApiResponse<Object> apiResponse = TsoApiResponse.<Object>builder()
                    .statusCode(400)
                    .message(TsoMessageUtil.getMessage("error.invalid_input"))
                    .data(validationErrors)
                    .build();
            return ResponseEntity.status(400).body(apiResponse);
        }

        TsoUserDto createdUser = tsoUserService.createUser(dto);
        return ResponseEntity.ok(TsoApiResponse.success(createdUser, TsoMessageUtil.getMessage("user.create_success")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TsoApiResponse<Object>> updateUser(@PathVariable Long id, @RequestBody TsoUserDto dto) {
        Map<String, String> validationErrors = TSOUserValidation.validateForUpdate(id, dto, tsoUserRepository);
        if (!validationErrors.isEmpty()) {
            TsoApiResponse<Object> apiResponse = TsoApiResponse.<Object>builder()
                    .statusCode(400)
                    .message(TsoMessageUtil.getMessage("error.invalid_input"))
                    .data(validationErrors)
                    .build();
            return ResponseEntity.status(400).body(apiResponse);
        }

        TsoUserDto updatedUser = tsoUserService.updateUser(id, dto);
        return ResponseEntity.ok(TsoApiResponse.success(updatedUser, TsoMessageUtil.getMessage("user.update_success")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TsoApiResponse<Object>> deleteUser(@PathVariable Long id) {
        tsoUserService.deleteUser(id);
        return ResponseEntity.ok(TsoApiResponse.success(null, TsoMessageUtil.getMessage("user.delete_success")));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<TsoApiResponse<Object>> changePassword(@PathVariable Long id,
            @RequestBody TsoChangePasswordDto dto) {
        tsoUserService.changePassword(id, dto);
        return ResponseEntity.ok(TsoApiResponse.success(null, "Thay đổi mật khẩu thành công"));
    }

    /**
     * Xuất danh sách người dùng ra file Excel (.xlsx).
     * GET /api/users/export
     */
    /*
    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) throws IOException {
        List<TsoUserDto> users = tsoUserService.getAllUsers();
        TsoExcelExportUtil.export(
                response,
                "danh_sach_nguoi_dung",
                List.of("ID", "T\u00e0i kho\u1ea3n", "Email", "Quy\u1ec1n h\u1ea1n", "Access ID"),
                users,
                (row, u) -> {
                    row.createCell(0).setCellValue(u.getUserId() != null ? u.getUserId() : 0);
                    row.createCell(1).setCellValue(u.getUsername() != null ? u.getUsername() : "");
                    row.createCell(2).setCellValue(u.getEmail() != null ? u.getEmail() : "");
                    row.createCell(3).setCellValue(u.getRoleId() != null ? u.getRoleId().toString() : "");
                    row.createCell(4).setCellValue(u.getAccessId() != null ? u.getAccessId().toString() : "");
                });
    }
    */
}
