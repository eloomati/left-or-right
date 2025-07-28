package io.mhetko.lor.service;

import io.mhetko.lor.dto.AppUserDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.exception.EmailAlreadyExistsException;
import io.mhetko.lor.exception.UserAlreadyExistsException;
import io.mhetko.lor.mapper.AppUserMapper;
import io.mhetko.lor.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;

    public AppUser registerUser(AppUserDTO appUserDTO) {
        validateUserRegistration(appUserDTO);

        AppUser appUser = appUserMapper.mapToAppUserEntity(appUserDTO);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRole("USER");
        appUser.setCreatedAt(LocalDateTime.now());
        appUser.setUpdatedAt(LocalDateTime.now());
        appUser.setIsActive(false);

        return appUserRepository.save(appUser);
    }

    private void validateUserRegistration(AppUserDTO appUserDTO) {
        if (appUserRepository.existsByUsername(appUserDTO.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (appUserRepository.existsByEmail(appUserDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
    }
}
