package io.mhetko.lor.service;

import io.mhetko.lor.dto.*;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.enums.Role;
import io.mhetko.lor.exception.*;
import io.mhetko.lor.mapper.AppUserMapper;
import io.mhetko.lor.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {

    private static final Role DEFAULT_ROLE = Role.USER;

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final TagService tagService;
    private final CategoryService categoryService;
    // private final ReportService reportService; // jeśli masz obsługę zgłoszeń

    public AppUser registerUser(RegisterUserDTO registerUserDTO) {
        log.info("Starting user registration for username: {}", registerUserDTO.getUsername());
        validateUserRegistration(registerUserDTO);

        AppUser appUser = prepareAppUser(registerUserDTO);
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

    // --- Konfiguracja profilu dostępna dla wszystkich użytkowników ---
    public AppUser updateProfile(Long userId, String avatarUrl, String country, String continent) {
        AppUser user = getUserOrThrow(userId);
        user.setAvatarUrl(avatarUrl);
        user.setCountry(country);
        user.setContinent(continent);
        user.setUpdatedAt(LocalDateTime.now());
        return appUserRepository.save(user);
    }

    // --- Operacje dostępne tylko dla ADMIN/SUPER_ADMIN ---
    public TagDTO createTag(Long userId, TagDTO tagDTO) {
        AppUser user = getUserOrThrow(userId);
        checkAdminPrivileges(user);
        return tagService.createTag(tagDTO);
    }

    public CategoryDTO createCategory(Long userId, CategoryDTO categoryDTO) {
        AppUser user = getUserOrThrow(userId);
        checkAdminPrivileges(user);
        return categoryService.createCategory(categoryDTO);
    }

    // public List<ReportDTO> getAllReports(Long userId) {
    //     AppUser user = getUserOrThrow(userId);
    //     checkAdminPrivileges(user);
    //     return reportService.getAllReports();
    // }

    // --- Pomocnicze metody ---
    private AppUser getUserOrThrow(Long userId) {
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId));
    }

    private void checkAdminPrivileges(AppUser user) {
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Brak uprawnień do tej operacji");
        }
    }

    public AppUser getUserById(Long userId) {
        return getUserOrThrow(userId);
    }

    private AppUser prepareAppUser(RegisterUserDTO registerUserDTO) {
        AppUser appUser = appUserMapper.mapToAppUserEntity(registerUserDTO);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRole(DEFAULT_ROLE);
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

    private void validateUserRegistration(RegisterUserDTO registerUserDTO) {
        if (appUserRepository.existsByUsername(registerUserDTO.getUsername())) {
            log.warn("Registration failed: username {} already exists", registerUserDTO.getUsername());
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (appUserRepository.existsByEmail(registerUserDTO.getEmail())) {
            log.warn("Registration failed: email {} already exists", registerUserDTO.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }
    }

    public AppUser uploadAvatar(Long userId, MultipartFile file) {
        AppUser user = getUserOrThrow(userId);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get("avatars", filename);
        try {
            Files.createDirectories(path.getParent());
            file.transferTo(path);
            user.setAvatarUrl("/avatars/" + filename);
            return appUserRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException("Błąd zapisu pliku", e);
        }
    }

    public void changePassword(Long userId, ChangePasswordDTO dto) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId));
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Obecne hasło jest nieprawidłowe.");
        }
        if (dto.getCurrentPassword().equals(dto.getNewPassword())) {
            throw new IllegalArgumentException("Nowe hasło nie może być takie samo jak obecne.");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        appUserRepository.save(user);
    }
}