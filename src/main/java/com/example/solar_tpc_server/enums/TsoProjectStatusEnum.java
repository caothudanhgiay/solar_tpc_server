package com.example.solar_tpc_server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TsoProjectStatusEnum {
    NOT_STARTED(1, "project.status.not_started", "Chưa thi công"),
    IN_PROGRESS(2, "project.status.in_progress", "Đang thi công"),
    COMPLETED(3, "project.status.completed", "Hoàn thành"),
    CANCELLED(4, "project.status.cancelled", "Đã hủy"),
    TERMINATED(5, "project.status.terminated", "Chấm dứt"),
    STRIKE(6, "project.status.strike", "Đình công"),
    PAUSED(7, "project.status.paused", "Tạm dừng");

    private final Integer statusId;
    private final String name; // Khóa đa ngôn ngữ (i18n key)
    private final String name2; // Tên hiển thị thô dự phòng
}
