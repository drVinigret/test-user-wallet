package com.user.wallet.user.wallet;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class AccountDTO implements Serializable {

  public AccountDTO() {
  }

  public AccountDTO(Long accountId, BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance;
  }

  private Long accountId;

  private BigDecimal balance;
}
