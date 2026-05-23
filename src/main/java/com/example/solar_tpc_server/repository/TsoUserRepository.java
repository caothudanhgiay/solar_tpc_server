package com.example.solar_tpc_server.repository;

import com.example.solar_tpc_server.entity.TsoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TsoUserRepository extends JpaRepository<TsoUser, Long> {
    
    Optional<TsoUser> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndUserIdNot(String username, Long userId);

    boolean existsByEmailAndUserIdNot(String email, Long userId);
}
