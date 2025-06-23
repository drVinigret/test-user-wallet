package com.user.wallet.user.wallet.service;

import com.user.wallet.user.wallet.entity.Account;
import com.user.wallet.user.wallet.exception.InsufficientFundsException;
import com.user.wallet.user.wallet.model.TransferRequest;
import com.user.wallet.user.wallet.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javax.security.auth.login.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MoneyTransferService {

  private final AccountRepository accountRepository;
  private final JwtService jwtService;
  private final ReentrantLock transferLock = new ReentrantLock();

  private final Logger log = LogManager.getLogger();

  public static final String BEARER_PREFIX = "Bearer ";

  @Transactional
  public void transfer(TransferRequest transferRequest, String token) throws AccountNotFoundException {
    log.info("[MoneyTransferService] Transfer request: {}", transferRequest);
    transferLock.lock();
    try {
      var jwt = token.substring(BEARER_PREFIX.length());
      Long userId = jwtService.extractUserId(jwt);
      Account fromAccount = accountRepository.findByUserIdWithLock(userId)
        .orElseThrow(() -> new AccountNotFoundException("Sender account not found"));
      Account toAccount = accountRepository.findByUserIdWithLock(transferRequest.getToUserId())
        .orElseThrow(() -> new AccountNotFoundException("Recipient account not found"));

      validateTransfer(fromAccount, toAccount, transferRequest.getAmount());

      fromAccount.setBalance(fromAccount.getBalance().subtract(transferRequest.getAmount()));
      toAccount.setBalance(toAccount.getBalance().add(transferRequest.getAmount()));

      log.info("[MoneyTransferService] Transfer amount {} from {} to {}",
        transferRequest.getAmount(), fromAccount, toAccount);
      accountRepository.saveAll(List.of(fromAccount, toAccount));
      log.info("[MoneyTransferService] Finish transfer: {}", transferRequest);
    } finally {
      transferLock.unlock();
    }
  }

  private void validateTransfer(Account fromAccount, Account toAccount, BigDecimal amount) {
    // Проверка на отрицательный баланс
    if (fromAccount.getBalance().compareTo(amount) < 0) {
      throw new InsufficientFundsException("Not enough money for transfer");
    }

    // Проверка на отрицательную сумму перевода
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Transfer amount must be positive");
    }

    // Проверка на перевод самому себе
    if (fromAccount.getId().equals(toAccount.getId())) {
      throw new IllegalArgumentException("Cannot transfer to yourself");
    }
  }
}