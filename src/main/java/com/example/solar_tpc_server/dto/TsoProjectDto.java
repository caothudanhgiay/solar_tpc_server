package com.example.solar_tpc_server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TsoProjectDto {
    private Long projectId;

    @NotBlank(message = "Tiêu đề dự án không được để trống")
    private String projectTitle;

    @NotBlank(message = "Tên dự án không được để trống")
    private String projectName;

    private String description;

    @NotBlank(message = "Địa chỉ dự án không được để trống")
    private String projectAddress;

    private java.math.BigDecimal solarPower;
    private java.math.BigDecimal savingPower;
    private Integer processStatus;
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;

    private String featuredImage;
    private Integer isFeatured;
    
    private List<TsoProjectDetailDto> details;
}
