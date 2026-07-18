package com.example.solar_tpc_server.repository;

import com.example.solar_tpc_server.entity.TsoProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TsoProjectRepository extends JpaRepository<TsoProject, Long> {
    List<TsoProject> findByIsFeatured(Integer isFeatured);
}
