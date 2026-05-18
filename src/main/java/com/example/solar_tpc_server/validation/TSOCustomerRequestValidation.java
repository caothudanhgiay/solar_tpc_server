package com.example.solar_tpc_server.validation;

import com.example.solar_tpc_server.dto.TsoCustomerRequestDto;

import java.util.HashMap;
import java.util.Map;

public class TSOCustomerRequestValidation {

    public static Map<String, String> validate(TsoCustomerRequestDto dto) {
        Map<String, String> errors = new HashMap<>();

        if (dto == null) {
            errors.put("request", "Dữ liệu yêu cầu không được để trống");
            return errors;
        }

        // 1. customer_name: required, max length 255
        if (!TSOValidation.isRequired(dto.getCustomerName())) {
            errors.put("customerName", "Tên khách hàng không được để trống");
        } else if (!TSOValidation.maxLength(dto.getCustomerName(), 255)) {
            errors.put("customerName", "Tên khách hàng không được vượt quá 255 ký tự");
        }

        // 2. customer_phone: required, max length 20
        if (!TSOValidation.isRequired(dto.getCustomerPhone())) {
            errors.put("customerPhone", "Số điện thoại không được để trống");
        } else if (!TSOValidation.maxLength(dto.getCustomerPhone(), 20)) {
            errors.put("customerPhone", "Số điện thoại không được vượt quá 20 ký tự");
        }

        // 3. customer_email: optional, max length 255, format email
        if (dto.getCustomerEmail() != null && !dto.getCustomerEmail().trim().isEmpty()) {
            if (!TSOValidation.maxLength(dto.getCustomerEmail(), 255)) {
                errors.put("customerEmail", "Email không được vượt quá 255 ký tự");
            } else if (!TSOValidation.isEmail(dto.getCustomerEmail())) {
                errors.put("customerEmail", "Email không đúng định dạng");
            }
        }

        // 4. customer_address: optional, max length 255
        if (dto.getCustomerAddress() != null && !dto.getCustomerAddress().trim().isEmpty()) {
            if (!TSOValidation.maxLength(dto.getCustomerAddress(), 255)) {
                errors.put("customerAddress", "Địa chỉ không được vượt quá 255 ký tự");
            }
        }

        // 5. request_content: required
        if (!TSOValidation.isRequired(dto.getRequestContent())) {
            errors.put("requestContent", "Nội dung yêu cầu không được để trống");
        }

        return errors;
    }
}
