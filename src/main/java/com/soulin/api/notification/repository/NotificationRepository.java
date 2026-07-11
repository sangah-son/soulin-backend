package com.soulin.api.notification.repository;

import com.soulin.api.notification.entity.Notification;
import com.soulin.api.post.entity.Post;
import com.soulin.api.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);

    long countByRecipientAndReadFalse(User recipient);

    Optional<Notification> findByNotificationIdAndRecipient(Long notificationId, User recipient);

    List<Notification> findAllByRecipientAndReadFalse(User recipient);

    void deleteAllByPost(Post post);
}
