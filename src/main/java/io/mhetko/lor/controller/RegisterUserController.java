package io.mhetko.lor.controller;

import io.mhetko.lor.dto.RegisterUserDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
public class RegisterUserController {

    private final AppUserService appUserService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account. Requires email confirmation, password repetition, " +
                    "and acceptance of terms and conditions. The password must meet complexity requirements.",
            tags = {"User"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AppUser.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data or validation requirements not met"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Username or email already exists"
            )
    })
    @ResponseBody
    public ResponseEntity<AppUser> registerUser(@RequestBody @Valid RegisterUserDTO registerUserDTO) {
        AppUser user = appUserService.registerUser(registerUserDTO);
        return ResponseEntity.status(201).body(user);
    }

    @GetMapping("/confirm")
    @Operation(
            summary = "Activate user account",
            description = "Activates a user account using the provided activation token.",
            tags = {"User"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account activated"),
            @ApiResponse(responseCode = "404", description = "Invalid token")
    })
    public ResponseEntity<String> confirmUser(@RequestParam String token) {
        appUserService.activateUser(token);
        return ResponseEntity.ok("Account has been activated!");
    }
}