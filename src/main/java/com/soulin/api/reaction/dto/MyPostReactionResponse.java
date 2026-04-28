package com.soulin.api.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPostReactionResponse {
    private Long postReactionId;
    private Integer reactionTypeId;
    private String reactionName;   // 공감/응원/위로/지지
    private String reactionText;   // "나도 그래"
    private Integer colorId;
    private String colorName;
    private String colorCode;      // hex
}
