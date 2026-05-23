package com.example.solar_tpc_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TsoUserDto {
    
    private Long userId;
    
    private String username;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    private String email;
    
    private Long accessId;
    
    private Long roleId;
    
    private String createdAt;
    
    private LocalDateTime createdDate;
    
    private LocalDateTime updatedDate;
}
