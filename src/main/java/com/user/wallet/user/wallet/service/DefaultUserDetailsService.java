package com.user.wallet.user.wallet.service;

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

  public UserDetails getByUserId(Long userId) throws UsernameNotFoundException {
    var user = userRepository.findById(userId)
      .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), new HashSet<>());
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var user = Optional.ofNullable(username)
      .map(userRepository::findByName)
      .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), new HashSet<>());
  }
}