package com.user.wallet.user.wallet.repository;

import com.user.wallet.user.wallet.entity.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "users")
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  @Cacheable(key = "#name", unless = "#result == false")
  boolean existsByName(String name);

  @Cacheable(key = "#name", unless = "#result == null")
  User findByName(String name);
}