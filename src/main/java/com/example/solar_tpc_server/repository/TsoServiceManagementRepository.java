package com.example.solar_tpc_server.repository;

import com.example.solar_tpc_server.entity.TsoServiceManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TsoServiceManagementRepository extends JpaRepository<TsoServiceManagement, Long> {

    @Query("SELECT s FROM TsoServiceManagement s WHERE " +
           "(:keyword IS NULL OR LOWER(s.serviceName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.serviceCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<TsoServiceManagement> searchServices(@Param("keyword") String keyword, Pageable pageable);

    List<TsoServiceManagement> findByServiceStatus(Integer serviceStatus);
}
