package com.user.wallet.user.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.user.wallet.user.wallet.entity.User;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

  @InjectMocks
  private JwtService jwtService;

  private static final String SECRET_KEY = "mySecretKey12345678901234567890123456789012";
  private static final Long TEST_USER_ID = 1L;
  private static final String TEST_USERNAME = "testUser";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(jwtService, "jwtSigningKey", SECRET_KEY);
  }

  @Test
  void generateToken_ShouldReturnValidToken() {
    User user = new User();
    user.setId(TEST_USER_ID);
    user.setName(TEST_USERNAME);
    String token = jwtService.generateToken(user);

    assertNotNull(token);
    assertFalse(token.isEmpty());
  }

  @Test
  void extractUserName_ShouldReturnCorrectUsername() {
    String token = generateTestToken();
    String username = jwtService.extractUserName(token);

    assertEquals(TEST_USERNAME, username);
  }

  @Test
  void extractUserId_ShouldReturnCorrectUserId() {
    String token = generateTestToken();
    Long userId = jwtService.extractUserId(token);

    assertEquals(TEST_USER_ID, userId);
  }

  @Test
  void isTokenValid_ShouldReturnTrueForValidToken() {
    String token = generateTestToken();
    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
      TEST_USERNAME, "", new java.util.ArrayList<>());
    boolean isValid = jwtService.isTokenValid(token, userDetails);

    assertTrue(isValid);
  }

  @Test
  void isTokenValid_ShouldReturnFalseForInvalidUser() {
    String token = generateTestToken();
    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
      "wrongUser", "", new java.util.ArrayList<>());
    boolean isValid = jwtService.isTokenValid(token, userDetails);

    assertFalse(isValid);
  }

  private String generateTestToken() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("USER_ID", TEST_USER_ID);

    return Jwts.builder()
      .claims(claims)
      .subject(TEST_USERNAME)
      .issuedAt(new Date())
      .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 час
      .signWith(jwtService.getSigningKey())
      .compact();
  }
}