package com.example.solar_tpc_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tso_asset_management")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TsoAssetManagement extends TsoMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "asset_group", length = 10, nullable = false)
    private String assetGroup;

    @Column(name = "asset_name", length = 500, nullable = false)
    private String assetName;

    @Column(name = "asset_type", length = 10, nullable = false)
    private String assetType;

    @Column(name = "provider", length = 255, nullable = false)
    private String provider;

    @Column(name = "current_usr", length = 255, nullable = false)
    private String currentUsr;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "price", precision = 13, scale = 4, nullable = false)
    private BigDecimal price;

    @Column(name = "warranty_period")
    private Integer warrantyPeriod;

    @Column(name = "date_of_purchase", nullable = false)
    private LocalDateTime dateOfPurchase;

    @Column(name = "asset_status")
    private Integer assetStatus;

}
