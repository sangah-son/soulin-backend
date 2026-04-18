package com.soulin.api.post.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PublishPostRequest {
    @NotNull
    private Long postId;
}
