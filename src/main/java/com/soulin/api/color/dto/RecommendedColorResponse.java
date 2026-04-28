package com.soulin.api.color.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendedColorResponse {
    private Integer colorId;
    private String colorName;
    private String colorCode;
    private String reason;
}
