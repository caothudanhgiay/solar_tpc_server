package com.example.solar_tpc_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tso_menu")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TsoMenu extends TsoMetaData {

    @Id
    @Column(name = "menu_code")
    private Long menuCode;

    @Column(name = "menu_name", nullable = false)
    private String menuName;

    @Column(name = "menu_name_eng")
    private String menuNameEng;

    @Column(name = "menu_url", nullable = false)
    private String menuUrl;

    @Column(name = "menu_id_parent")
    private Long menuIdParent;

    @Column(name = "menu_display_order", nullable = false)
    private Integer menuDisplayOrder;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled;

    @Column(name = "note")
    private String note;
}

