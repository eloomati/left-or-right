// src/main/java/io/mhetko/lor/controller/ContinentController.java
package io.mhetko.lor.controller;

import io.mhetko.lor.entity.Continent;
import io.mhetko.lor.service.ContinentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/continents")
@RequiredArgsConstructor
public class ContinentController {
    private final ContinentService continentService;

    @GetMapping
    public List<Continent> getAllContinents() {
        return continentService.getAllContinents();
    }
}