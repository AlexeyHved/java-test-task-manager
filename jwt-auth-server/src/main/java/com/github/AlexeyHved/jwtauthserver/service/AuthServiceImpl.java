package com.github.AlexeyHved.jwtauthserver.service;

import com.github.AlexeyHved.jwtauthserver.domain.Role;
import com.github.AlexeyHved.jwtauthserver.dto.JwtRequest;
import com.github.AlexeyHved.jwtauthserver.dto.JwtResponse;
import com.github.AlexeyHved.jwtauthserver.entity.RoleEntity;
import com.github.AlexeyHved.jwtauthserver.entity.UserEntity;
import com.github.AlexeyHved.jwtauthserver.exception.JwtException;
import com.github.AlexeyHved.jwtauthserver.exception.ResourceAlreadyExistEx;
import com.github.AlexeyHved.jwtauthserver.exception.ResourceNotFoundException;
import com.github.AlexeyHved.jwtauthserver.repo.RoleRepo;
import com.github.AlexeyHved.jwtauthserver.repo.UserRepo;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public JwtResponse registerUser(JwtRequest authRequest) {
        if (userRepo.existsByLogin(authRequest.getLogin())) {
            throw new ResourceAlreadyExistEx(String.format("%s already exist", authRequest.getLogin()));
        }
        String pass = passwordEncoder.encode(authRequest.getPass());
        RoleEntity roleUser = roleRepo.findByRole(Role.USER);

        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(authRequest.getLogin());
        userEntity.setPassword(pass);
        userEntity.getRoles().add(roleUser);
        userRepo.save(userEntity);

        String accessToken = jwtProvider.generateAccessToken(userEntity);
        String refreshToken = jwtProvider.generateRefreshToken(userEntity);

        userEntity.setRefreshToken(refreshToken);
        userRepo.save(userEntity);

        return new JwtResponse(accessToken, refreshToken);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public JwtResponse login(JwtRequest loginReq) {
        UserEntity userEntity = userRepo.findByLogin(loginReq.getLogin()).orElseThrow(() ->
                        new ResourceNotFoundException(String.format("User with login %s not found", loginReq.getLogin())));
        if (!passwordEncoder.matches(loginReq.getPass(), userEntity.getPassword())) {
            throw new IllegalArgumentException("Invalid Password");
        }
        String accessToken = jwtProvider.generateAccessToken(userEntity);
        String refreshToken = jwtProvider.generateRefreshToken(userEntity);
        userEntity.setRefreshToken(refreshToken);
        userRepo.save(userEntity);
        return new JwtResponse(accessToken, refreshToken);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public JwtResponse updateToken(String refreshToken) {
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new ResourceAccessException("Invalid refresh token");
        }
        Claims refreshClaims = jwtProvider.getRefreshClaims(refreshToken);
        String id = refreshClaims.getSubject();
        UserEntity userEntity = userRepo.findById(Long.valueOf(id)).orElseThrow(() ->
                new ResourceNotFoundException(String.format("User with id %s not found", id)));
        if (!userEntity.getRefreshToken().equals(refreshToken)) {
            throw new ResourceAccessException("Invalid refresh token");
        }
        String accessToken = jwtProvider.generateAccessToken(userEntity);
        String newRefreshToken = jwtProvider.generateRefreshToken(userEntity);
        userEntity.setRefreshToken(newRefreshToken);
        userRepo.save(userEntity);
        return new JwtResponse(accessToken, newRefreshToken);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteUser(String bearer) {
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            String accessToken = bearer.substring(7);
            if (!jwtProvider.validateAccessToken(accessToken)) throw new JwtException("Invalid Token");
            Claims accessClaims = jwtProvider.getAccessClaims(accessToken);
            String id = accessClaims.getSubject();
            userRepo.deleteById(Long.valueOf(id));
        } else {
            throw new IllegalArgumentException("Invalid authorization header");
        }
    }
}
