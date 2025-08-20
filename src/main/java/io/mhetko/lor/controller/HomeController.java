package io.mhetko.lor.controller;

import io.mhetko.lor.dto.RegisterUserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index"; // szuka templates/index.html
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerUserDTO", new RegisterUserDTO());
        return "register";
    }

    @GetMapping("/register-success")
    public String registerSuccess() {
        return "register-success";
    }

    @GetMapping("/proposed")
    public String proposed() {
        return "proposed";
    }
}