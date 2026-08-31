package com.soulin.api.crisis.dto;

import com.soulin.api.crisis.CrisisStatus;

public record CrisisDetectionResult(
        CrisisStatus status,
        String signal,
        Double confidence,
        String reason
) {
    private static final String API_FAILED_REASON = "CRISIS_DETECTION_API_FAILED";

    public static CrisisDetectionResult from(CrisisDetectionApiResponse response) {
        CrisisStatus status = Boolean.TRUE.equals(response.getCritical())
                ? CrisisStatus.CRITICAL
                : CrisisStatus.NONE;
        return new CrisisDetectionResult(
                status,
                response.getSignal(),
                response.getConfidence(),
                response.getReason()
        );
    }

    public static CrisisDetectionResult fallback() {
        return new CrisisDetectionResult(CrisisStatus.ERROR, null, null, API_FAILED_REASON);
    }

    public boolean isCritical() {
        return status == CrisisStatus.CRITICAL;
    }
}
