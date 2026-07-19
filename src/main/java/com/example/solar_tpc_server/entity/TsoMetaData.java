package com.example.solar_tpc_server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.solar_tpc_server.util.TsoDateUtil;
import com.example.solar_tpc_server.util.TsoConstant;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
public class TsoMetaData {

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = TsoDateUtil.datetimeNow();
        if (createdAt == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
                createdAt = authentication.getName();
            } else {
                createdAt = TsoConstant.SYSTEM; // Fallback value
            }
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = TsoDateUtil.datetimeNow();
    }
}
