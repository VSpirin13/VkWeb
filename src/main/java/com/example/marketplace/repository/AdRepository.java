package com.example.marketplace.repository;

import com.example.marketplace.model.Ad;
import com.example.marketplace.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AdRepository extends JpaRepository<Ad, Long> {

    @Query("SELECT a FROM Ad a JOIN FETCH a.author")
    List<Ad> findAllWithAuthor();
    Page<Ad> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<Ad> findByPriceGreaterThanEqual(BigDecimal minPrice, Pageable pageable);
    Page<Ad> findByPriceLessThanEqual(BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT a FROM Ad a JOIN FETCH a.author WHERE a.id = :id")
    Optional<Ad> findByIdWithAuthor(Long id);


    @Query(value = "SELECT a FROM Ad a JOIN FETCH a.author WHERE a.price BETWEEN :minPrice AND :maxPrice",
            countQuery = "SELECT count(a) FROM Ad a WHERE a.price BETWEEN :minPrice AND :maxPrice")
    Page<Ad> findByPriceBetweenWithAuthor(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query(value = "SELECT a FROM Ad a JOIN FETCH a.author WHERE a.price >= :minPrice",
            countQuery = "SELECT count(a) FROM Ad a WHERE a.price >= :minPrice")
    Page<Ad> findByPriceGreaterThanEqualWithAuthor(BigDecimal minPrice, Pageable pageable);

    @Query(value = "SELECT a FROM Ad a JOIN FETCH a.author WHERE a.price <= :maxPrice",
            countQuery = "SELECT count(a) FROM Ad a WHERE a.price <= :maxPrice")
    Page<Ad> findByPriceLessThanEqualWithAuthor(BigDecimal maxPrice, Pageable pageable);

    @Query(value = "SELECT a FROM Ad a JOIN FETCH a.author",
            countQuery = "SELECT count(a) FROM Ad a")
    Page<Ad> findAllWithAuthor(Pageable pageable);

    @Query(value = "SELECT a FROM Ad a JOIN FETCH a.author WHERE a.author = :author",
            countQuery = "SELECT COUNT(a) FROM Ad a WHERE a.author = :author")
    Page<Ad> findByAuthor(User author, Pageable pageable);

    Optional<Ad> findByIdAndAuthor(Long id, User author);

    Page<Ad> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);

    Page<Ad> findAll(Specification<Ad> spec, Pageable pageable);
}
