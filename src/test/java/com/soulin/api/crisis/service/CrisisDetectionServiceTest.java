package com.soulin.api.crisis.service;

import com.soulin.api.crisis.CrisisStatus;
import com.soulin.api.crisis.dto.CrisisDetectionApiRequest;
import com.soulin.api.crisis.dto.CrisisDetectionApiResponse;
import com.soulin.api.crisis.dto.CrisisDetectionResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrisisDetectionServiceTest {
    private static final String API_URL = "https://example.test/webhook/crisis-detection";
    private static final String API_KEY = "test-api-key";

    @Mock
    private RestTemplate restTemplate;

    @Test
    void sendsApiKeyAndMapsCriticalResponse() {
        CrisisDetectionService service = new CrisisDetectionService(restTemplate, API_URL, API_KEY);
        CrisisDetectionApiResponse apiResponse = new CrisisDetectionApiResponse(
                true,
                "직접",
                0.95,
                "위기 표현 감지",
                false
        );
        when(restTemplate.exchange(
                eq(API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(CrisisDetectionApiResponse.class)
        )).thenReturn(ResponseEntity.ok(apiResponse));

        CrisisDetectionResult result = service.detect("제목", "본문");

        assertThat(result.status()).isEqualTo(CrisisStatus.CRITICAL);
        assertThat(result.isCritical()).isTrue();

        ArgumentCaptor<HttpEntity> requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq(API_URL),
                eq(HttpMethod.POST),
                requestCaptor.capture(),
                eq(CrisisDetectionApiResponse.class)
        );
        HttpEntity<?> request = requestCaptor.getValue();
        assertThat(request.getHeaders().getFirst("x-api-key")).isEqualTo(API_KEY);
        assertThat(request.getBody()).isEqualTo(new CrisisDetectionApiRequest("제목\n본문"));
    }

    @Test
    void failsOpenOnTimeout() {
        CrisisDetectionService service = new CrisisDetectionService(restTemplate, API_URL, API_KEY);
        when(restTemplate.exchange(
                eq(API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(CrisisDetectionApiResponse.class)
        )).thenThrow(new ResourceAccessException("timeout"));

        CrisisDetectionResult result = service.detect("제목", "본문");

        assertThat(result.status()).isEqualTo(CrisisStatus.ERROR);
        assertThat(result.isCritical()).isFalse();
    }

    @Test
    void failsOpenWithoutApiKeyWithoutCallingWebhook() {
        CrisisDetectionService service = new CrisisDetectionService(restTemplate, API_URL, " ");

        CrisisDetectionResult result = service.detect("제목", "본문");

        assertThat(result.status()).isEqualTo(CrisisStatus.ERROR);
        verify(restTemplate, never()).exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(CrisisDetectionApiResponse.class)
        );
    }
}
