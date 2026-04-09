package com.soulin.api.reaction.entity;

import com.soulin.api.global.common.BaseEntity;
import com.soulin.api.color.entity.Color;
import com.soulin.api.post.entity.Post;
import com.soulin.api.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="post_reaction", uniqueConstraints =
        {@UniqueConstraint(name="uk_post_reaction_post_user",columnNames = {"post_id","user_id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostReaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_reaction_id")
    private Long postReactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reaction_type_id", nullable = false)
    private ReactionType reactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    public PostReaction(ReactionType reactionType, User user, Post post, Color color){
        this.reactionType=reactionType;
        this.user=user;
        this.post=post;
        this.color=color;
    }

    //리액션 수정 메서드, 텍스트 리액션,컬러 중 하나만 수정 / 둘 다 수정 가능
    public void updateReaction(ReactionType reactionType, Color color){
        if(reactionType == null && color == null)throw new IllegalArgumentException(("리액션 수정할 것 없음"));
        if(reactionType != null)this.reactionType=reactionType;
        if(color != null)this.color=color;
    }
}
