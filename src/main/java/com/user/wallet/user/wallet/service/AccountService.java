package com.user.wallet.user.wallet.service;

import com.user.wallet.user.wallet.AccountDTO;
import com.user.wallet.user.wallet.entity.Account;
import com.user.wallet.user.wallet.entity.User;
import com.user.wallet.user.wallet.exception.AccountNotFoundException;
import com.user.wallet.user.wallet.repository.AccountRepository;
import com.user.wallet.user.wallet.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  @Value("${deposit.amount.min}")
  private String amountString;
  private BigDecimal startDepositAmount;

  @PostConstruct
  public void init() {
    this.startDepositAmount = new BigDecimal(amountString);
  }

  @Transactional
  public Account createAccount(Long userId, BigDecimal initialBalance) {
    User user = userRepository.getReferenceById(userId);

    if (initialBalance.compareTo(startDepositAmount) < 0) {
      throw new IllegalArgumentException("Initial balance must be greater than the minimum");
    }

    Account account = new Account(user, initialBalance);
    return accountRepository.save(account);
  }

  @Transactional
  public AccountDTO updateBalance(Long accountId, BigDecimal newBalance) {
    Account account = accountRepository.findById(accountId)
      .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Balance cannot be negative");
    }

    account.setBalance(newBalance);
    var savedAccount = accountRepository.save(account);
    return new AccountDTO(account.getId(), account.getBalance());
  }

  @Transactional(readOnly = true)
  public AccountDTO getAccount(Long accountId) {
    return accountRepository.findById(accountId)
      .map(account -> new AccountDTO(account.getId(), account.getBalance()))
      .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));
  }

  @Transactional(readOnly = true)
  public AccountDTO getAccountByUserId(Long userId) {
    return accountRepository.findByUserId(userId)
      .map(account -> new AccountDTO(account.getId(), account.getBalance()))
      .orElseThrow(() -> new AccountNotFoundException("Account not found by userId: " + userId));
  }
}
