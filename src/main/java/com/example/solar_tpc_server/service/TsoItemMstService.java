package com.example.solar_tpc_server.service;

import com.example.solar_tpc_server.dto.TsoItemMstDto;
import com.example.solar_tpc_server.entity.TsoItemMst;
import com.example.solar_tpc_server.enums.TsoItemStatusEnum;
import com.example.solar_tpc_server.exception.TsoAppException;
import com.example.solar_tpc_server.exception.TsoErrorCode;
import com.example.solar_tpc_server.repository.TsoItemMstRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TsoItemMstService {

    private final TsoItemMstRepository itemMstRepository;

    public Page<TsoItemMstDto> getItemsPage(String keyword, Pageable pageable) {
        Page<TsoItemMst> page = itemMstRepository.searchItems(keyword, pageable);
        return page.map(this::convertToDto);
    }

    public List<TsoItemMstDto> getAllItems() {
        return itemMstRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<Map<String, String>> getGroupsByItemCode(String itemCode) {
        List<TsoItemMst> items = itemMstRepository.findByItemCodeAndServiceStatus(itemCode, TsoItemStatusEnum.ACTIVE.getStatusId());
        
        // Distinct groupItemCode and groupItemName
        Map<String, String> distinctGroups = new HashMap<>();
        for (TsoItemMst item : items) {
            distinctGroups.putIfAbsent(item.getGroupItemCode(), item.getGroupItemName());
        }
        
        return distinctGroups.entrySet().stream()
                .map(entry -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("groupItemCode", entry.getKey());
                    map.put("groupItemName", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, String>> getSubItemsByGroupCode(String groupItemCode) {
        List<TsoItemMst> items = itemMstRepository.findByGroupItemCodeAndServiceStatus(groupItemCode, TsoItemStatusEnum.ACTIVE.getStatusId());
        return items.stream()
                .map(item -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("itemSubCode", item.getItemSubCode());
                    map.put("itemSubName", item.getItemSubName());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public TsoItemMstDto getItemById(Long itemId) {
        TsoItemMst item = itemMstRepository.findById(itemId)
                .orElseThrow(() -> new TsoAppException(TsoErrorCode.NOT_FOUND, "Không tìm thấy danh mục với ID: " + itemId));
        return convertToDto(item);
    }

    @Transactional
    public TsoItemMstDto saveItem(TsoItemMstDto dto) {
        TsoItemMst entity = new TsoItemMst();
        if (dto.getItemId() != null) {
            entity = itemMstRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new TsoAppException(TsoErrorCode.NOT_FOUND, "Không tìm thấy danh mục để cập nhật"));
        }

        entity.setItemCode(dto.getItemCode());
        entity.setGroupItemCode(dto.getGroupItemCode());
        entity.setGroupItemName(dto.getGroupItemName());
        entity.setItemSubCode(dto.getItemSubCode());
        entity.setItemSubName(dto.getItemSubName());
        entity.setItemDescription(dto.getItemDescription());
        entity.setServiceStatus(dto.getServiceStatus());

        TsoItemMst savedEntity = itemMstRepository.saveAndFlush(entity);
        return convertToDto(savedEntity);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        TsoItemMst item = itemMstRepository.findById(itemId)
                .orElseThrow(() -> new TsoAppException(TsoErrorCode.NOT_FOUND, "Không tìm thấy danh mục với ID: " + itemId));
        itemMstRepository.delete(item);
    }

    private TsoItemMstDto convertToDto(TsoItemMst entity) {
        TsoItemMstDto dto = new TsoItemMstDto();
        dto.setItemId(entity.getItemId());
        dto.setItemCode(entity.getItemCode());
        dto.setGroupItemCode(entity.getGroupItemCode());
        dto.setGroupItemName(entity.getGroupItemName());
        dto.setItemSubCode(entity.getItemSubCode());
        dto.setItemSubName(entity.getItemSubName());
        dto.setItemDescription(entity.getItemDescription());
        dto.setServiceStatus(entity.getServiceStatus());
        if (entity.getServiceStatus() != null) {
            dto.setServiceStatusName(TsoItemStatusEnum.getDisplayName(entity.getServiceStatus()));
        }
        return dto;
    }
}
