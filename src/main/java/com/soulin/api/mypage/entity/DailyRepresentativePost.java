package com.soulin.api.mypage.entity;

import com.soulin.api.global.common.BaseEntity;
import com.soulin.api.post.entity.Post;
import com.soulin.api.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "daily_representative_posts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_daily_representative_user_date", columnNames = {"user_id", "represent_date"}),
                @UniqueConstraint(name = "uk_daily_representative_post", columnNames = "post_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyRepresentativePost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "representative_id")
    private Long representativeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "represent_date", nullable = false)
    private LocalDate representDate;

    public DailyRepresentativePost(User user, Post post, LocalDate representDate) {
        this.user = user;
        this.post = post;
        this.representDate = representDate;
    }

    public void updatePost(Post post) {
        this.post = post;
    }
}
