package com.example.solar_tpc_server.validation;

import com.example.solar_tpc_server.dto.TsoCustomerRequestDto;
import com.example.solar_tpc_server.util.TsoMessageUtil;

import java.util.HashMap;
import java.util.Map;

public class TSOCustomerRequestValidation {

    public static Map<String, String> validate(TsoCustomerRequestDto dto) {
        Map<String, String> errors = new HashMap<>();

        if (dto == null) {
            errors.put("request", TsoMessageUtil.getMessage("validation.request.required"));
            return errors;
        }

        // 1. customer_name: required, max length 255
        if (!TSOValidation.isRequired(dto.getCustomerName())) {
            errors.put("customerName", TsoMessageUtil.getMessage("validation.customerName.required"));
        } else if (!TSOValidation.maxLength(dto.getCustomerName(), 255)) {
            errors.put("customerName", TsoMessageUtil.getMessage("validation.customerName.maxLength"));
        }

        // 2. customer_phone: required, max length 20
        if (!TSOValidation.isRequired(dto.getCustomerPhone())) {
            errors.put("customerPhone", TsoMessageUtil.getMessage("validation.customerPhone.required"));
        } else if (!TSOValidation.maxLength(dto.getCustomerPhone(), 20)) {
            errors.put("customerPhone", TsoMessageUtil.getMessage("validation.customerPhone.maxLength"));
        }

        // 3. customer_email: optional, max length 255, format email
        if (dto.getCustomerEmail() != null && !dto.getCustomerEmail().trim().isEmpty()) {
            if (!TSOValidation.maxLength(dto.getCustomerEmail(), 255)) {
                errors.put("customerEmail", TsoMessageUtil.getMessage("validation.customerEmail.maxLength"));
            } else if (!TSOValidation.isEmail(dto.getCustomerEmail())) {
                errors.put("customerEmail", TsoMessageUtil.getMessage("validation.customerEmail.invalid"));
            }
        }

        // 4. customer_address: optional, max length 255
        if (dto.getCustomerAddress() != null && !dto.getCustomerAddress().trim().isEmpty()) {
            if (!TSOValidation.maxLength(dto.getCustomerAddress(), 255)) {
                errors.put("customerAddress", TsoMessageUtil.getMessage("validation.customerAddress.maxLength"));
            }
        }

        // 5. request_content: required
        if (!TSOValidation.isRequired(dto.getRequestContent())) {
            errors.put("requestContent", TsoMessageUtil.getMessage("validation.requestContent.required"));
        }

        return errors;
    }

    public static Map<String, String> validate(TsoCustomerRequestDto dto, boolean isSpam) {
        Map<String, String> errors = validate(dto);
        if (errors.isEmpty() && isSpam) {
            errors.put("customerPhone", TsoMessageUtil.getMessage("validation.customerPhone.spam"));
        }
        return errors;
    }
}

