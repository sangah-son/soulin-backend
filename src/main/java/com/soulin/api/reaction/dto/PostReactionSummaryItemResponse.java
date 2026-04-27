package com.soulin.api.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostReactionSummaryItemResponse {
    private Long postId;
    private String title;
    private String content;
    private TopReactionColorResponse topColor;
    private TopReactionTypeResponse topReactionType;
}
