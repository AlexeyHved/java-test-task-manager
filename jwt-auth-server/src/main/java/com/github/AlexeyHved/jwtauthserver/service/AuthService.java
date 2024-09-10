package com.github.AlexeyHved.jwtauthserver.service;

import com.github.AlexeyHved.jwtauthserver.dto.JwtRequest;
import com.github.AlexeyHved.jwtauthserver.dto.JwtResponse;

public interface AuthService {
    JwtResponse registerUser(JwtRequest authRequest);
    JwtResponse login(JwtRequest loginReq);
    JwtResponse updateToken(String refreshToken);
    void deleteUser(String bearer);
}
