package io.mhetko.lor.controller;

import io.mhetko.lor.dto.AppUserDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @PostMapping("/register")
    @ResponseBody
    public AppUser registerUser(@RequestBody AppUserDTO appUserDTO) {
        return appUserService.registerUser(appUserDTO);
    }
}
