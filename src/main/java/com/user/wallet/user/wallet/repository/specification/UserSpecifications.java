package com.user.wallet.user.wallet.repository.specification;

import com.user.wallet.user.wallet.entity.EmailData;
import com.user.wallet.user.wallet.entity.PhoneData;
import com.user.wallet.user.wallet.entity.User;
import jakarta.persistence.criteria.Join;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

  public static Specification<User> hasNameLike(String name) {
    return (root, query, cb) -> {
      if (name == null) {
        return cb.conjunction();
      }
      return cb.like(root.get("name"), "%" + name + "%");
    };
  }

  public static Specification<User> hasDateOfBirthAfter(LocalDate date) {
    return (root, query, cb) -> {
      if (date == null) {
        return cb.conjunction();
      }
      return cb.greaterThan(root.get("dateOfBirth"), date);
    };
  }

  public static Specification<User> hasEmail(String email) {
    return (root, query, cb) -> {
      if (email == null) {
        return cb.conjunction();
      }
      Join<User, EmailData> emailJoin = root.join("emails");
      return cb.equal(emailJoin.get("email"), email);
    };
  }

  public static Specification<User> hasPhone(String phone) {
    return (root, query, cb) -> {
      if (phone == null) {
        return cb.conjunction();
      }
      Join<User, PhoneData> phoneJoin = root.join("phones");
      return cb.equal(phoneJoin.get("phone"), phone);
    };
  }
}