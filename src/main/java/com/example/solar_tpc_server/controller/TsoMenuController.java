package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.dto.TsoMenuDto;
import com.example.solar_tpc_server.service.TsoMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.solar_tpc_server.util.TsoApiConstant;
import java.util.List;

@RestController
@RequestMapping(TsoApiConstant.API_MENUS)
@RequiredArgsConstructor
public class TsoMenuController {

    private final TsoMenuService tsoMenuService;

    @GetMapping
    public ResponseEntity<TsoApiResponse<List<TsoMenuDto>>> getEnabledMenus() {
        List<TsoMenuDto> menus = tsoMenuService.getAllEnabledMenus();
        return ResponseEntity.ok(TsoApiResponse.success(menus, "Lấy danh sách menu thành công"));
    }
}

