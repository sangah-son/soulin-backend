package com.soulin.api.notification.dto;

import com.soulin.api.notification.NotificationType;

import java.time.OffsetDateTime;

public record NotificationResponse(
        Long notificationId,
        NotificationType type,
        Long postId,
        String postTitle,
        Long actorId,
        String actorName,
        String reactionName,
        String colorName,
        boolean read,
        OffsetDateTime createdAt
) {
}
