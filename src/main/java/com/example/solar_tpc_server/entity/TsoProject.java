package com.example.solar_tpc_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tso_project")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TsoProject extends TsoMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_title", nullable = false)
    private String projectTitle;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "project_address", length = 500, nullable = false)
    private String projectAddress;

    @Column(name = "solar_power", precision = 13, scale = 4)
    private java.math.BigDecimal solarPower;

    @Column(name = "saving_power", precision = 13, scale = 4)
    private java.math.BigDecimal savingPower;

    @Column(name = "process_status")
    private Integer processStatus;

    @Column(name = "start_date")
    private java.time.LocalDate startDate;

    @Column(name = "end_date")
    private java.time.LocalDate endDate;

    @Column(name = "featured_image", length = 500)
    private String featuredImage;

    @Column(name = "is_featured")
    private Integer isFeatured;
}
