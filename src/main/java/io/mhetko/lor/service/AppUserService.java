package io.mhetko.lor.service;

import io.mhetko.lor.dto.AppUserDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.exception.EmailAlreadyExistsException;
import io.mhetko.lor.exception.UserAlreadyExistsException;
import io.mhetko.lor.mapper.AppUserMapper;
import io.mhetko.lor.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {

    private static final String DEAULT_ROLE = "USER";

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public AppUser registerUser(AppUserDTO appUserDTO) {
        log.info("Starting user registration for username: {}", appUserDTO.getUsername());
        validateUserRegistration(appUserDTO);

        AppUser appUser = prepareAppUser(appUserDTO);
        String activationToken = generateActivationToken(appUser);
        AppUser savedUser = appUserRepository.save(appUser);

        sendActivationEmail(savedUser, activationToken);

        log.info("User {} registered successfully", savedUser.getUsername());
        return savedUser;
    }

    public void activateUser(String token) {
        log.info("Attempting to activate user with token: {}", token);
        AppUser user = appUserRepository.findByActivationToken(token)
                .orElseThrow(() -> {
                    log.warn("Activation failed: invalid token {}", token);
                    return new RuntimeException("Invalid activation token");
                });
        user.setIsActive(true);
        user.setActivationToken(null);
        appUserRepository.save(user);
        log.info("User {} activated successfully", user.getUsername());
    }

    private AppUser prepareAppUser(AppUserDTO appUserDTO) {
        AppUser appUser = appUserMapper.mapToAppUserEntity(appUserDTO);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRole(DEAULT_ROLE);
        appUser.setCreatedAt(LocalDateTime.now());
        appUser.setUpdatedAt(LocalDateTime.now());
        appUser.setIsActive(false);
        return appUser;
    }

    private String generateActivationToken(AppUser appUser) {
        String activationToken = UUID.randomUUID().toString();
        appUser.setActivationToken(activationToken);
        appUser.setIsActive(false);
        return activationToken;
    }

    private void sendActivationEmail(AppUser user, String activationToken) {
        try {
            mailService.sendConfirmationEmail(user.getEmail(), activationToken);
            log.info("Activation email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send activation email to {}: {}", user.getEmail(), e.getMessage());
            throw e;
        }
    }

    private void validateUserRegistration(AppUserDTO appUserDTO) {
        if (appUserRepository.existsByUsername(appUserDTO.getUsername())) {
            log.warn("Registration failed: username {} already exists", appUserDTO.getUsername());
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (appUserRepository.existsByEmail(appUserDTO.getEmail())) {
            log.warn("Registration failed: email {} already exists", appUserDTO.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }
    }
}

