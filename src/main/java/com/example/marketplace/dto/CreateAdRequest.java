package com.example.marketplace.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateAdRequest {
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 5, max = 100, message = "Заголовок должен содержать от 5 до 100 символов")
    private String title;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 10, max = 1000, message = "Описание должно содержать от 10 до 1000 символов")
    private String description;

    @NotBlank(message = "URL-адрес изображения не может быть пустым")
    private String imageUrl;

    @NotNull(message = "Цена не может быть нулевой")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    private BigDecimal price;

    @NotBlank(message = "Город не может быть пустым")
    private String city;

    @NotBlank(message = "Номер телефона не может быть пустым")
    @Size(min = 7, max = 20, message = "Номер телефона должен содержать от 7 до 20 символов")
    private String phoneNumber;

    public CreateAdRequest() {}

    public CreateAdRequest(String title, String description, String imageUrl, BigDecimal price, String city, String phoneNumber) { // Добавили phoneNumber
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.city = city;
        this.phoneNumber = phoneNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
