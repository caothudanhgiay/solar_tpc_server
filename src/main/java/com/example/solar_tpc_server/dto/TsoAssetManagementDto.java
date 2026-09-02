package com.example.solar_tpc_server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TsoAssetManagementDto {

    private Long assetId;

    @NotBlank(message = "Nhóm thiết bị không được để trống")
    private String assetGroup;

    @NotBlank(message = "Tên thiết bị không được để trống")
    private String assetName;

    @NotBlank(message = "Loại thiết bị không được để trống")
    private String assetType;

    @NotBlank(message = "Người cấp không được để trống")
    private String provider;

    @NotBlank(message = "Người dùng hiện tại không được để trống")
    private String currentUsr;

    @NotNull(message = "Ngày sử dụng không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày hết hạn không được để trống")
    private LocalDateTime endDate;

    @NotNull(message = "Giá thiết bị không được để trống")
    private BigDecimal price;

    private Integer warrantyPeriod;

    @NotNull(message = "Ngày mua hàng không được để trống")
    private LocalDateTime dateOfPurchase;

    private Integer assetStatus;
    
    // For display
    private String assetStatusName;
}
