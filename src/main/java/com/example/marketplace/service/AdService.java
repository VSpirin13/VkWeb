package com.example.marketplace.service;

import com.example.marketplace.model.Ad;
import com.example.marketplace.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

public interface AdService {

    List<Ad> getAllAds();

    Page<Ad> getAds(
            String sortBy,
            String sortDirection,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String searchTerm,
            Pageable pageable
    );

    Ad createAd(Ad ad, User user);

    Ad getAdById(Long id);

    Page<Ad> getUserAds(User user, Pageable pageable);

    Ad updateAd(Long id, Ad updatedAd, User currentUser);

    void deleteAd(Long id, User currentUser);

    Page<Ad> searchAds(String searchTerm, Pageable pageable);

    Page<Ad> getAds(Pageable pageable, BigDecimal minPrice, BigDecimal maxPrice);

}
