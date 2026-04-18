package com.soulin.api.moderation.dto;

import com.soulin.api.moderation.ModerationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ModerationResult {
    private ModerationStatus status;
    private String reason;
    private List<String> labels;
}

//외부 AI 응답을 서비스 로직에 맞게 바꾼 결과
