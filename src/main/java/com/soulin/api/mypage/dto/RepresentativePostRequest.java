package com.soulin.api.mypage.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RepresentativePostRequest {
    @NotNull
    private Long postId;
}
