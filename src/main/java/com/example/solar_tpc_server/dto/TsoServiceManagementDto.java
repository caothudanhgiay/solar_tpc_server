package com.example.solar_tpc_server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TsoServiceManagementDto {

    private Long serviceId;

    @NotBlank(message = "{service.error.code_required}")
    private String serviceCode;

    @NotBlank(message = "{service.error.group_required}")
    private String serviceGroup;

    @NotBlank(message = "{service.error.name_required}")
    private String serviceName;

    @NotBlank(message = "{service.error.type_required}")
    private String serviceType;

    private String serviceImage;

    private Integer serviceStatus;

    private String serviceDescription;

    // For display
    private String serviceStatusName;
}
