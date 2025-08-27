// src/main/java/io/mhetko/lor/controller/CountryController.java
package io.mhetko.lor.controller;

import io.mhetko.lor.entity.Country;
import io.mhetko.lor.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;

    @GetMapping
    public List<Country> getAllCountries() {
        return countryService.getAllCountries();
    }

    @GetMapping("/by-continent/{continentId}")
    public List<Country> getCountriesByContinent(@PathVariable Long continentId) {
        return countryService.getCountriesByContinentId(continentId);
    }
}