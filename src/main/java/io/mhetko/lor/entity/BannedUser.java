package io.mhetko.lor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "banned_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannedUser {

    @Id
    @Column(name = "user_id")
    private Long userId;
    @Size(max = 500)
    private String reason;
    @Column(name = "banned_at")
    private LocalDateTime bannedAt;
    @Column(name = "banned_by")
    private Long bannedBy;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
