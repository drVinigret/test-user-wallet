package com.user.wallet.user.wallet.repository;

import com.user.wallet.user.wallet.entity.EmailData;
import com.user.wallet.user.wallet.entity.PhoneData;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {
}