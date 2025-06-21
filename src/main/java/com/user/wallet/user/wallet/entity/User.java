package com.user.wallet.user.wallet.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "user")
public class User {

  public User() {
  }

  public User(String name, LocalDate dateOfBirth, String password, List<EmailData> emails, List<PhoneData> phones,
    Account account) {
    this.name = name;
    this.dateOfBirth = dateOfBirth;
    this.password = password;
    this.emails = emails;
    this.phones = phones;
    this.account = account;
  }

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  private String name;
  private LocalDate dateOfBirth;
  private String password;

  @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
  private List<EmailData> emails = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
  private List<PhoneData> phones = new ArrayList<>();

  @OneToOne(mappedBy = "user", cascade = ALL)
  private Account account;
}

