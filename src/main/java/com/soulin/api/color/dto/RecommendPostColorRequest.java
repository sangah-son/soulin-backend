package com.soulin.api.color.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendPostColorRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
