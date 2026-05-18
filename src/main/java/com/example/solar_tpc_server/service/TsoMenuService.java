package com.example.solar_tpc_server.service;

import com.example.solar_tpc_server.dto.TsoMenuDto;
import com.example.solar_tpc_server.entity.TsoMenu;
import com.example.solar_tpc_server.repository.TsoMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TsoMenuService {

    private final TsoMenuRepository tsoMenuRepository;

    public List<TsoMenuDto> getAllEnabledMenus() {
        List<TsoMenu> allMenus = tsoMenuRepository.findByIsEnabledTrueOrderByMenuDisplayOrderAsc();

        // Separate root menus and submenus
        List<TsoMenu> rootMenus = allMenus.stream()
                .filter(menu -> menu.getMenuIdParent() == null)
                .collect(Collectors.toList());

        return rootMenus.stream()
                .map(root -> convertToDtoWithChildren(root, allMenus))
                .collect(Collectors.toList());
    }

    private TsoMenuDto convertToDtoWithChildren(TsoMenu menu, List<TsoMenu> allMenus) {
        TsoMenuDto dto = new TsoMenuDto(
                menu.getMenuCode(),
                menu.getMenuName(),
                menu.getMenuNameEng(),
                menu.getMenuUrl(),
                menu.getNote(),
                null
        );

        List<TsoMenuDto> children = allMenus.stream()
                .filter(child -> menu.getMenuCode().equals(child.getMenuIdParent()))
                .map(child -> convertToDtoWithChildren(child, allMenus))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            dto.setSubMenus(children);
        }

        return dto;
    }
}
