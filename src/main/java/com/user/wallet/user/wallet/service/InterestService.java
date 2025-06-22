package com.user.wallet.user.wallet.service;

import com.user.wallet.user.wallet.entity.Account;
import com.user.wallet.user.wallet.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterestService {

  private final Logger log = LogManager.getLogger();

  private final AccountRepository accountRepository;

  @Value("${deposit.amount.min}")
  private String amountString;
  private BigDecimal startDepositAmount;

  @PostConstruct
  public void init() {
    this.startDepositAmount = new BigDecimal(amountString);
  }

  @Scheduled(fixedRate = 30_000) // 30 секунд
  @Transactional
  public void applyInterest() {
    log.info("[InterestService] Start task");
    List<Account> accounts = accountRepository.findAll();
    accounts.forEach(acc -> {
      BigDecimal maxAllowed = startDepositAmount.multiply(new BigDecimal("2.07"));
      BigDecimal dividedBalance = acc.getBalance().multiply(new BigDecimal("1.1"));
      if (dividedBalance.compareTo(maxAllowed) < 0) {
        acc.setBalance(dividedBalance);
      } else {
        log.info("[InterestService] Balance is max for account id {}", acc.getId());
      }
    });
    log.info("[InterestService] Finish task");
  }
}