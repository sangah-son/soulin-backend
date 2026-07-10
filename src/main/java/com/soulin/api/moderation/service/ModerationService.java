package com.soulin.api.moderation.service;

import com.soulin.api.moderation.ModerationStatus;
import com.soulin.api.moderation.dto.ModerationApiRequest;
import com.soulin.api.moderation.dto.ModerationApiResponse;
import com.soulin.api.moderation.dto.ModerationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ModerationService {
    private static final double DEFAULT_THRESHOLD = 0.7;
    private static final String MODERATION_API_FAILED_LABEL = "MODERATION_API_FAILED";

    private static final Set<String> BLOCKED_LABELS = Set.of(
            "ABUSE",
            "HATE",
            "VIOLENCE",
            "SEXUAL",
            "DISCRIMINATION",
            "CRIME",
            "CENSURE"
    );

    private final RestTemplate restTemplate;
    private final String moderationApiUrl;
    private final boolean failOpen;

    public ModerationService(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${app.moderation.api-url:https://minseong12-moderation.hf.space/moderate}") String moderationApiUrl,
            @Value("${app.moderation.connect-timeout-ms:5000}") long connectTimeoutMs,
            @Value("${app.moderation.read-timeout-ms:20000}") long readTimeoutMs,
            @Value("${app.moderation.fail-open:true}") boolean failOpen
    ) {
        this.moderationApiUrl = moderationApiUrl;
        this.failOpen = failOpen;
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .readTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
    }

    public ModerationResult moderate(String title, String content) {
        String text = title + "\n" + content;
        ModerationApiRequest request = new ModerationApiRequest(text, DEFAULT_THRESHOLD);
        long startedAt = System.currentTimeMillis();

        try {
            ResponseEntity<ModerationApiResponse> response = restTemplate.postForEntity(
                    moderationApiUrl,
                    request,
                    ModerationApiResponse.class
            );

            ModerationApiResponse body = response.getBody();

            if (body == null) {
                throw new IllegalStateException("모더레이션 응답이 비어 있습니다.");
            }

            List<String> labels = body.getOverall_labels() == null ? List.of() : body.getOverall_labels();
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.info("Moderation API succeeded. elapsedMs={}, harmful={}, labels={}",
                    elapsedMs,
                    body.is_harmful(),
                    labels);

            boolean hasBlockedLabel = labels.stream()
                    .anyMatch(BLOCKED_LABELS::contains);

            if (body.is_harmful() || hasBlockedLabel) {
                String reason = String.join(", ", labels);
                return new ModerationResult(
                        ModerationStatus.REJECTED,
                        reason,
                        labels
                );
            }

            return new ModerationResult(
                    ModerationStatus.APPROVED,
                    null,
                    labels
            );
        } catch (RestClientException | IllegalStateException e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.warn("Moderation API failed. elapsedMs={}, failOpen={}", elapsedMs, failOpen, e);
            if (failOpen) {
                return new ModerationResult(
                        ModerationStatus.APPROVED,
                        MODERATION_API_FAILED_LABEL,
                        List.of(MODERATION_API_FAILED_LABEL)
                );
            }
            throw new IllegalStateException("모더레이션 서버 호출에 실패했습니다.", e);
        }
    }
}
