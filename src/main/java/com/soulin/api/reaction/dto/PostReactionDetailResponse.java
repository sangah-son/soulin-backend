package com.soulin.api.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostReactionDetailResponse {
    private Long postId;
    private String title;
    private List<ReactionColorStatResponse> colorStats;
    private List<ReactionTextStatResponse> reactionTextStats;
}
