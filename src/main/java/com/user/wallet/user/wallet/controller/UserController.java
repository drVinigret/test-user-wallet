package com.user.wallet.user.wallet.controller;

import com.user.wallet.user.wallet.entity.User;
import com.user.wallet.user.wallet.service.JwtService;
import com.user.wallet.user.wallet.service.UserService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final JwtService jwtService;

  @GetMapping("/search")
  public Page<User> searchUsers(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) LocalDate dateOfBirth,
    @RequestParam(required = false) String email,
    @RequestParam(required = false) String phone,
    Pageable pageable
  ) {
    return userService.searchUsers(name, dateOfBirth, email, phone, pageable);
  }

  @PatchMapping("/emails")
  public void addEmails(
    @RequestBody List<String> emails,
    @RequestHeader("Authorization") String token
  ) {
    Long userId = jwtService.extractUserId(token);
    User user = userService.getByUserId(userId);
    userService.addUserEmails(user, emails);
  }

  @PatchMapping("/phones")
  public void addPhones(
    @RequestBody List<String> phones,
    @RequestHeader("Authorization") String token
  ) {
    Long userId = jwtService.extractUserId(token);
    User user = userService.getByUserId(userId);
    userService.addUserPhones(user, phones);
  }
}