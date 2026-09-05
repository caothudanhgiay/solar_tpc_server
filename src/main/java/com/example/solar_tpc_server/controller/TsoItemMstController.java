package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.dto.TsoItemMstDto;
import com.example.solar_tpc_server.enums.TsoItemStatusEnum;
import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.service.TsoItemMstService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class TsoItemMstController {

    private final TsoItemMstService itemMstService;

    @GetMapping("/page")
    public TsoApiResponse<Map<String, Object>> getItemsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("itemId").descending());
        Page<TsoItemMstDto> pageData = itemMstService.getItemsPage(keyword, pageable);

        List<Map<String, Object>> statuses = Arrays.stream(TsoItemStatusEnum.values())
                .map(status -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", status.getStatusId());
                    map.put("name", status.getName2());
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("page", pageData);
        response.put("statuses", statuses);

        return TsoApiResponse.success(response);
    }

    @GetMapping("/all")
    public TsoApiResponse<List<TsoItemMstDto>> getAllItems() {
        List<TsoItemMstDto> data = itemMstService.getAllItems();
        return TsoApiResponse.success(data);
    }

    @GetMapping("/groups")
    public TsoApiResponse<List<Map<String, String>>> getGroupsByItemCode(@RequestParam String itemCode) {
        List<Map<String, String>> data = itemMstService.getGroupsByItemCode(itemCode);
        return TsoApiResponse.success(data);
    }

    @GetMapping("/sub-items")
    public TsoApiResponse<List<Map<String, String>>> getSubItemsByGroupCode(@RequestParam String groupItemCode) {
        List<Map<String, String>> data = itemMstService.getSubItemsByGroupCode(groupItemCode);
        return TsoApiResponse.success(data);
    }

    @GetMapping("/{itemId}")
    public TsoApiResponse<TsoItemMstDto> getItemById(@PathVariable Long itemId) {
        TsoItemMstDto data = itemMstService.getItemById(itemId);
        return TsoApiResponse.success(data);
    }

    @PostMapping
    public TsoApiResponse<TsoItemMstDto> createItem(@Valid @RequestBody TsoItemMstDto dto) {
        TsoItemMstDto data = itemMstService.saveItem(dto);
        return TsoApiResponse.success(data);
    }

    @PutMapping("/{itemId}")
    public TsoApiResponse<TsoItemMstDto> updateItem(@PathVariable Long itemId, @Valid @RequestBody TsoItemMstDto dto) {
        dto.setItemId(itemId);
        TsoItemMstDto data = itemMstService.saveItem(dto);
        return TsoApiResponse.success(data);
    }

    @DeleteMapping("/{itemId}")
    public TsoApiResponse<Void> deleteItem(@PathVariable Long itemId) {
        itemMstService.deleteItem(itemId);
        return TsoApiResponse.success(null);
    }
}
