package io.mhetko.lor.service;

import io.mhetko.lor.dto.LoginUserDTO;
import io.mhetko.lor.dto.UserCredentialsDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.exception.InvalidCredentialsException;
import io.mhetko.lor.mapper.UserCredentialsMapper;
import io.mhetko.lor.repository.AppUserRepository;
import io.mhetko.lor.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final AppUserRepository appUserRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserCredentialsMapper userCredentialsMapper;

    public String login(LoginUserDTO loginUserDTO) {
        AppUser user = findUserByUsername(loginUserDTO.getUsername());
        validateUserCredentials(user, loginUserDTO.getPassword());
        updateLastLoginAt(user);
        String token = generateJwtToken(user.getUsername());
        log.info("User '{}' successfully logged in.", user.getUsername());
        return token;
    }

    private AppUser findUserByUsername(String username) {
        log.debug("Searching for user '{}'", username);
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private void validateUserCredentials(AppUser user, String rawPassword) {
        UserCredentialsDTO dto = userCredentialsMapper.toDto(user);
        if (Boolean.FALSE.equals(dto.getIsActive())) {
            log.warn("Login attempt for inactive user '{}'", dto.getUsername());
            throw new InvalidCredentialsException("User is not active");
        }
        if (!passwordEncoder.matches(rawPassword, dto.getPassword())) {
            log.warn("Invalid password attempt for user '{}'", dto.getUsername());
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    private void updateLastLoginAt(AppUser user) {
        user.setLastLoginAt(java.time.LocalDateTime.now());
        appUserRepository.save(user);
        log.debug("Updated lastLoginAt for user '{}'", user.getUsername());
    }

    private String generateJwtToken(String username) {
        return jwtUtil.generateToken(username);
    }
}