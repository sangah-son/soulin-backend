package com.soulin.api.emotion.service;

import com.soulin.api.emotion.dto.EmotionApiRequest;
import com.soulin.api.emotion.dto.EmotionApiResponse;
import com.soulin.api.emotion.dto.EmotionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class EmotionClassificationService {

    private static final String EMOTION_API_URL = "https://MINSEONG12-emotion.hf.space/predict";

    private final RestTemplate restTemplate = new RestTemplate();

    public EmotionResult classify(String text) {
        EmotionApiRequest request = new EmotionApiRequest(text);

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
            return new EmotionResult(body.getEmotion(), confidence);
        } catch (RestClientException e) {
            throw new IllegalStateException("감정 분류 서버 호출에 실패했습니다.", e);
        }
    }
}
