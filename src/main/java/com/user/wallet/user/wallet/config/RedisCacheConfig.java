package com.user.wallet.user.wallet.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching
public class RedisCacheConfig {

  @Value("${redis.cacheAccountsMin}")
  private long cacheAccountsMin;

  @Value("${redis.cacheUsersMin}")
  private long cacheUsersMin;

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    return RedisCacheManager.builder(connectionFactory)
      .withCacheConfiguration("accounts",
        RedisCacheConfiguration.defaultCacheConfig()
          .entryTtl(Duration.ofMinutes(cacheAccountsMin))
          .disableCachingNullValues()
      )
      .withCacheConfiguration("users",
        RedisCacheConfiguration.defaultCacheConfig()
          .entryTtl(Duration.ofMinutes(cacheUsersMin))
      )
      .transactionAware()
      .enableStatistics()
      .build();
  }
}