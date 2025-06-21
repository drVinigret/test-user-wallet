package com.user.wallet.user.wallet.service;

import com.user.wallet.user.wallet.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  @Value("${token.signing.key}")
  private String jwtSigningKey;

  /**
   * Извлечение имени пользователя из токена
   *
   * @param token токен
   * @return имя пользователя
   */
  public String extractUserName(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Генерация токена
   *
   * @param user данные пользователя
   * @return токен
   */
  public String generateToken(User user) {
    String email = user.getEmails()
      .stream()
      .findFirst()
      .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"))
      .getEmail();

    Map<String, Object> claims = new HashMap<>();
    claims.put("email", email);
    claims.put("password", user.getPassword());
    return generateToken(claims, user.getName());
  }

  /**
   * Проверка токена на валидность
   *
   * @param token токен
   * @param userDetails данные пользователя
   * @return true, если токен валиден
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String userName = extractUserName(token);
    return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  /**
   * Извлечение данных из токена
   *
   * @param token токен
   * @param claimsResolvers функция извлечения данных
   * @param <T> тип данных
   * @return данные
   */
  private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
    final Claims claims = extractAllClaims(token);
    return claimsResolvers.apply(claims);
  }

  /**
   * Генерация токена
   *
   * @param extraClaims дополнительные данные
   * @param userName имя пользователя
   * @return токен
   */
  private String generateToken(Map<String, Object> extraClaims, String userName) {
    return Jwts.builder().claims(extraClaims).subject(userName)
      .issuedAt(new Date(System.currentTimeMillis()))
      .expiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
      .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
  }

  /**
   * Проверка токена на просроченность
   *
   * @param token токен
   * @return true, если токен просрочен
   */
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Извлечение даты истечения токена
   *
   * @param token токен
   * @return дата истечения
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Извлечение всех данных из токена
   *
   * @param token токен
   * @return данные
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
      .getBody();
  }

  /**
   * Получение ключа для подписи токена
   *
   * @return ключ
   */
  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}