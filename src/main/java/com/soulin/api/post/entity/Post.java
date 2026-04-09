package com.soulin.api.post.entity;

import com.soulin.api.global.common.BaseEntity;
import com.soulin.api.color.entity.Color;
import com.soulin.api.post.PostStatus;
import com.soulin.api.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
