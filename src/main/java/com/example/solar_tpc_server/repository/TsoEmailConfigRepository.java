package com.example.solar_tpc_server.repository;

import com.example.solar_tpc_server.entity.TsoEmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TsoEmailConfigRepository extends JpaRepository<TsoEmailConfig, Long> {

    /**
     * Tìm cấu hình email theo config_key và trạng thái is_active.
     * Dùng để lấy config đang hoạt động cho từng loại email.
     *
     * @param configKey khóa định danh (VD: "CUSTOMER_REQUEST_NOTIFY")
     * @param isActive  trạng thái (1 = bật)
     * @return Optional chứa TsoEmailConfig nếu tìm thấy
     */
    Optional<TsoEmailConfig> findByConfigKeyAndIsActive(String configKey, Integer isActive);
}
