package com.user.wallet.user.wallet.service.container;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.user.wallet.user.wallet.AccountDTO;
import com.user.wallet.user.wallet.model.TransferRequest;
import com.user.wallet.user.wallet.schema.JwtAuthenticationResponse;
import com.user.wallet.user.wallet.schema.SignUpRequest;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.Jedis;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRegistrationAndMoneyTransferTest {

  private final Logger log = LogManager.getLogger();

  @Value("${deposit.amount.min}")
  private String amountString;
  private BigDecimal transferAmount = new BigDecimal("500.00");
  private BigDecimal startDepositAmount;

  @PostConstruct
  public void init() {
    this.startDepositAmount = new BigDecimal(amountString);
  }

  @Autowired
  private TestRestTemplate restTemplate;

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
    .withDatabaseName("testdb")
    .withUsername("testuser")
    .withPassword("testpass");

  @Container
  static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
    .withExposedPorts(6379)
    .waitingFor(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1));

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");

    registry.add("spring.redis.host", redis::getHost);
    registry.add("spring.redis.port", redis::getFirstMappedPort);
    registry.add("spring.redis.password", () -> "");
  }

  @BeforeEach
  void clearRedis() {
    // Очистка Redis перед каждым тестом
    try (var connection = new Jedis(redis.getHost(), redis.getFirstMappedPort())) {
      connection.flushAll();
    }
  }

  @Test
  void testRedisConnection() {
    // Простая проверка подключения к Redis
    try (var connection = new Jedis(redis.getHost(), redis.getFirstMappedPort())) {
      connection.set("test", "value");
      assertEquals("value", connection.get("test"));
    }
  }

  @Test
  void testUserRegistrationAndMoneyTransfer() {
    // 1. Регистрация первого пользователя
    SignUpRequest user1Request = new SignUpRequest();
    user1Request.setUsername("User1");
    user1Request.setPassword("password123");
    user1Request.setEmail("user1@example.com");
    user1Request.setDateOfBirth(LocalDate.of(1990, 1, 1));

    ResponseEntity<JwtAuthenticationResponse> user1Response = registerUser(user1Request);
    assertEquals(HttpStatus.OK, user1Response.getStatusCode());
    String user1Token = "Bearer " + Objects.requireNonNull(user1Response.getBody()).getToken();
    Long userId1 = user1Response.getBody().getUserId();

    var account1 = getAccountDTO(userId1, user1Token);
    log.info("[TEST_CONTAINER] account1: {}", account1.getBody());

    // 2. Регистрация второго пользователя
    SignUpRequest user2Request = new SignUpRequest();
    user2Request.setUsername("User2");
    user2Request.setPassword("password456");
    user2Request.setEmail("user2@example.com");
    user2Request.setDateOfBirth(LocalDate.of(1995, 5, 5));

    var account2 = getAccountDTO(userId1, user1Token);
    log.info("[TEST_CONTAINER] account1: {}", account2.getBody());

    ResponseEntity<JwtAuthenticationResponse> user2Response = registerUser(user2Request);
    assertEquals(HttpStatus.OK, user2Response.getStatusCode());
    String user2Token = "Bearer " + Objects.requireNonNull(user2Response.getBody()).getToken();
    Long userId2 = user2Response.getBody().getUserId();

    // 3. Выполняем перевод денег
    transferMoney(user1Token, userId2, transferAmount);

    // 4. Проверяем балансы (если есть соответствующий API)
    checkBalance(userId1, user1Token, startDepositAmount.subtract(transferAmount));
    checkBalance(userId2, user2Token, startDepositAmount.add(transferAmount));
  }

  private ResponseEntity<JwtAuthenticationResponse> registerUser(SignUpRequest request) {
    log.info("[TEST_CONTAINER] request to add user: {}", request);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<SignUpRequest> entity = new HttpEntity<>(request, headers);
    var response = restTemplate.exchange(
      "/auth/sign-up",
      HttpMethod.POST,
      entity,
      JwtAuthenticationResponse.class
    );

    log.info("[TEST_CONTAINER] token after add user: {}", response.getBody().getToken());
    return response;
  }

  private void transferMoney(String senderToken, Long toUserId, BigDecimal amount) {
    HttpHeaders headers = createAuthHeaders(senderToken);

    TransferRequest transferRequest = new TransferRequest();
    transferRequest.setToUserId(toUserId);
    transferRequest.setAmount(amount);

    HttpEntity<TransferRequest> entity = new HttpEntity<>(transferRequest, headers);
    ResponseEntity<Void> response = restTemplate.exchange(
      "/accounts/money/transfer",
      HttpMethod.PATCH,
      entity,
      Void.class
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  private HttpHeaders createAuthHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", token);
    return headers;
  }

  private void checkBalance(Long userId, String token, BigDecimal expectedBalance) {
    ResponseEntity<AccountDTO> response = getAccountDTO(userId, token);

    log.info("[TEST_CONTAINER] account: {} with balance: {}", response.getBody().getAccountId(), response.getBody().getBalance());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    AccountDTO accountDto = response.getBody();
    assertNotNull(accountDto);
    assertEquals(0, expectedBalance.compareTo(accountDto.getBalance()));
  }

  private ResponseEntity<AccountDTO> getAccountDTO(Long userId, String token) {
    return restTemplate.exchange(
      "/accounts/by-user/" + userId,
      HttpMethod.GET,
      new HttpEntity<>(createAuthHeaders(token)),
      AccountDTO.class);
  }
}