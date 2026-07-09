package com.soulin.api.notification.event;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationWebSocketListener {
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotification(NotificationCreatedEvent event) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(event.recipientId()),
                "/queue/notifications",
                event.notification()
        );
    }
}
