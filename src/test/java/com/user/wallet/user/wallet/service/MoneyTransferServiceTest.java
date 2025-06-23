package com.user.wallet.user.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.user.wallet.user.wallet.entity.Account;
import com.user.wallet.user.wallet.exception.InsufficientFundsException;
import com.user.wallet.user.wallet.model.TransferRequest;
import com.user.wallet.user.wallet.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import javax.security.auth.login.AccountNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MoneyTransferServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private JwtService jwtService;

  @InjectMocks
  private MoneyTransferService moneyTransferService;

  private static final Long FROM_USER_ID = 1L;
  private static final Long TO_USER_ID = 2L;
  private static final String TEST_TOKEN = "testToken";
  private static final BigDecimal TRANSFER_AMOUNT = new BigDecimal("500.00");

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(moneyTransferService, "log", mock(org.apache.logging.log4j.Logger.class));
  }

  @Test
  void transfer_Success() throws AccountNotFoundException {
    TransferRequest request = new TransferRequest(TO_USER_ID, TRANSFER_AMOUNT);
    Account fromAccount = createAccount(FROM_USER_ID, new BigDecimal("1000.00"));
    Account toAccount = createAccount(TO_USER_ID, new BigDecimal("500.00"));

    when(jwtService.extractUserId(any())).thenReturn(FROM_USER_ID);
    when(accountRepository.findByUserIdWithLock(FROM_USER_ID)).thenReturn(Optional.of(fromAccount));
    when(accountRepository.findByUserIdWithLock(TO_USER_ID)).thenReturn(Optional.of(toAccount));

    moneyTransferService.transfer(request, TEST_TOKEN);

    assertEquals(new BigDecimal("500.00"), fromAccount.getBalance());
    assertEquals(new BigDecimal("1000.00"), toAccount.getBalance());
    verify(accountRepository, times(1)).saveAll(List.of(fromAccount, toAccount));
  }

  @Test
  void transfer_ThrowsWhenInsufficientFunds() {
    TransferRequest request = new TransferRequest(TO_USER_ID, TRANSFER_AMOUNT);
    Account fromAccount = createAccount(FROM_USER_ID, new BigDecimal("300.00"));
    Account toAccount = createAccount(TO_USER_ID, new BigDecimal("500.00"));

    when(jwtService.extractUserId(any())).thenReturn(FROM_USER_ID);
    when(accountRepository.findByUserIdWithLock(FROM_USER_ID)).thenReturn(Optional.of(fromAccount));
    when(accountRepository.findByUserIdWithLock(TO_USER_ID)).thenReturn(Optional.of(toAccount));

    assertThrows(InsufficientFundsException.class,
      () -> moneyTransferService.transfer(request, TEST_TOKEN));
  }

  @Test
  void transfer_ThrowsWhenNegativeAmount() {
    TransferRequest request = new TransferRequest(TO_USER_ID, new BigDecimal("-100.00"));
    Account fromAccount = createAccount(FROM_USER_ID, new BigDecimal("1000.00"));
    Account toAccount = createAccount(TO_USER_ID, new BigDecimal("500.00"));

    when(jwtService.extractUserId(any())).thenReturn(FROM_USER_ID);
    when(accountRepository.findByUserIdWithLock(FROM_USER_ID)).thenReturn(Optional.of(fromAccount));
    when(accountRepository.findByUserIdWithLock(TO_USER_ID)).thenReturn(Optional.of(toAccount));

    assertThrows(IllegalArgumentException.class,
      () -> moneyTransferService.transfer(request, TEST_TOKEN));
  }

  @Test
  void transfer_ThrowsWhenTransferToSelf() {
    TransferRequest request = new TransferRequest(FROM_USER_ID, TRANSFER_AMOUNT);
    Account account = createAccount(FROM_USER_ID, new BigDecimal("1000.00"));

    when(jwtService.extractUserId(any())).thenReturn(FROM_USER_ID);
    when(accountRepository.findByUserIdWithLock(FROM_USER_ID)).thenReturn(Optional.of(account));

    assertThrows(IllegalArgumentException.class,
      () -> moneyTransferService.transfer(request, TEST_TOKEN));
  }

  @Test
  void transfer_ThrowsWhenSenderNotFound() {
    TransferRequest request = new TransferRequest(TO_USER_ID, TRANSFER_AMOUNT);

    when(jwtService.extractUserId(any())).thenReturn(FROM_USER_ID);
    when(accountRepository.findByUserIdWithLock(FROM_USER_ID)).thenReturn(Optional.empty());

    assertThrows(AccountNotFoundException.class,
      () -> moneyTransferService.transfer(request, TEST_TOKEN));
  }

  @Test
  void transfer_ThrowsWhenRecipientNotFound() {
    TransferRequest request = new TransferRequest(TO_USER_ID, TRANSFER_AMOUNT);
    Account fromAccount = createAccount(FROM_USER_ID, new BigDecimal("1000.00"));

    when(jwtService.extractUserId(any())).thenReturn(FROM_USER_ID);
    when(accountRepository.findByUserIdWithLock(FROM_USER_ID)).thenReturn(Optional.of(fromAccount));
    when(accountRepository.findByUserIdWithLock(TO_USER_ID)).thenReturn(Optional.empty());

    assertThrows(AccountNotFoundException.class,
      () -> moneyTransferService.transfer(request, TEST_TOKEN));
  }

  private Account createAccount(Long userId, BigDecimal balance) {
    Account account = new Account();
    account.setId(userId);
    account.setBalance(balance);
    return account;
  }
}