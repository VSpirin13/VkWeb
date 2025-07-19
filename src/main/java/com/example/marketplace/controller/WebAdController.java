package com.example.marketplace.controller;

import com.example.marketplace.model.Ad;
import com.example.marketplace.service.AdService;
import com.example.marketplace.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.marketplace.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Controller
@RequestMapping({"/", "/ads"})
public class WebAdController {

     private final AdService adService;
    private final UserService userService;
     @Autowired
     public WebAdController(AdService adService, UserService userService) {
         this.adService = adService;
         this.userService = userService;
     }

    private final String UPLOAD_DIR = "./uploads/";

    @GetMapping
    public String listAds(
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortDirection", required = false) String sortDirection,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {


        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, sortBy);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Ad> adsPage = adService.getAds(sortBy, sortDirection, minPrice, maxPrice, searchTerm, pageable);

        model.addAttribute("ads", adsPage.getContent());
        model.addAttribute("currentPage", adsPage.getNumber());
        model.addAttribute("totalPages", adsPage.getTotalPages());
        model.addAttribute("totalItems", adsPage.getTotalElements());
        model.addAttribute("sortBy", Optional.ofNullable(sortBy).orElse("createdAt"));
        model.addAttribute("sortDirection", Optional.ofNullable(sortDirection).orElse("desc"));
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("searchTerm", searchTerm);

        return "ad_list";
    }

    @GetMapping("/new")
    public String showCreateAdForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {

            return "redirect:/ads/login";
        }
        model.addAttribute("ad", new Ad());
        return "create_ad";
    }

    @PostMapping("/new")
    public String createAd(@ModelAttribute("ad") @Valid Ad ad,
                           BindingResult bindingResult,
                           Model model,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {

            return "create_ad";
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
                model.addAttribute("errorMessage", "Вы должны быть авторизованы для создания объявления.");
                return "create_ad";
            }

            User currentUser = (User) authentication.getPrincipal();

            String imageUrl = null;

            if (!imageFile.isEmpty()) {
                try {
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    String originalFilename = imageFile.getOriginalFilename();
                    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

                    Path filePath = uploadPath.resolve(uniqueFilename);

                    Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    imageUrl = "/uploads/" + uniqueFilename;

                } catch (IOException e) {
                    model.addAttribute("errorMessage", "Ошибка при загрузке изображения: " + e.getMessage());
                    return "create_ad";
                }
            } else {
                model.addAttribute("errorMessage", "Пожалуйста, выберите изображение для объявления.");
                return "create_ad";
            }

            Ad newAd = new Ad();
            newAd.setTitle(ad.getTitle());
            newAd.setDescription(ad.getDescription());
            newAd.setImageUrl(imageUrl);
            newAd.setPrice(ad.getPrice());
            newAd.setAuthor(currentUser);
            newAd.setCreatedAt(LocalDateTime.now());
            newAd.setCity(ad.getCity());
            newAd.setPhoneNumber(ad.getPhoneNumber());


            adService.createAd(newAd, currentUser);

            redirectAttributes.addFlashAttribute("successMessage", "Объявление успешно создано!");

            return "redirect:/ads";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при создании объявления: " + e.getMessage());
            return "create_ad";
        }
    }

}
