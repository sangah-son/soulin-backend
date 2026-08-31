package com.soulin.api.safety.dto;

import com.soulin.api.crisis.dto.CrisisDetectionResult;
import com.soulin.api.moderation.dto.ModerationResult;

public record ContentSafetyResult(
        ModerationResult moderation,
        CrisisDetectionResult crisis
) {
}
