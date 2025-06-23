package com.user.wallet.user.wallet.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Data
@Table(name = "account")
public class Account {

  public Account() {
  }

  public Account(User user, BigDecimal balance) {
    this.user = user;
    this.balance = balance;
  }

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id")
  @JsonBackReference
  private User user;

  @Column(precision = 19, scale = 2)
  private BigDecimal balance;
}
