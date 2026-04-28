package com.soulin.api.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReceivedReactionItem {
    private Integer reactionTypeId;
    private String reactionName;
    private String reactionText;
    private Integer colorId;
    private String colorCode;
    private Long count; // JPQL count(*)이 Long으로 떨어짐
}
