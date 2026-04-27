package com.soulin.api.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyReactionSummaryResponse {
    private Integer totalReactionCount;
    private List<ReactionColorRatioResponse> colorRatios;
    private List<PostReactionSummaryItemResponse> postReactionSummaries;
}
