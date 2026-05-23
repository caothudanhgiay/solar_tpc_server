package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.dto.TsoUserDto;
import com.example.solar_tpc_server.repository.TsoUserRepository;
import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.service.TsoUserService;
import com.example.solar_tpc_server.util.TsoApiConstant;
import com.example.solar_tpc_server.util.TsoMessageUtil;
import com.example.solar_tpc_server.validation.TSOUserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(TsoApiConstant.API_USERS)
@RequiredArgsConstructor
public class TsoUserController {

    private final TsoUserService tsoUserService;
    private final TsoUserRepository tsoUserRepository;

    @GetMapping
    public ResponseEntity<TsoApiResponse<List<TsoUserDto>>> getAllUsers() {
        List<TsoUserDto> users = tsoUserService.getAllUsers();
        return ResponseEntity.ok(TsoApiResponse.success(users, TsoMessageUtil.getMessage("user.fetch_success")));
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
}
