package com.example.solar_tpc_server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TsoItemMstDto {

    private Long itemId;

    @NotBlank(message = "Mã mục không được để trống")
    private String itemCode;

    @NotBlank(message = "Mã nhóm mục không được để trống")
    private String groupItemCode;

    @NotBlank(message = "Tên nhóm mục không được để trống")
    private String groupItemName;

    @NotBlank(message = "Mã mục con không được để trống")
    private String itemSubCode;

    @NotBlank(message = "Tên mục con không được để trống")
    private String itemSubName;

    private String itemDescription;

    private Integer serviceStatus;
    
    // For display
    private String serviceStatusName;
}
