package com.example.solar_tpc_server.service;

import com.example.solar_tpc_server.dto.TsoUserDto;
import com.example.solar_tpc_server.entity.TsoUser;
import com.example.solar_tpc_server.exception.TsoAppException;
import com.example.solar_tpc_server.exception.TsoErrorCode;
import com.example.solar_tpc_server.repository.TsoUserRepository;
import com.example.solar_tpc_server.util.TSODateUtil;
import com.example.solar_tpc_server.util.TsoCommonUtil;
import com.example.solar_tpc_server.util.TsoConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TsoUserService {

    private final TsoUserRepository tsoUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<TsoUserDto> getAllUsers() {
        return tsoUserRepository.findAll().stream()
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
        TsoUser user = new TsoUser();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail().trim());
        user.setAccessId(dto.getAccessId());
        user.setRoleId(dto.getRoleId());

        String createAt = TsoCommonUtil.isNotBlank(dto.getCreatedAt()) ? dto.getCreatedAt() : TsoConstant.SYSTEM;
        user.setCreatedAt(createAt);
        user.setCreatedDate(TSODateUtil.datetimeNow());
        user.setUpdatedDate(null);

        TsoUser savedUser = tsoUserRepository.save(user);
        return convertToDto(savedUser);
    }

    @Transactional
    public TsoUserDto updateUser(Long id, TsoUserDto dto) {
        TsoUser user = tsoUserRepository.findById(id)
                .orElseThrow(() -> new TsoAppException(TsoErrorCode.USER_NOT_EXISTED));

        user.setUsername(dto.getUsername().trim());
        user.setEmail(dto.getEmail().trim());
        user.setAccessId(dto.getAccessId());
        user.setRoleId(dto.getRoleId());

        // Password update is optional
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user.setUpdatedDate(TSODateUtil.datetimeNow());

        TsoUser updatedUser = tsoUserRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!tsoUserRepository.existsById(id)) {
            throw new TsoAppException(TsoErrorCode.USER_NOT_EXISTED);
        }
        tsoUserRepository.deleteById(id);
    }

    private TsoUserDto convertToDto(TsoUser user) {
        return TsoUserDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .accessId(user.getAccessId())
                .roleId(user.getRoleId())
                .createdAt(user.getCreatedAt())
                .createdDate(user.getCreatedDate())
                .updatedDate(user.getUpdatedDate())
                .build();
    }
}
