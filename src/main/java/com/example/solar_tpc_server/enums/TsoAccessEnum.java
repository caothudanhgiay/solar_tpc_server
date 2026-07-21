package com.example.solar_tpc_server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TsoAccessEnum implements TsoEnum {
    ROOT(1, "access.root", "All"),
    ADMIN(2, "access.admin", "Các trang quản lí"),
    USER(3, "access.user", "Normal User"),
    GUEST(4, "access.guest", "Guest User");

    private final Integer roleId;
    private final String nameKey; // Khóa đa ngôn ngữ (i18n key)
    private final String name2; // Tên hiển thị thô dự phòng

    @Override
    public String getNameKey() {
        return nameKey;
    }

    @Override
    public String getName2() {
        return name2;
    }

    public static String getDisplayName(Integer roleId) {
        return TsoEnum.getNameById(values(), roleId, TsoAccessEnum::getRoleId);
    }
}
