package com.soulin.api.notification.service;

import com.soulin.api.color.entity.Color;
import com.soulin.api.global.common.TimeZoneUtils;
import com.soulin.api.notification.NotificationType;
import com.soulin.api.notification.dto.NotificationResponse;
import com.soulin.api.notification.entity.Notification;
import com.soulin.api.notification.event.NotificationCreatedEvent;
import com.soulin.api.notification.repository.NotificationRepository;
import com.soulin.api.post.entity.Post;
import com.soulin.api.reaction.entity.ReactionType;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private static final int MAX_NOTIFICATION_COUNT = 50;

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void createReactionNotification(
            User actor,
            Post post,
            ReactionType reactionType,
            Color color
    ) {
        User recipient = post.getUser();
        if (recipient.getId().equals(actor.getId())) {
            return;
        }

        Notification notification = notificationRepository.save(new Notification(
                recipient,
                actor,
                post,
                NotificationType.POST_REACTION,
                reactionType.getReactionName(),
                color.getColorName()
        ));
        eventPublisher.publishEvent(new NotificationCreatedEvent(
                recipient.getId(),
                toResponse(notification)
        ));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {
        User user = getUser(userId);
        return notificationRepository
                .findByRecipientOrderByCreatedAtDesc(user, PageRequest.of(0, MAX_NOTIFICATION_COUNT))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientAndReadFalse(getUser(userId));
    }

    public void markAsRead(Long userId, Long notificationId) {
        User user = getUser(userId);
        Notification notification = notificationRepository
                .findByNotificationIdAndRecipient(notificationId, user)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        notification.markAsRead();
    }

    public void markAllAsRead(Long userId) {
        User user = getUser(userId);
        notificationRepository.findAllByRecipientAndReadFalse(user)
                .forEach(Notification::markAsRead);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getType(),
                notification.getPost().getPostId(),
                notification.getPost().getTitle(),
                notification.getActor().getId(),
                notification.getActor().getUserName(),
                notification.getReactionName(),
                notification.getColorName(),
                notification.isRead(),
                TimeZoneUtils.toKst(notification.getCreatedAt())
        );
    }
}
