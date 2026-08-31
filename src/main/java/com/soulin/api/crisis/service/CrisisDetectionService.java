package com.soulin.api.crisis.service;

import com.soulin.api.crisis.dto.CrisisDetectionApiRequest;
import com.soulin.api.crisis.dto.CrisisDetectionApiResponse;
import com.soulin.api.crisis.dto.CrisisDetectionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class CrisisDetectionService {
    private static final String API_KEY_HEADER = "x-api-key";

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;

    public CrisisDetectionService(
            @Qualifier("crisisDetectionRestTemplate") RestTemplate restTemplate,
            @Value("${app.crisis-detection.api-url}") String apiUrl,
            @Value("${app.crisis-detection.api-key:}") String apiKey
    ) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Crisis detection is not configured because its API key is missing. Requests will fail open.");
        }
    }

    public CrisisDetectionResult detect(String title, String content) {
        if (apiKey == null || apiKey.isBlank()) {
            return CrisisDetectionResult.fallback();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(API_KEY_HEADER, apiKey);
        HttpEntity<CrisisDetectionApiRequest> request = new HttpEntity<>(
                new CrisisDetectionApiRequest(title + "\n" + content),
                headers
        );
        long startedAt = System.currentTimeMillis();

        try {
            ResponseEntity<CrisisDetectionApiResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    CrisisDetectionApiResponse.class
            );
            CrisisDetectionApiResponse body = response.getBody();

            if (body == null || body.getCritical() == null || Boolean.TRUE.equals(body.getError())) {
                throw new IllegalStateException("Crisis detection returned an invalid response.");
            }

            CrisisDetectionResult result = CrisisDetectionResult.from(body);
            log.info("Crisis detection succeeded. elapsedMs={}, status={}",
                    System.currentTimeMillis() - startedAt,
                    result.status());
            return result;
        } catch (RestClientException | IllegalStateException e) {
            log.warn("Crisis detection failed. elapsedMs={}, errorType={}. Request will fail open.",
                    System.currentTimeMillis() - startedAt,
                    e.getClass().getSimpleName());
            return CrisisDetectionResult.fallback();
        }
    }
}
