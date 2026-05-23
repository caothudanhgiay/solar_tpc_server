package com.example.solar_tpc_server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TsoLoginRequestDto {

    @NotBlank(message = "{validation.username.required}")
    private String username;

    @NotBlank(message = "{validation.password.required}")
    private String password;
}
