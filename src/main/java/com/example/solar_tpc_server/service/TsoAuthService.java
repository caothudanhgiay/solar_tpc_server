package com.example.solar_tpc_server.service;

import com.example.solar_tpc_server.dto.TsoLoginRequestDto;
import com.example.solar_tpc_server.dto.TsoLoginResponseDto;
import com.example.solar_tpc_server.entity.TsoUser;
import com.example.solar_tpc_server.exception.TsoAppException;
import com.example.solar_tpc_server.exception.TsoErrorCode;
import com.example.solar_tpc_server.repository.TsoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TsoAuthService {

    private final TsoUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    public TsoLoginResponseDto login(TsoLoginRequestDto request) {
        TsoUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new TsoAppException(TsoErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new TsoAppException(TsoErrorCode.INVALID_CREDENTIALS);
        }

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("solar_tpc_server")
                .issuedAt(now)
                .expiresAt(now.plusMillis(jwtExpirationMs))
                .subject(user.getUsername())
                .claim("role", user.getRoleId())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

        return TsoLoginResponseDto.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRoleId() != null ? user.getRoleId().toString() : "ADMIN")
                .build();
    }
}
