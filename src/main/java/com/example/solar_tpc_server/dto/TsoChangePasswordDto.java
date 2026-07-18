package com.example.solar_tpc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TsoChangePasswordDto {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
