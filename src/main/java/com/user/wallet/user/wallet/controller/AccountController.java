package com.user.wallet.user.wallet.controller;

import com.user.wallet.user.wallet.AccountDTO;
import com.user.wallet.user.wallet.entity.Account;
import com.user.wallet.user.wallet.model.TransferRequest;
import com.user.wallet.user.wallet.service.AccountService;
import com.user.wallet.user.wallet.service.MoneyTransferService;
import java.math.BigDecimal;
import javax.security.auth.login.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;
  private final MoneyTransferService moneyTransferService;

  @PutMapping("/{accountId}/balance")
  public ResponseEntity<AccountDTO> updateBalance(
    @PathVariable Long accountId,
    @RequestParam BigDecimal newBalance) {
    AccountDTO account = accountService.updateBalance(accountId, newBalance);
    return ResponseEntity.ok(account);
  }

  @GetMapping("/{accountId}")
  public ResponseEntity<AccountDTO> getAccount(@PathVariable Long accountId) {
    AccountDTO account = accountService.getAccount(accountId);
    return ResponseEntity.ok(account);
  }

  @GetMapping("/by-user/{userId}")
  public ResponseEntity<AccountDTO> getAccountByUserId(@PathVariable Long userId) {
    AccountDTO account = accountService.getAccountByUserId(userId);
    return ResponseEntity.ok(account);
  }

  @PatchMapping("/money/transfer")
  public void transferMoney(
    @RequestBody TransferRequest transferRequest,
    @RequestHeader("Authorization") String token
  ) throws AccountNotFoundException {
    moneyTransferService.transfer(transferRequest, token);
  }
}