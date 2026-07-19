package com.example.solar_tpc_server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TsoProjectStatusEnum implements TsoEnum {
    NOT_STARTED(1, "project.status.not_started", "Chưa thi công"),
    IN_PROGRESS(2, "project.status.in_progress", "Đang thi công"),
    COMPLETED(3, "project.status.completed", "Hoàn thành"),
    CANCELLED(4, "project.status.cancelled", "Đã hủy"),
    TERMINATED(5, "project.status.terminated", "Chấm dứt"),
    STRIKE(6, "project.status.strike", "Đình công"),
    PAUSED(7, "project.status.paused", "Tạm dừng");

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
        return TsoEnum.getNameById(values(), statusId, TsoProjectStatusEnum::getStatusId);
    }
}
