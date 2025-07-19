package com.example.marketplace.service.impl;

import com.example.marketplace.model.Ad;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.AdRepository;
import com.example.marketplace.service.AdService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;

    public AdServiceImpl(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    @Override
    public List<Ad> getAllAds() {
        return adRepository.findAllWithAuthor();
    }

    @Override
    public Page<Ad> getAds(String sortBy, String sortDirection, BigDecimal minPrice, BigDecimal maxPrice, String searchTerm, Pageable pageable) {
        Specification<Ad> spec = (root, query, criteriaBuilder) -> {

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("author", JoinType.INNER);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String likePattern = "%" + searchTerm.toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern);
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern);
                predicates.add(criteriaBuilder.or(titlePredicate, descriptionPredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort currentSort = pageable.getSort();
        Sort newSort = currentSort;
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
            newSort = Sort.by(direction, sortBy);
            pageable = org.springframework.data.domain.PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
        }

        return adRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public Ad createAd(Ad ad, User user) {
        ad.setAuthor(user);

        return adRepository.save(ad);
    }

    @Override
    public Ad getAdById(Long id) {
        return adRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new EntityNotFoundException("Объявление с ID " + id + " не найдено."));
    }

    @Override
    public Page<Ad> getUserAds(User user, Pageable pageable) {

        return adRepository.findByAuthor(user, pageable);
    }

    @Override
    @Transactional
    public Ad updateAd(Long id, Ad updatedAd, User currentUser) {
        Ad existingAd = adRepository.findByIdAndAuthor(id, currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Объявление не найдено или вы не являетесь его автором."));

        existingAd.setTitle(updatedAd.getTitle());
        existingAd.setDescription(updatedAd.getDescription());
        existingAd.setPrice(updatedAd.getPrice());
        existingAd.setCity(updatedAd.getCity());
        existingAd.setPhoneNumber(updatedAd.getPhoneNumber());
        existingAd.setImageUrl(updatedAd.getImageUrl());

        return adRepository.save(existingAd);
    }

    @Override
    @Transactional
    public void deleteAd(Long id, User currentUser) {
        Ad existingAd = adRepository.findByIdAndAuthor(id, currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Объявление не найдено или вы не являетесь его автором."));
        adRepository.delete(existingAd);
    }

    @Override
    public Page<Ad> searchAds(String searchTerm, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Ad> getAds(Pageable pageable, BigDecimal minPrice, BigDecimal maxPrice) {
        return null;
    }
}
