package com.soulin.api.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReactionColorStatResponse {
    private Integer colorId;
    private String colorName;
    private String colorCode;
    private Integer count;
    private Double ratio;
}
