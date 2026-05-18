package com.example.solar_tpc_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tso_grp_role")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TsoGrpRole extends TsoMetaData {

    @EmbeddedId
    private TsoGrpRoleId id;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled;

    @Column(name = "note")
    private String note;
}

