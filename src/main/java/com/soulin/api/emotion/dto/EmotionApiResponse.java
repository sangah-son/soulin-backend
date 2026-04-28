package com.soulin.api.emotion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class EmotionApiResponse {
    // 최종 예측 감정 레이블 (분노/불안/당황/슬픔/상처/기쁨 중 하나)
    private String emotion;
    // 예측 감정의 확률값 (0~1)
    private Double confidence;
    // 해당 감정에 매핑된 색상 코드 배열 (HEX, 최대 3개)
    private List<String> colors;
    // 6개 감정 전체의 확률값 딕셔너리
    private Map<String, Double> scores;
}
