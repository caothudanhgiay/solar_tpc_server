package com.example.solar_tpc_server.validation;

import com.example.solar_tpc_server.dto.TsoUserDto;
import com.example.solar_tpc_server.repository.TsoUserRepository;
import com.example.solar_tpc_server.util.TsoMessageUtil;

import java.util.HashMap;
import java.util.Map;

public class TSOUserValidation {

    public static Map<String, String> validateForCreate(TsoUserDto dto, TsoUserRepository repository) {
        Map<String, String> errors = new HashMap<>();

        if (dto == null) {
            errors.put("request", TsoMessageUtil.getMessage("validation.request.required"));
            return errors;
        }

        // 1. Username validation
        if (!TSOValidation.isRequired(dto.getUsername())) {
            errors.put("username", TsoMessageUtil.getMessage("validation.username.required"));
        } else if (!TSOValidation.maxLength(dto.getUsername(), 50)) {
            errors.put("username", TsoMessageUtil.getMessage("validation.username.maxLength"));
        } else if (repository.existsByUsername(dto.getUsername().trim())) {
            errors.put("username", TsoMessageUtil.getMessage("validation.username.exists"));
        }

        // 2. Password validation
        if (!TSOValidation.isRequired(dto.getPassword())) {
            errors.put("password", TsoMessageUtil.getMessage("validation.password.required"));
        } else if (!TSOValidation.minLength(dto.getPassword(), 6)) {
            errors.put("password", TsoMessageUtil.getMessage("validation.password.minLength"));
        }

        // 3. Email validation
        if (!TSOValidation.isRequired(dto.getEmail())) {
            errors.put("email", TsoMessageUtil.getMessage("validation.email.required"));
        } else {
            String trimmedEmail = dto.getEmail().trim();
            if (!TSOValidation.maxLength(trimmedEmail, 255)) {
                errors.put("email", TsoMessageUtil.getMessage("validation.email.maxLength"));
            } else if (!TSOValidation.isEmail(trimmedEmail)) {
                errors.put("email", TsoMessageUtil.getMessage("validation.email.invalid"));
            } else if (repository.existsByEmail(trimmedEmail)) {
                errors.put("email", TsoMessageUtil.getMessage("validation.email.exists"));
            }
        }

        // 4. Access ID validation
        if (dto.getAccessId() == null) {
            errors.put("accessId", TsoMessageUtil.getMessage("validation.accessId.required"));
        }

        // 5. Role ID validation
        if (dto.getRoleId() == null) {
            errors.put("roleId", TsoMessageUtil.getMessage("validation.roleId.required"));
        }

        return errors;
    }

    public static Map<String, String> validateForUpdate(Long userId, TsoUserDto dto, TsoUserRepository repository) {
        Map<String, String> errors = new HashMap<>();

        if (dto == null) {
            errors.put("request", TsoMessageUtil.getMessage("validation.request.required"));
            return errors;
        }

        // 1. Username validation
        if (!TSOValidation.isRequired(dto.getUsername())) {
            errors.put("username", TsoMessageUtil.getMessage("validation.username.required"));
        } else if (!TSOValidation.maxLength(dto.getUsername(), 50)) {
            errors.put("username", TsoMessageUtil.getMessage("validation.username.maxLength"));
        } else if (repository.existsByUsernameAndUserIdNot(dto.getUsername().trim(), userId)) {
            errors.put("username", TsoMessageUtil.getMessage("validation.username.exists"));
        }

        // 2. Password validation (Optional for update)
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            if (!TSOValidation.minLength(dto.getPassword(), 6)) {
                errors.put("password", TsoMessageUtil.getMessage("validation.password.minLength"));
            }
        }

        // 3. Email validation
        if (!TSOValidation.isRequired(dto.getEmail())) {
            errors.put("email", TsoMessageUtil.getMessage("validation.email.required"));
        } else {
            String trimmedEmail = dto.getEmail().trim();
            if (!TSOValidation.maxLength(trimmedEmail, 255)) {
                errors.put("email", TsoMessageUtil.getMessage("validation.email.maxLength"));
            } else if (!TSOValidation.isEmail(trimmedEmail)) {
                errors.put("email", TsoMessageUtil.getMessage("validation.email.invalid"));
            } else if (repository.existsByEmailAndUserIdNot(trimmedEmail, userId)) {
                errors.put("email", TsoMessageUtil.getMessage("validation.email.exists"));
            }
        }

        // 4. Access ID validation
        if (dto.getAccessId() == null) {
            errors.put("accessId", TsoMessageUtil.getMessage("validation.accessId.required"));
        }

        // 5. Role ID validation
        if (dto.getRoleId() == null) {
            errors.put("roleId", TsoMessageUtil.getMessage("validation.roleId.required"));
        }

        return errors;
    }
}
