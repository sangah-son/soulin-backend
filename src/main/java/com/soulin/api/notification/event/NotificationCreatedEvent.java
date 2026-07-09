package com.soulin.api.notification.event;

import com.soulin.api.notification.dto.NotificationResponse;

public record NotificationCreatedEvent(
        Long recipientId,
        NotificationResponse notification
) {
}
