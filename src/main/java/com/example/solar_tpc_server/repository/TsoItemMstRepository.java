package com.example.solar_tpc_server.repository;

import com.example.solar_tpc_server.entity.TsoItemMst;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TsoItemMstRepository extends JpaRepository<TsoItemMst, Long> {

    @Query("SELECT i FROM TsoItemMst i WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(i.itemCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.groupItemName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.itemSubName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<TsoItemMst> searchItems(@Param("keyword") String keyword, Pageable pageable);

    List<TsoItemMst> findByItemCodeAndServiceStatus(String itemCode, Integer serviceStatus);

    List<TsoItemMst> findByGroupItemCodeAndServiceStatus(String groupItemCode, Integer serviceStatus);

}
