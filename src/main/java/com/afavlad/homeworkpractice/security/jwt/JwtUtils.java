package com.afavlad.homeworkpractice.security.jwt;

import com.afavlad.homeworkpractice.security.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {

  private final JwtProperties jwtProperties;

  /**
   * Ключ для подписи токена (HS256). Делаем ключ из строки yaml.
   */
  private SecretKey signingKey() {
    byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Генерация ACCESS токена.
   */
  public String generateAccessToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", userDetails.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .toList());

    return buildToken(claims, userDetails.getUsername(), jwtProperties.getAccessExpirationMs());
  }

  /**
   * Генерация REFRESH токена.
   */
  public String generateRefreshToken(UserDetails userDetails) {
    Map<String, Object> claims = Map.of("type", "refresh");
    return buildToken(claims, userDetails.getUsername(), jwtProperties.getRefreshExpirationMs());
  }

  private String buildToken(Map<String, Object> claims, String subject, long ttlMs) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + ttlMs);

    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(now)
        .expiration(exp)
        .signWith(signingKey())
        .compact();
  }

  /**
   * Достаём username (sub) из токена.
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Универсальный способ достать любой claim.
   */
  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    Claims claims = extractAllClaims(token);
    return resolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(signingKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * Проверка токена: подпись валидна, не истёк и принадлежит этому пользователю.
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  public boolean isTokenExpired(String token) {
    Date exp = extractClaim(token, Claims::getExpiration);
    return exp.before(new Date());
  }
}
