package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.service.TsoMenuService;
import com.example.solar_tpc_server.service.TsoProjectService;
import com.example.solar_tpc_server.service.TsoServiceManagementService;
import com.example.solar_tpc_server.util.TsoApiConstant;
import com.example.solar_tpc_server.util.TsoMessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(TsoApiConstant.API_HOME)
@RequiredArgsConstructor
public class TsoHomeController {

    private final TsoMenuService menuService;
    private final TsoProjectService projectService;
    private final TsoServiceManagementService serviceManagementService;

    @GetMapping
    public ResponseEntity<TsoApiResponse<Object>> getHomeData() {
        Map<String, Object> data = Map.of(
                "menus", menuService.getAllEnabledMenus(),
                "projects", projectService.getFeaturedProjects(),
                "services", serviceManagementService.getActiveServices()
        );
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("message.success")));
    }
}
