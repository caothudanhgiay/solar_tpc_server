package com.example.solar_tpc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TsoLoginResponseDto {
    private String token;
    private String username;
    private String role;
}
