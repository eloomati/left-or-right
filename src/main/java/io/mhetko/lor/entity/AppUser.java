package io.mhetko.lor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 5, max = 50)
    private String username;
    @Email
    @Size(max = 100)
    private String email;
    @Size(min = 8, max = 255)
    private String password;
    @Size(max = 20)
    private String role;
    @Column(name = "avatar_url")
    private String avatarUrl;
    @Size(max = 100)
    private String country;
    @Size(max = 100)
    private String continent;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "is_active")
    private Boolean isActive;
}
