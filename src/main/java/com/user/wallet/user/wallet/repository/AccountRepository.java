package com.user.wallet.user.wallet.repository;

import com.user.wallet.user.wallet.entity.Account;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "accounts")
public interface AccountRepository extends JpaRepository<Account, Long> {

  @Cacheable(key = "#id")
  @Override
  Optional<Account> findById(Long id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM Account a WHERE a.user.id = :userId")
  Optional<Account> findByUserIdWithLock(@Param("userId") Long userId);

  @Query("SELECT a FROM Account a WHERE a.user.id = :userId")
  Optional<Account> findByUserId(@Param("userId") Long userId);
}