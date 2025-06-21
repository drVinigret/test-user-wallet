package com.user.wallet.user.wallet.controller;

import com.user.wallet.user.wallet.schema.JwtAuthenticationResponse;
import com.user.wallet.user.wallet.schema.SignInRequest;
import com.user.wallet.user.wallet.schema.SignUpRequest;
import com.user.wallet.user.wallet.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  @Operation(summary = "Регистрация пользователя")
  @PostMapping("/sign-up")
  public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
    return userService.signUp(request);
  }

  @Operation(summary = "Авторизация пользователя")
  @PostMapping("/sign-in")
  public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
    return userService.signIn(request);
  }
}