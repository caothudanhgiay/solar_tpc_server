package com.example.solar_tpc_server.validation;

import com.example.solar_tpc_server.dto.TsoChatbotDto.TsoChatbotRequestDto;
import com.example.solar_tpc_server.util.TsoMessageUtil;

import java.util.HashMap;
import java.util.Map;

public class TsoChatBotValidation {

    public static Map<String, String> validateAsk(TsoChatbotRequestDto request) {
        Map<String, String> errors = new HashMap<>();

        if (request == null) {
            errors.put("request", TsoMessageUtil.getMessage("error.invalid_input"));
            return errors;
        }

        if (!TSOValidation.isRequired(request.getMessage())) {
            errors.put("message", TsoMessageUtil.getMessage("error.invalid_input"));
        }

        return errors;
    }
}
