package com.example.solar_tpc_server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TsoAccessEnum {
    ROOT(1, "role.root", "Root Admin"),
    ADMIN(2, "role.admin", "Administrator"),
    USER(3, "role.user", "Normal User"),
    GUEST(4, "role.guest", "Guest User");

    private final Integer roleId;
    private final String name; // Khóa đa ngôn ngữ (i18n key)
    private final String name2; // Tên hiển thị thô dự phòng
}
