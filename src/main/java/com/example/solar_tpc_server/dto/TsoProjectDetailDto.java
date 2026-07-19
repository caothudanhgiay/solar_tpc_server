package com.example.solar_tpc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TsoProjectDetailDto {
    private Long projectDetailId;
    private Long projectId;
    private String projectCode;
    private String imageUrl;
    private String content;
    private LocalDate constructionDate;
}
