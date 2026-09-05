package com.example.solar_tpc_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tso_item_mst")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TsoItemMst extends TsoMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_code", length = 10, nullable = false)
    private String itemCode;

    @Column(name = "group_item_code", length = 10, nullable = false)
    private String groupItemCode;

    @Column(name = "group_item_name", length = 255, nullable = false)
    private String groupItemName;

    @Column(name = "item_sub_code", length = 10, nullable = false)
    private String itemSubCode;

    @Column(name = "item_sub_name", length = 255, nullable = false)
    private String itemSubName;

    @Column(name = "item_description", length = 500)
    private String itemDescription;

    @Column(name = "service_status")
    private Integer serviceStatus;

}
