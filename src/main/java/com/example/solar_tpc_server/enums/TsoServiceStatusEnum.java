package com.example.solar_tpc_server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TsoServiceStatusEnum implements TsoEnum {
    ACTIVE(1, "service.status.active", "Đang hoạt động"),
    INACTIVE(2, "service.status.inactive", "Dừng hoạt động"),
    CANCELED(3, "service.status.canceled", "Hủy"),
    IN_DEVELOPMENT(4, "service.status.in_development", "Đang phát triển");

    private final Integer statusId;
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

    public static String getDisplayName(Integer statusId) {
        return TsoEnum.getNameById(values(), statusId, TsoServiceStatusEnum::getStatusId);
    }
}
