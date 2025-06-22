package com.user.wallet.user.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.user.wallet.user.wallet.entity.EmailData;
import com.user.wallet.user.wallet.entity.PhoneData;
import com.user.wallet.user.wallet.entity.User;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticateServiceTest {

  @InjectMocks
  private AuthenticateService authenticateService;

  @ParameterizedTest
  @MethodSource("credentialsProvider")
  void validateCredentials_Parameterized(String password, String phone, String email, boolean expected) {
    User user = createTestUser();
    assertEquals(expected,
      authenticateService.validateCredentials(user, password, phone, email));
  }

  private User createTestUser() {
    User user = new User();
    user.setPassword("secure123");

    PhoneData phoneData = new PhoneData();
    phoneData.setPhone("79201234567");
    user.setPhones(List.of(phoneData));

    EmailData emailData = new EmailData();
    emailData.setEmail("user@example.com");
    user.setEmails(List.of(emailData));

    return user;
  }

  private static Stream<Arguments> credentialsProvider() {
    return Stream.of(
      Arguments.of("secure123", "79201234567", null, true),
      Arguments.of("secure123", null, "user@example.com", true),
      Arguments.of("wrong123", "79201234567", null, false)
    );
  }
}