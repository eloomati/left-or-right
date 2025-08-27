package io.mhetko.lor.service;

import io.mhetko.lor.entity.Continent;
import io.mhetko.lor.repository.ContinentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContinentService {
    private final ContinentRepository continentRepository;

    public List<Continent> getAllContinents() {
        return continentRepository.findAll();
    }
}