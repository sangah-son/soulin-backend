package com.soulin.api.moderation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModerationApiRequest {
    private String text;  //검사할 전체 문장
    private Double threshold;  //판정 민감도
}
