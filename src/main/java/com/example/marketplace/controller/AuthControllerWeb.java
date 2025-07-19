package com.example.marketplace.controller;

import com.example.marketplace.dto.RegistrationRequest;
import com.example.marketplace.model.User;
import com.example.marketplace.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthControllerWeb {
    private final UserService userService;

    public AuthControllerWeb(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("registrationForm") @Valid RegistrationRequest registrationRequest,
                                      BindingResult bindingResult,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationError", "Пожалуйста, исправьте ошибки ниже.");
            return "register";
        }
        try {
            User newUser = new User();
            newUser.setUsername(registrationRequest.getUsername());
            newUser.setPassword(registrationRequest.getPassword());

            userService.registerUser(newUser);

            return "redirect:/registration_success";
        } catch (Exception e) {
            model.addAttribute("registrationError", "Не удалось зарегистрировать пользователя: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/registration_success")
    public String registrationSuccess() {
        return "registration_success";
    }

}
