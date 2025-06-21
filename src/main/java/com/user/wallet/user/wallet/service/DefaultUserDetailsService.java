package com.user.wallet.user.wallet.service;

import com.user.wallet.user.wallet.entity.User;
import com.user.wallet.user.wallet.repository.UserRepository;
import java.util.HashSet;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return getCurrentUser(username);
  }

  public User getByUsername(String username) {
    return Optional.of(userRepository.findByName(username))
      .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
  }

  public org.springframework.security.core.userdetails.User getCurrentUser(String name) {
    var user = getByUsername(name);
    return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), new HashSet<>());
  }
}