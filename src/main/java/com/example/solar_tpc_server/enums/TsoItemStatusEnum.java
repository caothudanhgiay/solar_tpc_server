package com.example.solar_tpc_server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TsoItemStatusEnum implements TsoEnum {
    ACTIVE(1, "item.status.active", "Đang hoạt động"),
    INACTIVE(2, "item.status.inactive", "Dừng hoạt động"),
    CANCELED(3, "item.status.canceled", "Hủy"),
    DEVELOPING(4, "item.status.developing", "Đang phát triển");

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
        return TsoEnum.getNameById(values(), statusId, TsoItemStatusEnum::getStatusId);
    }
}
