package com.example.solar_tpc_server.service;

import com.example.solar_tpc_server.dto.TsoUserDto;
import com.example.solar_tpc_server.dto.TsoChangePasswordDto;
import com.example.solar_tpc_server.entity.TsoUser;
import com.example.solar_tpc_server.exception.TsoAppException;
import com.example.solar_tpc_server.exception.TsoErrorCode;
import com.example.solar_tpc_server.repository.TsoUserRepository;
import com.example.solar_tpc_server.util.TsoDateUtil;
import com.example.solar_tpc_server.util.TsoCommonUtil;
import com.example.solar_tpc_server.util.TsoConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TsoUserService {

    private final TsoUserRepository tsoUserRepository;
    private final PasswordEncoder passwordEncoder;

    private TsoUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new TsoAppException(TsoErrorCode.UNAUTHORIZED);
        }
        String username = authentication.getName();
        return tsoUserRepository.findByUsername(username)
                .orElseThrow(() -> new TsoAppException(TsoErrorCode.USER_NOT_EXISTED));
    }

    @Transactional(readOnly = true)
    public List<TsoUserDto> getAllUsers() {
        TsoUser currentUser = getCurrentUser();
        Long currentUserRoleId = currentUser.getRoleId();

        return tsoUserRepository.findAll().stream()
                .filter(user -> {
                    if (currentUserRoleId == 1L) return true; // Root sees all
                    if (currentUserRoleId == 2L) return user.getRoleId() == 2L || user.getRoleId() == 3L; // Admin sees admins and users
                    if (currentUserRoleId == 3L) return user.getRoleId() == 3L; // User sees only users
                    return false; // Guest
                })
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TsoUserDto getUserById(Long id) {
        TsoUser user = tsoUserRepository.findById(id)
                .orElseThrow(() -> new TsoAppException(TsoErrorCode.USER_NOT_EXISTED));
        return convertToDto(user);
    }

    @Transactional
    public TsoUserDto createUser(TsoUserDto dto) {
        TsoUser currentUser = getCurrentUser();
        Long currentUserRoleId = currentUser.getRoleId();

        if (currentUserRoleId == 3L) {
            throw new TsoAppException(TsoErrorCode.FORBIDDEN); // User cannot create
        }
        if (currentUserRoleId == 2L && (dto.getRoleId() == 1L || dto.getRoleId() == 2L)) {
            throw new TsoAppException(TsoErrorCode.FORBIDDEN); // Admin cannot create Root or Admin
        }

        TsoUser user = new TsoUser();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail().trim());
        user.setAccessId(dto.getAccessId());
        user.setRoleId(dto.getRoleId());

        String createAt = TsoCommonUtil.isNotBlank(dto.getCreatedAt()) ? dto.getCreatedAt() : TsoConstant.SYSTEM;
        user.setCreatedAt(createAt);
        user.setCreatedDate(TsoDateUtil.datetimeNow());
        user.setUpdatedDate(null);

        TsoUser savedUser = tsoUserRepository.save(user);
        return convertToDto(savedUser);
    }

    @Transactional
    public TsoUserDto updateUser(Long id, TsoUserDto dto) {
        TsoUser currentUser = getCurrentUser();
        Long currentUserRoleId = currentUser.getRoleId();

        TsoUser user = tsoUserRepository.findById(id)
                .orElseThrow(() -> new TsoAppException(TsoErrorCode.USER_NOT_EXISTED));

        if (currentUserRoleId == 3L) {
            throw new TsoAppException(TsoErrorCode.FORBIDDEN);
        }

        if (currentUserRoleId == 2L) {
            if (user.getRoleId() == 1L || (user.getRoleId() == 2L && !user.getUserId().equals(currentUser.getUserId()))) {
                throw new TsoAppException(TsoErrorCode.FORBIDDEN);
            }
            if (dto.getRoleId() == 1L || (dto.getRoleId() == 2L && !user.getUserId().equals(currentUser.getUserId()))) {
                throw new TsoAppException(TsoErrorCode.FORBIDDEN);
            }
        }

        user.setUsername(dto.getUsername().trim());
        user.setEmail(dto.getEmail().trim());
        user.setAccessId(dto.getAccessId());
        user.setRoleId(dto.getRoleId());

        user.setUpdatedDate(TsoDateUtil.datetimeNow());

        TsoUser updatedUser = tsoUserRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        TsoUser currentUser = getCurrentUser();
        Long currentUserRoleId = currentUser.getRoleId();

        if (currentUserRoleId == 3L) {
            throw new TsoAppException(TsoErrorCode.FORBIDDEN);
        }

        TsoUser targetUser = tsoUserRepository.findById(id)
                .orElseThrow(() -> new TsoAppException(TsoErrorCode.USER_NOT_EXISTED));

        if (currentUserRoleId == 2L) {
            if (targetUser.getRoleId() == 1L || (targetUser.getRoleId() == 2L && !targetUser.getUserId().equals(currentUser.getUserId()))) {
                throw new TsoAppException(TsoErrorCode.FORBIDDEN);
            }
        }

        tsoUserRepository.deleteById(id);
    }

    @Transactional
    public void changePassword(Long id, TsoChangePasswordDto dto) {
        TsoUser user = tsoUserRepository.findById(id)
                .orElseThrow(() -> new TsoAppException(TsoErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác"); // Or a specific exception
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedDate(TsoDateUtil.datetimeNow());
        tsoUserRepository.save(user);
    }

    private TsoUserDto convertToDto(TsoUser user) {
        return TsoUserDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .accessId(user.getAccessId())
                .accessName(user.getAccessId() != null ? com.example.solar_tpc_server.enums.TsoAccessEnum.getDisplayName(user.getAccessId().intValue()) : null)
                .roleId(user.getRoleId())
                .roleName(user.getRoleId() != null ? com.example.solar_tpc_server.enums.TsoRoleEnum.getDisplayName(user.getRoleId().intValue()) : null)
                .createdAt(user.getCreatedAt())
                .createdDate(user.getCreatedDate())
                .updatedDate(user.getUpdatedDate())
                .build();
    }
}
