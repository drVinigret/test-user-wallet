package com.user.wallet.user.wallet.repository;

import com.user.wallet.user.wallet.entity.EmailData;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {
    boolean existsByEmailIn(List<String> emails);
}