package com.soulin.api.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReactionTextStatResponse {
    private Integer reactionTypeId;
    private String reactionName;
    private String reactionText;
    private Integer count;
}
