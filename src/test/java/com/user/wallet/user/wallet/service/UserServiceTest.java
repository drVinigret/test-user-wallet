package com.user.wallet.user.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.user.wallet.user.wallet.entity.EmailData;
import com.user.wallet.user.wallet.entity.PhoneData;
import com.user.wallet.user.wallet.entity.User;
import com.user.wallet.user.wallet.repository.EmailDataRepository;
import com.user.wallet.user.wallet.repository.PhoneDataRepository;
import com.user.wallet.user.wallet.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private EmailDataRepository emailDataRepository;

  @Mock
  private PhoneDataRepository phoneDataRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;
  private final String testUsername = "testUser";
  private final Long testUserId = 1L;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(testUserId);
    testUser.setName(testUsername);
    testUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
  }

  @Test
  void save_ShouldReturnSavedUser() {
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    User savedUser = userService.save(testUser);

    assertNotNull(savedUser);
    assertEquals(testUsername, savedUser.getName());
    verify(userRepository, times(1)).save(testUser);
  }

  @Test
  void create_WithUniqueNameAndEmail_ShouldCreateUser() {
    when(userRepository.existsByName(testUsername)).thenReturn(false);
    when(emailDataRepository.existsByEmailIn(anyList())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    User createdUser = userService.create(testUser);

    assertNotNull(createdUser);
    verify(userRepository, times(1)).existsByName(testUsername);
    verify(emailDataRepository, times(1)).existsByEmailIn(anyList());
    verify(userRepository, times(1)).save(testUser);
  }

  @Test
  void create_WithExistingName_ShouldThrowException() {
    when(userRepository.existsByName(testUsername)).thenReturn(true);

    assertThrows(RuntimeException.class, () -> userService.create(testUser));
    verify(userRepository, times(1)).existsByName(testUsername);
    verify(emailDataRepository, never()).existsByEmailIn(anyList());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void create_WithExistingEmail_ShouldThrowException() {
    when(userRepository.existsByName(testUsername)).thenReturn(false);
    when(emailDataRepository.existsByEmailIn(anyList())).thenReturn(true);

    assertThrows(RuntimeException.class, () -> userService.create(testUser));
    verify(userRepository, times(1)).existsByName(testUsername);
    verify(emailDataRepository, times(1)).existsByEmailIn(anyList());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void addUserEmails_WithNewEmails_ShouldAddEmails() {
    List<String> newEmails = List.of("new1@example.com", "new2@example.com");
    EmailData emailData1 = new EmailData(testUser, "new1@example.com");
    EmailData emailData2 = new EmailData(testUser, "new2@example.com");

    when(emailDataRepository.saveAll(anyList())).thenReturn(List.of(emailData1, emailData2));

    userService.addUserEmails(testUser, newEmails);

    verify(emailDataRepository, times(1)).saveAll(anyList());
  }

  @Test
  void addUserPhones_WithNewPhones_ShouldAddPhones() {
    List<String> newPhones = List.of("+123456789", "+987654321");
    PhoneData phoneData1 = new PhoneData(testUser, "+123456789");
    PhoneData phoneData2 = new PhoneData(testUser, "+987654321");

    when(phoneDataRepository.saveAll(anyList())).thenReturn(List.of(phoneData1, phoneData2));

    userService.addUserPhones(testUser, newPhones);

    verify(phoneDataRepository, times(1)).saveAll(anyList());
  }

  @Test
  void getByUsername_WithExistingUser_ShouldReturnUser() {
    when(userRepository.findByName(testUsername)).thenReturn(testUser);

    User foundUser = userService.getByUsername(testUsername);

    assertNotNull(foundUser);
    assertEquals(testUsername, foundUser.getName());
    verify(userRepository, times(1)).findByName(testUsername);
  }

  @Test
  void getByUsername_WithNonExistingUser_ShouldThrowException() {
    when(userRepository.findByName(testUsername)).thenReturn(null);

    assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername(testUsername));
    verify(userRepository, times(1)).findByName(testUsername);
  }

  @Test
  void getByUserId_WithExistingUser_ShouldReturnUser() {
    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

    User foundUser = userService.getByUserId(testUserId);

    assertNotNull(foundUser);
    assertEquals(testUserId, foundUser.getId());
    verify(userRepository, times(1)).findById(testUserId);
  }

  @Test
  void getByUserId_WithNonExistingUser_ShouldThrowException() {
    when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> userService.getByUserId(testUserId));
    verify(userRepository, times(1)).findById(testUserId);
  }

  @Test
  void searchUsers_ShouldReturnPageOfUsers() {
    Page mockPage = mock(Page.class);
    when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

    Page<User> result = userService.searchUsers(
      "test",
      LocalDate.of(1990, 1, 1),
      "test@example.com",
      "+123456789",
      Pageable.unpaged()
    );

    assertNotNull(result);
    verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
  }
}