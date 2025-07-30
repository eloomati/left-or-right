package io.mhetko.lor.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AuthTestController {

    @GetMapping("/api/test")
    public String test(Authentication authentication) {
        if (authentication == null) {
            log.info("Controller: Brak Authentication w kontekście");
            return "Nie jesteś zalogowany.";
        }
        log.info("Controller: Authentication = {}", authentication);
        return "Jesteś zalogowany jako: " + authentication.getName();
    }
}