package com.user.wallet.user.wallet.service;

import com.user.wallet.user.wallet.entity.Account;
import com.user.wallet.user.wallet.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private InterestService interestService;


  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(interestService, "amountString", "1000.00");
    interestService.init();
  }

  @Test
  void applyInterest_ShouldAddInterestForEligibleAccounts() {
    Account eligibleAccount1 = new Account();
    eligibleAccount1.setId(1L);
    eligibleAccount1.setBalance(new BigDecimal("1500.00")); // Ниже предела (1000 * 2.07 = 2070)

    Account eligibleAccount2 = new Account();
    eligibleAccount2.setId(2L);
    eligibleAccount2.setBalance(new BigDecimal("2000.00"));

    Account ineligibleAccount = new Account();
    ineligibleAccount.setId(3L);
    ineligibleAccount.setBalance(new BigDecimal("3000.00")); // Выше предела

    List<Account> accounts = Arrays.asList(eligibleAccount1, eligibleAccount2, ineligibleAccount);

    when(accountRepository.findAll()).thenReturn(accounts);

    interestService.applyInterest();

    assertThat(eligibleAccount1.getBalance())
      .isEqualByComparingTo(new BigDecimal("1650.00")); // 1500 + 10% = 1650

    assertThat(eligibleAccount2.getBalance())
      .isEqualByComparingTo(new BigDecimal("2000.00")); // 2000 + 10% = 2200

    assertThat(ineligibleAccount.getBalance())
      .isEqualByComparingTo(new BigDecimal("3000.00"));

    verify(accountRepository, times(1)).findAll();
  }

  @Test
  void applyInterest_ShouldNotAddInterestWhenBalanceAtMax() {
    Account maxBalanceAccount = new Account();
    maxBalanceAccount.setId(1L);
    maxBalanceAccount.setBalance(new BigDecimal("2070.00")); // Ровно предел (1000 * 2.07)

    when(accountRepository.findAll()).thenReturn(List.of(maxBalanceAccount));

    interestService.applyInterest();

    assertThat(maxBalanceAccount.getBalance())
      .isEqualByComparingTo(new BigDecimal("2070.00"));
  }

  @Test
  void applyInterest_ShouldHandleEmptyAccountList() {
    when(accountRepository.findAll()).thenReturn(List.of());

    interestService.applyInterest();

    verify(accountRepository, times(1)).findAll();
  }

  @Test
  void applyInterest_ShouldHandleRoundingCorrectly() {
    Account account = new Account();
    account.setBalance(new BigDecimal("1234.56"));

    when(accountRepository.findAll()).thenReturn(List.of(account));

    interestService.applyInterest();

    assertThat(account.getBalance())
      .isEqualByComparingTo(new BigDecimal("1358.016")); // 1234.56 + 10% = 1358.016
  }
}