package io.mhetko.lor.controller;

import io.mhetko.lor.dto.UpdateProfileDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @PutMapping("/{userId}/profile")
    public ResponseEntity<AppUser> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileDTO updateProfileDTO) {
        AppUser updated = appUserService.updateProfile(
                userId,
                updateProfileDTO.getAvatarUrl(),
                updateProfileDTO.getCountry(),
                updateProfileDTO.getContinent()
        );
        return ResponseEntity.ok(updated);
    }
}