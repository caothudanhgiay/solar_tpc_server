package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.dto.TsoCustomerRequestDto;
import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.service.TsoCustomerRequestService;
import com.example.solar_tpc_server.util.TsoApiConstant;
import com.example.solar_tpc_server.util.TsoMessageUtil;
import com.example.solar_tpc_server.validation.TSOCustomerRequestValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(TsoApiConstant.API_CUSTOMER_REQUESTS)
@RequiredArgsConstructor
public class TsoCustomerRequestController {

    private final TsoCustomerRequestService service;

    @PostMapping
    public ResponseEntity<TsoApiResponse<Object>> createCustomerRequest(@RequestBody TsoCustomerRequestDto requestDto) {
        boolean isSpam = false;
        if (requestDto != null && requestDto.getCustomerPhone() != null && !requestDto.getCustomerPhone().trim().isEmpty()) {
            isSpam = service.isSpamRequest(requestDto.getCustomerPhone().trim());
        }

        Map<String, String> validationErrors = TSOCustomerRequestValidation.validate(requestDto, isSpam);
        if (!validationErrors.isEmpty()) {
            TsoApiResponse<Object> apiResponse = TsoApiResponse.<Object>builder()
                    .statusCode(400)
                    .message(TsoMessageUtil.getMessage("error.invalid_input"))
                    .data(validationErrors)
                    .build();
            return ResponseEntity.status(400).body(apiResponse);
        }

        service.saveCustomerRequest(requestDto);
        return ResponseEntity.ok(TsoApiResponse.success(requestDto, TsoMessageUtil.getMessage("customer_request.success")));
    }
}
