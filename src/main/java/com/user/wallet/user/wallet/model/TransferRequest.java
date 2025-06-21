package com.user.wallet.user.wallet.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TransferRequest {

  public TransferRequest() {
  }

  public TransferRequest(Long fromUserId, Long toUserId, BigDecimal amount) {
    this.fromUserId = fromUserId;
    this.toUserId = toUserId;
    this.amount = amount;
  }

  private Long fromUserId;
  private Long toUserId;
  private BigDecimal amount;
}

