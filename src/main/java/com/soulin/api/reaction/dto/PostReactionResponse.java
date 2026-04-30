package com.soulin.api.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class PostReactionResponse {
    private Long postReactionId;
    private Long postId;
    private Long userId;
    private Integer colorId;
    private Integer reactionTypeId;
    private OffsetDateTime createdAt;
}
