package com.soulin.api.post.entity;

import com.soulin.api.global.common.BaseEntity;
import com.soulin.api.color.entity.Color;
import com.soulin.api.post.PostStatus;
import com.soulin.api.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_id")
    private Long postId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name="is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="color_id", nullable = false)
    private Color color;

    public Post(String title, String content, Boolean isPublic, PostStatus status, User user, Color color){
        this.title=title;
        this.content=content;
        this.isPublic=isPublic;
        this.status=status;
        this.user=user;
        this.color=color;
    }

    public void updatePost(String title, String content, Boolean isPublic, Color color) {
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.color = color;
    }

    public void publish(){
        this.status=PostStatus.PUBLISHED;
        this.publishedAt=LocalDateTime.now();
    }

    public void reject(){
        this.status=PostStatus.REJECTED;
    }

    public void saveAsPrivateDraft() {
        this.status = PostStatus.DRAFT;
        this.isPublic = false;
        this.publishedAt = null;
    }
}
