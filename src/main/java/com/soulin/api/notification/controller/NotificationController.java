package com.soulin.api.notification.controller;

import com.soulin.api.global.jwt.CustomUserPrincipal;
import com.soulin.api.notification.dto.NotificationResponse;
import com.soulin.api.notification.dto.UnreadNotificationCountResponse;
import com.soulin.api.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(notificationService.getNotifications(principal.getUserId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadNotificationCountResponse> getUnreadCount(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                new UnreadNotificationCountResponse(
                        notificationService.getUnreadCount(principal.getUserId())
                )
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(principal.getUserId(), notificationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        notificationService.markAllAsRead(principal.getUserId());
        return ResponseEntity.noContent().build();
    }
}
