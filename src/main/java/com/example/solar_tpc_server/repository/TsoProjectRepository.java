package com.example.solar_tpc_server.repository;

import com.example.solar_tpc_server.entity.TsoProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TsoProjectRepository extends JpaRepository<TsoProject, Long> {
    List<TsoProject> findByIsFeatured(Integer isFeatured);

    boolean existsByProjectCode(String projectCode);

    boolean existsByProjectCodeAndProjectIdNot(String projectCode, Long projectId);

    @Query("SELECT p FROM TsoProject p WHERE " +
            "(:keyword IS NULL OR LOWER(p.projectName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.projectTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.projectCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<TsoProject> searchProjects(@Param("keyword") String keyword, Pageable pageable);
}
