package io.mhetko.lor.controller;

import io.mhetko.lor.dto.ChangePasswordDTO;
import io.mhetko.lor.dto.UpdateProfileDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<AppUser> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        AppUser updated = appUserService.uploadAvatar(userId, file);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long userId,
            @RequestBody ChangePasswordDTO dto) {
        appUserService.changePassword(userId, dto);
        return ResponseEntity.ok().build();
    }
}