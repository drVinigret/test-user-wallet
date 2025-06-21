package com.user.wallet.user.wallet.controller;

import com.user.wallet.user.wallet.model.TransferRequest;
import com.user.wallet.user.wallet.service.MoneyTransferService;
import javax.security.auth.login.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

  private final MoneyTransferService moneyTransferService;

  @PatchMapping("/emails")
  public void addEmails(
    @RequestBody TransferRequest transferRequest,
    @RequestHeader("Authorization") String token
  ) throws AccountNotFoundException {
    moneyTransferService.transfer(transferRequest, token);
  }
}