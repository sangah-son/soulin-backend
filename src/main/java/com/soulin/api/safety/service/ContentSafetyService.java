package com.soulin.api.safety.service;

import com.soulin.api.crisis.dto.CrisisDetectionResult;
import com.soulin.api.crisis.service.CrisisDetectionService;
import com.soulin.api.moderation.dto.ModerationResult;
import com.soulin.api.moderation.service.ModerationService;
import com.soulin.api.safety.dto.ContentSafetyResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class ContentSafetyService {
    private final ModerationService moderationService;
    private final CrisisDetectionService crisisDetectionService;
    private final Executor executor;

    public ContentSafetyService(
            ModerationService moderationService,
            CrisisDetectionService crisisDetectionService,
            @Qualifier("contentSafetyExecutor") Executor executor
    ) {
        this.moderationService = moderationService;
        this.crisisDetectionService = crisisDetectionService;
        this.executor = executor;
    }

    public ContentSafetyResult evaluate(String title, String content) {
        CompletableFuture<ModerationResult> moderation = CompletableFuture.supplyAsync(
                () -> moderationService.moderate(title, content),
                executor
        );
        CompletableFuture<CrisisDetectionResult> crisis = CompletableFuture.supplyAsync(
                () -> crisisDetectionService.detect(title, content),
                executor
        );

        return new ContentSafetyResult(moderation.join(), crisis.join());
    }
}
