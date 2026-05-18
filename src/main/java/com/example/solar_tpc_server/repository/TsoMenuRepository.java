package com.example.solar_tpc_server.repository;

import com.example.solar_tpc_server.entity.TsoMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TsoMenuRepository extends JpaRepository<TsoMenu, Long> {
    List<TsoMenu> findByIsEnabledTrueOrderByMenuDisplayOrderAsc();
}
