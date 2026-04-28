package com.soulin.api.emotion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmotionResult {
    private String emotion;
    private double confidence;
}
