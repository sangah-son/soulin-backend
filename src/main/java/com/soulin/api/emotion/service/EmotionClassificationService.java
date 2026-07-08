package com.soulin.api.emotion.service;

import com.soulin.api.emotion.dto.EmotionApiRequest;
import com.soulin.api.emotion.dto.EmotionApiResponse;
import com.soulin.api.emotion.dto.EmotionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Service
public class EmotionClassificationService {

    private static final String EMOTION_API_URL = "https://MINSEONG12-emotion.hf.space/predict";
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(8);

    private final RestTemplate restTemplate;

    public EmotionClassificationService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(CONNECT_TIMEOUT)
                .readTimeout(READ_TIMEOUT)
                .build();
    }

    public EmotionResult classify(String text) {
        EmotionApiRequest request = new EmotionApiRequest(text);
        long startedAt = System.currentTimeMillis();

        try {
            ResponseEntity<EmotionApiResponse> response = restTemplate.postForEntity(
                    EMOTION_API_URL,
                    request,
                    EmotionApiResponse.class
            );

            EmotionApiResponse body = response.getBody();
            if (body == null || body.getEmotion() == null) {
                throw new IllegalStateException("감정 분류 응답이 비어 있습니다.");
            }

            double confidence = body.getConfidence() == null ? 0.0 : body.getConfidence();
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.info("Emotion API succeeded. elapsedMs={}, emotion={}, confidence={}",
                    elapsedMs,
                    body.getEmotion(),
                    confidence);
            return new EmotionResult(body.getEmotion(), confidence);
        } catch (RestClientException e) {
            throw new IllegalStateException("감정 분류 서버 호출에 실패했습니다.", e);
        }
    }
}
