package com.example.solar_tpc_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tso_service_management")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TsoServiceManagement extends TsoMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "service_code", length = 10, nullable = false)
    private String serviceCode;

    @Column(name = "service_group", length = 10, nullable = false)
    private String serviceGroup;

    @Column(name = "service_name", length = 255, nullable = false)
    private String serviceName;

    @Column(name = "service_type", length = 10, nullable = false)
    private String serviceType;

    @Column(name = "service_image", length = 500, nullable = false)
    private String serviceImage;

    @Column(name = "service_status")
    private Integer serviceStatus;

    @Column(name = "service_description", length = 500)
    private String serviceDescription;

}
