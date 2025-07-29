package io.mhetko.lor.controller;

import io.mhetko.lor.dto.AppUserDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and sends an activation email.",
            tags = {"User"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @ResponseBody
    public AppUser registerUser(@RequestBody AppUserDTO appUserDTO) {
        return appUserService.registerUser(appUserDTO);
    }

    @GetMapping("/confirm")
    @Operation(
            summary = "Activate user account",
            description = "Activates a user account using the provided token.",
            tags = {"User"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account activated"),
            @ApiResponse(responseCode = "404", description = "Invalid token")
    })
    public String confirmUser(@RequestParam String token) {
        appUserService.activateUser(token);
        return "Account has been activated!";
    }
}
