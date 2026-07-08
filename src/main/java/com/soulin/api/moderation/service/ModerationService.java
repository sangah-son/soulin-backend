package com.soulin.api.moderation.service;

import com.soulin.api.moderation.ModerationStatus;
import com.soulin.api.moderation.dto.ModerationApiRequest;
import com.soulin.api.moderation.dto.ModerationApiResponse;
import com.soulin.api.moderation.dto.ModerationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Service
public class ModerationService {
    private static final String MODERATION_API_URL = "https://MINSEONG12-moderation.hf.space/moderate";
    private static final double DEFAULT_THRESHOLD = 0.7;
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(8);

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

    public ModerationService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(CONNECT_TIMEOUT)
                .readTimeout(READ_TIMEOUT)
                .build();
    }

    public ModerationResult moderate(String title, String content) {
        String text = title + "\n" + content;
        ModerationApiRequest request = new ModerationApiRequest(text, DEFAULT_THRESHOLD);
        long startedAt = System.currentTimeMillis();

        try {
            ResponseEntity<ModerationApiResponse> response = restTemplate.postForEntity(
                    MODERATION_API_URL,
                    request,
                    ModerationApiResponse.class
            );

            ModerationApiResponse body = response.getBody();

            if (body == null) {
                throw new IllegalStateException("모더레이션 응답이 비어 있습니다.");
            }

            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.info("Moderation API succeeded. elapsedMs={}, harmful={}, labels={}",
                    elapsedMs,
                    body.is_harmful(),
                    body.getOverall_labels());

            boolean hasBlockedLabel = body.getOverall_labels().stream()
                    .anyMatch(BLOCKED_LABELS::contains);

            if (body.is_harmful() || hasBlockedLabel) {
                String reason = String.join(", ", body.getOverall_labels());
                return new ModerationResult(
                        ModerationStatus.REJECTED,
                        reason,
                        body.getOverall_labels()
                );
            }

            return new ModerationResult(
                    ModerationStatus.APPROVED,
                    null,
                    body.getOverall_labels()
            );
        } catch (RestClientException e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.warn("Moderation API failed. elapsedMs={}", elapsedMs, e);
            throw new IllegalStateException("모더레이션 서버 호출에 실패했습니다.", e);
        }
    }
}
