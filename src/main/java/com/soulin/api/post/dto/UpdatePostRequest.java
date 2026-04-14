package com.soulin.api.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdatePostRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPublic;
    @NotNull
    private Integer colorId;
}
