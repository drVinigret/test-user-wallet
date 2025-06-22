package com.user.wallet.user.wallet.service;

import com.user.wallet.user.wallet.entity.EmailData;
import com.user.wallet.user.wallet.entity.PhoneData;
import com.user.wallet.user.wallet.entity.User;
import com.user.wallet.user.wallet.repository.EmailDataRepository;
import com.user.wallet.user.wallet.repository.PhoneDataRepository;
import com.user.wallet.user.wallet.repository.UserRepository;
import com.user.wallet.user.wallet.repository.specification.UserSpecifications;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final EmailDataRepository emailDataRepository;
  private final PhoneDataRepository phoneDataRepository;

  public User save(User user) {
    return userRepository.save(user);
  }

  public User create(User user) {
    if (userRepository.existsByName(user.getName())) {
      throw new RuntimeException("Пользователь с таким именем уже существует");
    }

    List<String> emails = user.getEmails().stream().map(email -> email.getEmail()).toList();
    if (emailDataRepository.existsByEmailIn(emails)) {
      throw new RuntimeException("Пользователь с таким email уже существует");
    }

    return save(user);
  }

  public void addUserEmails(User user, List<String> emails) {
    List<String> notExistStringEmails =
      emails.stream()
        .filter(email -> !user.getEmails().stream().map(EmailData::getEmail).toList().contains(email))
        .toList();
    List<EmailData> notExistEmails = notExistStringEmails.stream().map(mail -> new EmailData(user, mail)).toList();
    emailDataRepository.saveAll(notExistEmails);
  }

  public void addUserPhones(User user, List<String> phones) {
    List<String> notExistStringPhones =
      phones.stream()
        .filter(phone -> !user.getPhones().stream().map(PhoneData::getPhone).toList().contains(phone))
        .toList();
    List<PhoneData> notExistEmails = notExistStringPhones.stream().map(phone -> new PhoneData(user, phone)).toList();
    phoneDataRepository.saveAll(notExistEmails);
  }

  public User getByUsername(String username) {
    return Optional.ofNullable(userRepository.findByName(username))
      .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
  }

  public User getByUserId(Long userId) {
    return userRepository.findById(userId)
      .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
  }

  public Page<User> searchUsers(
    String name,
    LocalDate dateOfBirth,
    String email,
    String phone,
    Pageable pageable) {

    Specification<User> spec = Specification.allOf(
      UserSpecifications.hasNameLike(name),
      UserSpecifications.hasDateOfBirthAfter(dateOfBirth),
      UserSpecifications.hasEmail(email),
      UserSpecifications.hasPhone(phone)
    );

    return userRepository.findAll(spec, pageable);
  }
}