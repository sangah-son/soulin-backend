package com.soulin.api.moderation.entity;

import com.soulin.api.moderation.ModerationStatus;
import com.soulin.api.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name="moderations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Moderation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="moderation_id")
    private Long moderationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModerationStatus status;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id", nullable = false)
    private Post post;

    public Moderation(ModerationStatus status,String reason,Post post){
        this.status=status;
        this.reason=reason;
        this.post=post;
    }
}
