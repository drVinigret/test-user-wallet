package com.user.wallet.user.wallet.service;

import com.user.wallet.user.wallet.entity.Account;
import com.user.wallet.user.wallet.repository.AccountRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterestService {

  private AccountRepository accountRepository;

  public InterestService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Scheduled(fixedRate = 30_000) // 30 секунд
  @Transactional
  public void applyInterest() {
    BigDecimal startDepositAmount = new BigDecimal("20.00");
    List<Account> accounts = accountRepository.findAll();
    accounts.forEach(acc -> {
      BigDecimal maxAllowed = acc.getBalance().multiply(new BigDecimal("2.07"));
      if (acc.getBalance().divide(startDepositAmount, 2, RoundingMode.HALF_UP).compareTo(maxAllowed) < 0) {
        BigDecimal interest = acc.getBalance().multiply(new BigDecimal("0.1"));
        acc.setBalance(acc.getBalance().add(interest));
      }
    });
  }
}