package com.example.marketplace.dto;

import com.example.marketplace.model.Ad;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdResponseDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String city;
    private String phoneNumber;
    private String imageUrl;
    private LocalDateTime createdAt;
    private String authorLogin;


    public AdResponseDTO(Ad ad) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.description = ad.getDescription();
        this.price = ad.getPrice();
        this.city = ad.getCity();
        this.phoneNumber = ad.getPhoneNumber();
        this.imageUrl = ad.getImageUrl();
        this.createdAt = ad.getCreatedAt();

        if (ad.getAuthor() != null) {
            this.authorLogin = ad.getAuthor().getLogin();
        } else {
            this.authorLogin = "Неизвестный";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAuthorLogin() {
        return authorLogin;
    }

    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
    }
}
