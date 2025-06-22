package com.user.wallet.user.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.user.wallet.user.wallet.entity.User;
import com.user.wallet.user.wallet.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class DefaultUserDetailsServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private DefaultUserDetailsService userDetailsService;

  private static final Long TEST_USER_ID = 1L;
  private static final String TEST_USERNAME = "testUser";
  private static final String TEST_PASSWORD = "password";

  @Test
  void getByUserId_Success() {
    User testUser = new User();
    testUser.setId(TEST_USER_ID);
    testUser.setName(TEST_USERNAME);
    testUser.setPassword(TEST_PASSWORD);
    when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
    UserDetails userDetails = userDetailsService.getByUserId(TEST_USER_ID);

    assertNotNull(userDetails);
    assertEquals(TEST_USERNAME, userDetails.getUsername());
    assertEquals(TEST_PASSWORD, userDetails.getPassword());
    assertTrue(userDetails.getAuthorities().isEmpty());
    verify(userRepository, times(1)).findById(TEST_USER_ID);
  }

  @Test
  void getByUserId_UserNotFound() {
    when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class,
      () -> userDetailsService.getByUserId(TEST_USER_ID));
    verify(userRepository, times(1)).findById(TEST_USER_ID);
  }

  @Test
  void loadUserByUsername_Success() {
    User testUser = new User();
    testUser.setName(TEST_USERNAME);
    testUser.setPassword(TEST_PASSWORD);
    when(userRepository.findByName(TEST_USERNAME)).thenReturn(testUser);
    UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_USERNAME);

    assertNotNull(userDetails);
    assertEquals(TEST_USERNAME, userDetails.getUsername());
    assertEquals(TEST_PASSWORD, userDetails.getPassword());
    assertTrue(userDetails.getAuthorities().isEmpty());
    verify(userRepository, times(1)).findByName(TEST_USERNAME);
  }

  @Test
  void loadUserByUsername_UserNotFound() {
    when(userRepository.findByName(TEST_USERNAME)).thenReturn(null);

    assertThrows(UsernameNotFoundException.class,
      () -> userDetailsService.loadUserByUsername(TEST_USERNAME));
    verify(userRepository, times(1)).findByName(TEST_USERNAME);
  }

  @Test
  void loadUserByUsername_NullUsername() {
    assertThrows(UsernameNotFoundException.class,
      () -> userDetailsService.loadUserByUsername(null));
    verify(userRepository, never()).findByName(any());
  }
}