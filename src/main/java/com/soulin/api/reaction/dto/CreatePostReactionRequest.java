package com.soulin.api.reaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePostReactionRequest {
    @NotNull
    private Integer colorId;
    @NotNull
    private Integer reactionTypeId;
}
