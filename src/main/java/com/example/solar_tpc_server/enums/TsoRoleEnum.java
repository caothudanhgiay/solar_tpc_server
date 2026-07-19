package com.example.solar_tpc_server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TsoRoleEnum implements TsoEnum {
    ROOT(1, "role.root", "Root Admin"),
    ADMIN(2, "role.admin", "Administrator"),
    USER(3, "role.user", "Normal User"),
    GUEST(4, "role.guest", "Guest User");

    private final Integer roleId;
    private final String nameKey;
    private final String name2;

    @Override
    public String getNameKey() {
        return nameKey;
    }

    @Override
    public String getName2() {
        return name2;
    }

    public static String getDisplayName(Integer roleId) {
        return TsoEnum.getNameById(values(), roleId, TsoRoleEnum::getRoleId);
    }
}
