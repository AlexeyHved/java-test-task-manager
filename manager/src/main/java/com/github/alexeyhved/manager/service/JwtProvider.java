package com.github.alexeyhved.manager.service;

import com.github.alexeyhved.manager.dto.JwtResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.security.Key;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey jwtAccessSecret;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
    }

    public Mono<JwtResponse> validateAccessToken(JwtResponse jwtResponse) {
        if (validateToken(jwtResponse.getAccessToken())) {
            return Mono.just(jwtResponse);
        }
        return Mono.error(new IllegalAccessException("Invalid access token"));
    }

    public Mono<String> validateAccessToken(String accessToken) {
        if (validateToken(accessToken)) {
            return Mono.just(accessToken);
        }
        return Mono.error(new IllegalAccessException("Invalid access token"));
    }

    public Mono<String> getTokenFromBearer(String bearer) {
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return Mono.just(bearer.substring(7));
        }
        return Mono.error(new JwtException("Invalid authorization bearer"));
    }

    private boolean validateToken(@NonNull String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Mono<Long> getUserIdFromToken(String accessToken) {
        Claims claims = getAccessClaims(accessToken);
        Long userId = Long.valueOf(claims.getSubject());
        return Mono.just(userId);
    }
}
