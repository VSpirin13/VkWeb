package com.example.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationRequest {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 20, message = "Имя пользователя должно содержать от 3 до 20 символов")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 100, message = "Пароль должен состоять как минимум из 6 символов")
    private String password;

    public RegistrationRequest() {}

    public RegistrationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
