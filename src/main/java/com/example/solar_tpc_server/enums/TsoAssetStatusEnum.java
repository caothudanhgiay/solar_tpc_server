package com.example.solar_tpc_server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TsoAssetStatusEnum implements TsoEnum {
    NOT_USED(1, "asset.status.not_used", "Chưa sử dụng"),
    IN_USE(2, "asset.status.in_use", "Đang sử dụng"),
    STOPPED(3, "asset.status.stopped", "Dừng sử dụng"),
    DAMAGED(4, "asset.status.damaged", "Bị hư hỏng"),
    MAINTENANCE(5, "asset.status.maintenance", "Đang bảo trì"),
    BORROWED(6, "asset.status.borrowed", "Cho mượn"),
    RENTED(7, "asset.status.rented", "Cho thuê"),
    EXPIRED(8, "asset.status.expired", "Đã hết hạn");

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
        return TsoEnum.getNameById(values(), statusId, TsoAssetStatusEnum::getStatusId);
    }
}
