package com.example.solar_tpc_server.service;

import com.example.solar_tpc_server.dto.TsoCustomerRequestDto;
import com.example.solar_tpc_server.entity.TsoCustomerRequest;
import com.example.solar_tpc_server.repository.TsoCustomerRequestRepository;
import com.example.solar_tpc_server.util.TSODateUtil;
import com.example.solar_tpc_server.util.TsoConstant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TsoCustomerRequestService {

    private final TsoCustomerRequestRepository repository;
    private final TsoEmailService emailService;

    public void saveCustomerRequest(TsoCustomerRequestDto requestDto) {
        TsoCustomerRequest request = new TsoCustomerRequest();
        request.setCustomerName(requestDto.getCustomerName());
        request.setCustomerPhone(requestDto.getCustomerPhone());
        request.setCustomerEmail(requestDto.getCustomerEmail());
        request.setCustomerAddress(requestDto.getCustomerAddress());
        request.setRequestContent(requestDto.getRequestContent());
        request.setCreatedAt(TsoConstant.SYSTEM);
        request.setCreatedDate(TSODateUtil.datetimeNow());
        request.setUpdatedDate(null);
        repository.save(request);

        // Gửi email thông báo cho khách hàng (async, không block HTTP response)
        emailService.sendCustomerRequestNotify(requestDto);
    }

    public boolean isSpamRequest(String phone) {
        return repository.findTopByCustomerPhoneOrderByCreatedDateDesc(phone)
                .map(lastRequest -> lastRequest.getCreatedDate().plusMinutes(10).isAfter(java.time.LocalDateTime.now()))
                .orElse(false);
    }
}
