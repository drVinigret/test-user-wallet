package com.user.wallet.user.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.user.wallet.user.wallet.entity.Account;
import com.user.wallet.user.wallet.entity.User;
import com.user.wallet.user.wallet.exception.AccountNotFoundException;
import com.user.wallet.user.wallet.repository.AccountRepository;
import com.user.wallet.user.wallet.repository.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AccountService accountService;

  private final BigDecimal MIN_DEPOSIT = new BigDecimal("1000.00");
  private final User testUser = new User();
  private final Account testAccount = new Account();

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(accountService, "startDepositAmount", MIN_DEPOSIT);

    testUser.setId(1L);
    testAccount.setId(1L);
    testAccount.setUser(testUser);
    testAccount.setBalance(MIN_DEPOSIT);
  }

  @Test
  void createAccount_Success() {
    BigDecimal initialBalance = new BigDecimal("1500.00");
    when(userRepository.getReferenceById(1L)).thenReturn(testUser);
    when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
    Account result = accountService.createAccount(1L, initialBalance);

    assertNotNull(result);
    assertEquals(testUser, result.getUser());
    verify(accountRepository, times(1)).save(any(Account.class));
  }

  @Test
  void createAccount_ThrowsWhenBalanceBelowMinimum() {
    BigDecimal invalidBalance = new BigDecimal("500.00");
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> accountService.createAccount(1L, invalidBalance)
    );

    assertEquals("Initial balance must be greater than the minimum", exception.getMessage());
    verify(accountRepository, never()).save(any());
  }

  @Test
  void updateBalance_Success() {
    BigDecimal newBalance = new BigDecimal("2000.00");
    when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
    when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
    Account result = accountService.updateBalance(1L, newBalance);

    assertEquals(newBalance, result.getBalance());
    verify(accountRepository, times(1)).save(testAccount);
  }

  @Test
  void updateBalance_ThrowsWhenNegativeBalance() {
    BigDecimal negativeBalance = new BigDecimal("-100.00");
    when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> accountService.updateBalance(1L, negativeBalance)
    );

    assertEquals("Balance cannot be negative", exception.getMessage());
    verify(accountRepository, never()).save(any());
  }

  @Test
  void updateBalance_ThrowsWhenAccountNotFound() {
    when(accountRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
      AccountNotFoundException.class,
      () -> accountService.updateBalance(1L, new BigDecimal("1000.00"))
    );
  }

  @Test
  void getAccount_Success() {
    when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
    Account result = accountService.getAccount(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  void getAccount_ThrowsWhenNotFound() {
    when(accountRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
      AccountNotFoundException.class,
      () -> accountService.getAccount(1L)
    );
  }
}
