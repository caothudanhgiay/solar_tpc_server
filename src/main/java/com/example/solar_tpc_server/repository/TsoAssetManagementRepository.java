package com.example.solar_tpc_server.repository;

import com.example.solar_tpc_server.entity.TsoAssetManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TsoAssetManagementRepository extends JpaRepository<TsoAssetManagement, Long> {

    @Query("SELECT a FROM TsoAssetManagement a WHERE " +
            "(:keyword IS NULL OR LOWER(a.assetName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.assetGroup) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<TsoAssetManagement> searchAssets(@Param("keyword") String keyword, Pageable pageable);
}
