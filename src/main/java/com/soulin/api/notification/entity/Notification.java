package com.soulin.api.notification.entity;

import com.soulin.api.global.common.BaseEntity;
import com.soulin.api.notification.NotificationType;
import com.soulin.api.post.entity.Post;
import com.soulin.api.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 30)
    private NotificationType type;

    @Column(name = "reaction_name", nullable = false, length = 100)
    private String reactionName;

    @Column(name = "color_name", nullable = false, length = 100)
    private String colorName;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    public Notification(
            User recipient,
            User actor,
            Post post,
            NotificationType type,
            String reactionName,
            String colorName
    ) {
        this.recipient = recipient;
        this.actor = actor;
        this.post = post;
        this.type = type;
        this.reactionName = reactionName;
        this.colorName = colorName;
        this.read = false;
    }

    public void markAsRead() {
        this.read = true;
    }
}
