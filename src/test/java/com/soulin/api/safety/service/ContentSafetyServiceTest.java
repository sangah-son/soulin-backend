package com.soulin.api.safety.service;

import com.soulin.api.crisis.CrisisStatus;
import com.soulin.api.crisis.dto.CrisisDetectionResult;
import com.soulin.api.crisis.service.CrisisDetectionService;
import com.soulin.api.moderation.ModerationStatus;
import com.soulin.api.moderation.dto.ModerationResult;
import com.soulin.api.moderation.service.ModerationService;
import com.soulin.api.safety.dto.ContentSafetyResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContentSafetyServiceTest {
    @Test
    void callsModerationAndCrisisDetectionInParallel() throws Exception {
        ModerationService moderationService = mock(ModerationService.class);
        CrisisDetectionService crisisDetectionService = mock(CrisisDetectionService.class);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CyclicBarrier bothCallsStarted = new CyclicBarrier(2);

        ModerationResult moderationResult = new ModerationResult(
                ModerationStatus.APPROVED,
                null,
                List.of()
        );
        CrisisDetectionResult crisisResult = new CrisisDetectionResult(
                CrisisStatus.NONE,
                "없음",
                0.98,
                "위기 신호 없음"
        );

        when(moderationService.moderate("제목", "본문")).thenAnswer(invocation -> {
            bothCallsStarted.await(1, TimeUnit.SECONDS);
            return moderationResult;
        });
        when(crisisDetectionService.detect("제목", "본문")).thenAnswer(invocation -> {
            bothCallsStarted.await(1, TimeUnit.SECONDS);
            return crisisResult;
        });

        try {
            ContentSafetyService service = new ContentSafetyService(
                    moderationService,
                    crisisDetectionService,
                    executor
            );

            ContentSafetyResult result = service.evaluate("제목", "본문");

            assertThat(result.moderation()).isSameAs(moderationResult);
            assertThat(result.crisis()).isSameAs(crisisResult);
        } finally {
            executor.shutdownNow();
        }
    }
}
