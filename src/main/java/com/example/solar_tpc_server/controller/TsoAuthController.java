package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.dto.TsoLoginRequestDto;
import com.example.solar_tpc_server.dto.TsoLoginResponseDto;
import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.service.TsoAuthService;
import com.example.solar_tpc_server.util.TsoMessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TsoAuthController {

    private final TsoAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TsoApiResponse<Object>> login(@Valid @RequestBody TsoLoginRequestDto request) {
        TsoLoginResponseDto responseDto = authService.login(request);
        return ResponseEntity.ok(TsoApiResponse.success(responseDto, TsoMessageUtil.getMessage("auth.login_success")));
    }
}
