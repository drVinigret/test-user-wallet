package com.user.wallet.user.wallet.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "phone_data")
public class PhoneData {

  public PhoneData() {
  }

  public PhoneData(User user, String phone) {
    this.user = user;
    this.phone = phone;
  }

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(unique = true)
  private String phone;  // Формат: 79207865432
}
