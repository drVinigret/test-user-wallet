package com.user.wallet.user.wallet.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.user.wallet.user.wallet.filter.JwtAuthenticationFilter;
import com.user.wallet.user.wallet.service.DefaultUserDetailsService;
import com.user.wallet.user.wallet.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final DefaultUserDetailsService userService;

  //permitAll - Эндпоинт доступен всем пользователям, и авторизованным и нет
  //authenticated - Только авторизованные пользователи
  //hasRole - Пользователь должен иметь конкретную роль, и, соответственно быть авторизованным
  //hasAnyRole - Должен иметь одну из перечисленных ролей (не представлено в коде)

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
      // Своего рода отключение CORS (разрешение запросов со всех доменов)
      .cors(cors -> cors.configurationSource(request -> {
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
      }))
      // Настройка доступа к конечным точкам
      .authorizeHttpRequests(request -> request
        // Можно указать конкретный путь, * - 1 уровень вложенности, ** - любое количество уровней вложенности
        .requestMatchers("/auth/**").permitAll()
        .requestMatchers("/swagger-ui/**", "/swagger-resources/*", "/v3/api-docs/**").permitAll()
        .requestMatchers("/endpoint", "/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated())
      .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
      .authenticationProvider(authenticationProvider())
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
    throws Exception {
    return config.getAuthenticationManager();
  }
}