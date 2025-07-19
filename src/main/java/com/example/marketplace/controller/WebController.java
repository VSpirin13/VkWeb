package com.example.marketplace.controller;

import com.example.marketplace.model.Ad;
import com.example.marketplace.model.User;
import com.example.marketplace.service.AdService;
import com.example.marketplace.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RequestMapping({"/ads"})
@Controller
public class WebController {
    private final AdService adService;
    private final UserService userService;

    public WebController(UserService userService, AdService adService) {
        this.userService = userService;
        this.adService = adService;
    }


    @GetMapping("/{id}")
    public String getAdDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Ad ad = adService.getAdById(id);
            model.addAttribute("ad", ad);
            return "ad_details";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ads";
        }
    }


    @GetMapping("/profile")
    public String userProfile(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        try {
            User currentUser = userService.findByUsername(currentUserName)
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден."));

            Pageable pageable = PageRequest.of(page, size, sortDirection, sortBy);
            Page<Ad> userAdsPage = adService.getUserAds(currentUser, pageable);

            model.addAttribute("username", currentUserName);
            model.addAttribute("userAds", userAdsPage.getContent());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("currentPage", userAdsPage.getNumber());
            model.addAttribute("totalPages", userAdsPage.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);

            return "profile";
        } catch (EntityNotFoundException e) {

            return "redirect:/ads/login";
        }
    }

    private boolean canUserEditAd(Long adId, User currentUser) {
        if (currentUser == null || currentUser.getId() == null) {

            return false;
        }

        try {
            Ad adToEdit = adService.getAdById(adId);

            if (adToEdit.getAuthor() != null && adToEdit.getAuthor().getId().equals(currentUser.getId())) {
                return true;
            }
            return false;

        } catch (EntityNotFoundException e) {

            return false;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(WebController.class);

    @GetMapping("/edit/{id}")
    public String showEditAdForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        try {
            User currentUser = userService.findByUsername(currentUserName)
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден."));
            if (!canUserEditAd(id, currentUser)) {
                redirectAttributes.addFlashAttribute("errorMessage", "У вас нет прав на редактирование этого объявления.");
                return "redirect:/ads/profile";
            }

            Ad adToEdit = adService.getAdById(id);
            model.addAttribute("ad", adToEdit);
            return "create_ad";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ads/profile";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateAd(@PathVariable Long id,
                           @ModelAttribute("ad") @Valid Ad ad,
                           BindingResult bindingResult,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           RedirectAttributes redirectAttributes,
                           Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        try {
            currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден."));
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ads/profile";
        }


        if (!canUserEditAd(id, currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "У вас нет прав на редактирование этого объявления.");
            return "redirect:/ads/profile";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("ad", ad);
            return "create_ad";
        }


        try {
            Ad existingAd = adService.getAdById(id);
            if (existingAd == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Объявление не найдено.");
                return "redirect:/ads/profile";
            }


            existingAd.setTitle(ad.getTitle());
            existingAd.setDescription(ad.getDescription());
            existingAd.setPrice(ad.getPrice());
            existingAd.setCity(ad.getCity());
            existingAd.setPhoneNumber(ad.getPhoneNumber());


            String imageUrl = existingAd.getImageUrl();

            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    final String UPLOAD_DIR = "src/main/resources/static/images/uploads/";
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    if (existingAd.getImageUrl() != null && !existingAd.getImageUrl().isEmpty()) {
                        String oldFileName = existingAd.getImageUrl().substring(existingAd.getImageUrl().lastIndexOf("/") + 1);
                        Path oldFilePath = uploadPath.resolve(oldFileName);
                        Files.deleteIfExists(oldFilePath);
                    }


                    String originalFilename = imageFile.getOriginalFilename();
                    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
                    Path filePath = uploadPath.resolve(uniqueFilename);


                    Files.copy(imageFile.getInputStream(), filePath);

                    imageUrl = "/images/uploads/" + uniqueFilename;
                    existingAd.setImageUrl(imageUrl);
                } catch (IOException e) {
                    model.addAttribute("errorMessage", "Ошибка при загрузке изображения: " + e.getMessage());
                    // Возвращаемся на форму, сохраняя введенные данные
                    model.addAttribute("ad", ad);
                    return "create_ad";
                }
            }

            adService.updateAd(id, existingAd, currentUser);

            redirectAttributes.addFlashAttribute("successMessage", "Объявление успешно обновлено!");
            return "redirect:/ads/" + id;
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ads/profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при обновлении объявления: " + e.getMessage());
            model.addAttribute("ad", ad);
            return "create_ad";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteAd(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        try {
            User currentUser = userService.findByUsername(currentUserName)
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден."));

            adService.deleteAd(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Объявление успешно удалено!");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении объявления: " + e.getMessage());
        }
        return "redirect:/ads/profile";
    }
}
