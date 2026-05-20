package com.example.solar_tpc_server.repository;

import com.example.solar_tpc_server.entity.TsoCustomerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TsoCustomerRequestRepository extends JpaRepository<TsoCustomerRequest, Long> {
    Optional<TsoCustomerRequest> findTopByCustomerPhoneOrderByCreatedDateDesc(String customerPhone);
}