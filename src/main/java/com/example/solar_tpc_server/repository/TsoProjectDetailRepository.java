package com.example.solar_tpc_server.repository;

import com.example.solar_tpc_server.entity.TsoProjectDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TsoProjectDetailRepository extends JpaRepository<TsoProjectDetail, Long> {
    List<TsoProjectDetail> findByProjectId(Long projectId);
    void deleteByProjectId(Long projectId);
}
