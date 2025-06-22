package com.user.wallet.user.wallet.service;

import com.user.wallet.user.wallet.entity.EmailData;
import com.user.wallet.user.wallet.entity.PhoneData;
import com.user.wallet.user.wallet.entity.User;
import com.user.wallet.user.wallet.schema.JwtAuthenticationResponse;
import com.user.wallet.user.wallet.schema.SignInRequest;
import com.user.wallet.user.wallet.schema.SignUpRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateService {

  private final Logger log = LogManager.getLogger();

  private final UserService userService;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public JwtAuthenticationResponse signUp(SignUpRequest request) {
    var user = new User();
    user.setName(request.getUsername());
    user.setPassword(request.getPassword());
    EmailData email = new EmailData();
    email.setEmail(request.getEmail());
    user.setEmails(List.of(email));
    userService.create(user);

    var jwt = jwtService.generateToken(user);
    return new JwtAuthenticationResponse(jwt);
  }

  public JwtAuthenticationResponse signIn(SignInRequest request) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
      request.getUsername(),
      request.getPassword()
    ));

    var user = userService.getByUsername(request.getUsername());

    if (!validateCredentials(user, request.getPassword(), request.getPhoneNumber(), request.getEmail())) {
      log.debug("[AuthenticateService] Invalid credentials for user {}", request.getUsername());
      throw new RuntimeException("Credentials not valid");
    }

    var jwt = jwtService.generateToken(user);
    return new JwtAuthenticationResponse(jwt);
  }

  boolean validateCredentials(User user, String password, String phone, String email) {
    return user.getPassword().equals(password)
      && (user.getPhones().stream().map(PhoneData::getPhone).toList().contains(phone)
      || user.getEmails().stream().map(EmailData::getEmail).toList().contains(email));
  }
}
