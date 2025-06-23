package com.user.wallet.user.wallet.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
@Schema(description = "Запрос на регистрацию")
public class SignUpRequest {

  @Schema(description = "Имя пользователя", example = "Jon")
  @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
  @NotBlank(message = "Имя пользователя не может быть пустым")
  private String username;

  @Schema(description = "Адрес электронной почты", example = "jondoe@gmail.com")
  @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
  @NotBlank(message = "Адрес электронной почты не может быть пустым")
  @Email(message = "Email адрес должен быть в формате user@example.com")
  private String email;

  @Schema(description = "Дата рождения", example = "2000-01-01")
  @NotNull(message = "Дата рождения не может быть пустой")
  @Past(message = "Дата рождения должна быть в прошлом")
  private LocalDate dateOfBirth;

  @Schema(description = "Пароль", example = "my_1secret1_password")
  @Size(min = 8, max = 500, message = "Длина пароля должна быть от 8 до 500 символов")
  private String password;
}