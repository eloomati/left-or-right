package io.mhetko.lor.controller;

import io.mhetko.lor.dto.AppUserDTO;
import io.mhetko.lor.dto.LoginUserDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.service.LoginService;
import io.mhetko.lor.util.UserUtils;
import io.mhetko.lor.mapper.AppUserMapper;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class LoginUserController {

    private final LoginService loginService;
    private final UserUtils userUtils;
    private final AppUserMapper appUserMapper;

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Logs in a user based on username and password. Returns a JWT token upon successful authentication.",
            tags = {"User"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully logged in, returns JWT token",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid login credentials"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public ResponseEntity<String> login(@RequestBody @Valid LoginUserDTO loginUserDTO){
        String token = loginService.login(loginUserDTO);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current user info",
            description = "Returns information about the currently authenticated user based on JWT token.",
            tags = {"User"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Current user info returned",
                    content = @Content(schema = @Schema(implementation = AppUserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated"
            )
    })
    public ResponseEntity<AppUserDTO> getCurrentUserInfo() {
        AppUser user = userUtils.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return ResponseEntity.ok(appUserMapper.mapToAppUserDTO(user));
    }
}