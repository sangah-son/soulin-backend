package com.soulin.api.color.service;

import com.soulin.api.color.dto.RecommendPostColorRequest;
import com.soulin.api.color.dto.RecommendPostColorResponse;
import com.soulin.api.color.dto.RecommendedColorResponse;
import com.soulin.api.color.entity.Color;
import com.soulin.api.color.repository.ColorRepository;
import com.soulin.api.emotion.dto.EmotionResult;
import com.soulin.api.emotion.service.EmotionClassificationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ColorRecommendationService {

    private final EmotionClassificationService emotionClassificationService;
    private final ColorRepository colorRepository;

    /**
     * 감정 레이블별 색상 추천 매핑.
     * 감정분류 API가 반환하는 6개 레이블(분노/불안/당황/슬픔/상처/기쁨)에 대해
     * 우리 시드 색상명(RED/ORANGE/YELLOW/LIGHTGREEN/GREEN/LIGHTBLUE/BLUE/NAVY/PURPLE/PINK/GREY/BLACK)으로 매핑한다.
     */
    private static final Map<String, List<ColorMapping>> EMOTION_COLOR_MAPPINGS = Map.of(
            "분노", List.of(
                    new ColorMapping("RED", "분노와 긴장의 감정이 강하게 느껴집니다."),
                    new ColorMapping("BLACK", "고각성의 부정적 감정이 묻어납니다."),
                    new ColorMapping("PURPLE", "강한 권능감이 함께 느껴집니다.")
            ),
            "불안", List.of(
                    new ColorMapping("YELLOW", "걱정과 초조함이 느껴집니다."),
                    new ColorMapping("GREY", "공포와 무기력의 흔적이 느껴집니다."),
                    new ColorMapping("PURPLE", "두려움이 함께 느껴집니다.")
            ),
            "당황", List.of(
                    new ColorMapping("RED", "긴장된 감정이 느껴집니다."),
                    new ColorMapping("YELLOW", "불확실함이 묻어납니다."),
                    new ColorMapping("PURPLE", "경외와 양가의 감정이 느껴집니다.")
            ),
            "슬픔", List.of(
                    new ColorMapping("BLUE", "슬픔의 감정이 가장 짙게 느껴집니다."),
                    new ColorMapping("NAVY", "고독과 깊은 부정의 감정이 느껴집니다."),
                    new ColorMapping("GREY", "저각성의 부정적 감정이 느껴집니다.")
            ),
            "상처", List.of(
                    new ColorMapping("PINK", "관계적 상처가 느껴집니다."),
                    new ColorMapping("GREEN", "질투의 감정이 느껴집니다."),
                    new ColorMapping("BLUE", "감정적 고통이 느껴집니다.")
            ),
            "기쁨", List.of(
                    new ColorMapping("ORANGE", "고각성의 긍정적 감정이 느껴집니다."),
                    new ColorMapping("LIGHTBLUE", "일관되고 안정적인 긍정의 감정이 느껴집니다."),
                    new ColorMapping("LIGHTGREEN", "잔잔하고 저각성의 긍정 감정이 느껴집니다.")
            )
    );

    public RecommendPostColorResponse recommend(RecommendPostColorRequest request) {
        String text = request.getTitle() + "\n" + request.getContent();
        EmotionResult emotionResult = emotionClassificationService.classify(text);

        List<ColorMapping> mappings = EMOTION_COLOR_MAPPINGS.get(emotionResult.getEmotion());
        if (mappings == null) {
            throw new IllegalStateException(
                    "매핑되지 않은 감정 레이블입니다: " + emotionResult.getEmotion()
            );
        }

        List<RecommendedColorResponse> recommended = mappings.stream()
                .map(mapping -> {
                    Color color = colorRepository.findByColorName(mapping.getColorName())
                            .orElseThrow(() -> new IllegalStateException(
                                    "색상을 찾을 수 없습니다: " + mapping.getColorName()
                            ));
                    return new RecommendedColorResponse(
                            color.getColorId(),
                            color.getColorName(),
                            color.getColorCode(),
                            mapping.getReason()
                    );
                })
                .toList();

        return new RecommendPostColorResponse(recommended);
    }

    @Getter
    @AllArgsConstructor
    private static class ColorMapping {
        private final String colorName;
        private final String reason;
    }
}
