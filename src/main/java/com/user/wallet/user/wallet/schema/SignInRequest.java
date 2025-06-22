package com.user.wallet.user.wallet.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификацию")
public class SignInRequest {

  @Schema(description = "Имя пользователя", example = "Jon")
  @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
  @NotBlank(message = "Имя пользователя не может быть пустыми")
  private String username;

  @Schema(description = "Адрес электронной почты", example = "jondoe@gmail.com")
  @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
  @Email(message = "Email адрес должен быть в формате user@example.com")
  private String email;

  @Schema(description = "Номер телефона", example = "+33333333333")
  @Pattern(regexp = "^7\\d{10}$", message = "Номер телефона должен начинаться с 7 и содержать 10 цифр")
  private String phoneNumber;

  @Schema(description = "Пароль", example = "my_1secret1_password")
  @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
  @NotBlank(message = "Пароль не может быть пустыми")
  private String password;
}