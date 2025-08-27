package io.mhetko.lor.controller;

import io.mhetko.lor.dto.RegisterUserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.security.Principal;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        model.addAttribute("isLoggedIn", principal != null);
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, Principal principal) {
        model.addAttribute("registerUserDTO", new RegisterUserDTO());
        model.addAttribute("isLoggedIn", principal != null);
        return "register";
    }

    @GetMapping("/register-success")
    public String registerSuccess(Model model, Principal principal) {
        model.addAttribute("isLoggedIn", principal != null);
        return "register-success";
    }

    @GetMapping("/proposed")
    public String proposed(Model model, Principal principal) {
        model.addAttribute("isLoggedIn", principal != null);
        return "proposed";
    }

    @GetMapping("/profile")
    public String profilePage(Model model, Principal principal) {
        model.addAttribute("isLoggedIn", principal != null);
        return "profile";
    }

    @GetMapping("/watched")
    public String watchedTopicsPage(Model model, Principal principal) {
        model.addAttribute("isLoggedIn", principal != null);
        return "watched";
    }

    @GetMapping("/about")
    public String about(Model model, Principal principal) {
        model.addAttribute("isLoggedIn", principal != null);
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model, Principal principal) {
        model.addAttribute("isLoggedIn", principal != null);
        return "contact";
    }
}