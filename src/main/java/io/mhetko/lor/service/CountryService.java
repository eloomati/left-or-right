package io.mhetko.lor.service;

import io.mhetko.lor.entity.Country;
import io.mhetko.lor.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public List<Country> getCountriesByContinentId(Long continentId) {
        return countryRepository.findByContinentId(continentId);
    }
}