package com.user.wallet.user.wallet.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TransferRequest {

  public TransferRequest() {
  }

  public TransferRequest(Long toUserId, BigDecimal amount) {
    this.toUserId = toUserId;
    this.amount = amount;
  }

  private Long toUserId;
  private BigDecimal amount;
}

