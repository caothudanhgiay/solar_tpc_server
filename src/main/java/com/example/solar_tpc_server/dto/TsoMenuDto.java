package com.example.solar_tpc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TsoMenuDto {
    private Long menuCode;
    private String menuName;
    private String menuNameEng;
    private String menuUrl;
    private String note;
    private List<TsoMenuDto> subMenus;
}
