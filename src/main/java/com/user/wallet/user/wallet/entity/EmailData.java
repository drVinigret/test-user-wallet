package com.user.wallet.user.wallet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "email_data")
public class EmailData {

  public EmailData() {
  }

  public EmailData(User user, String mail) {
    this.user = user;
    this.email = mail;
  }

  @Id
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(unique = true)
  private String email;
}
