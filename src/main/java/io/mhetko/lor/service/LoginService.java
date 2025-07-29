package io.mhetko.lor.service;

import io.mhetko.lor.dto.LoginUserDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.exception.InvalidCredentialsException;
import io.mhetko.lor.repository.AppUserRepository;
import io.mhetko.lor.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AppUserRepository appUserRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public String login(LoginUserDTO loginUserDTO) {
        AppUser user = appUserRepository.findByUsername(loginUserDTO.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(loginUserDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getUsername());
    }
}
