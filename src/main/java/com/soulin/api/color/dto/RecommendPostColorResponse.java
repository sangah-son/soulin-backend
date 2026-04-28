package com.soulin.api.color.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecommendPostColorResponse {
    private List<RecommendedColorResponse> recommendedColors;
}
