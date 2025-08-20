package io.mhetko.lor.controller;

import io.mhetko.lor.dto.NotificationDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.repository.AppUserRepository;
import io.mhetko.lor.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping
    public List<NotificationDTO> getUserNotifications(Principal principal) {
        return notificationService.getUserNotificationsByPrincipal(principal);
    }


    @PutMapping("/{id}/read")
    public void markNotificationAsRead(@PathVariable Long id, Principal principal) {
        notificationService.markAsReadByPrincipal(id, principal);
    }
}