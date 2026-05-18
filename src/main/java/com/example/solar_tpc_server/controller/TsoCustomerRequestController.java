package com.example.solar_tpc_server.controller;

import com.example.solar_tpc_server.dto.TsoCustomerRequestDto;
import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.service.TsoCustomerRequestService;
import com.example.solar_tpc_server.util.TsoApiConstant;
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
        
        Map<String, String> validationErrors = TSOCustomerRequestValidation.validate(requestDto);
        if (!validationErrors.isEmpty()) {
            TsoApiResponse<Object> apiResponse = TsoApiResponse.<Object>builder()
                    .statusCode(400)
                    .message("Dữ liệu đầu vào không hợp lệ")
                    .data(validationErrors)
                    .build();
            return ResponseEntity.status(400).body(apiResponse);
        }

        service.saveCustomerRequest(requestDto);
        return ResponseEntity.ok(TsoApiResponse.success(null, "Gửi yêu cầu thành công, chúng tôi sẽ liên hệ lại với bạn sớm nhất!"));
    }
}
