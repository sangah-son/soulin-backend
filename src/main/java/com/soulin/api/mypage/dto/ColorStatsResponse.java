package com.soulin.api.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class ColorStatsResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalPostCount;
    private List<ColorStatItem> colorStats;

    @Getter
    @AllArgsConstructor
    public static class ColorStatItem {
        private Integer colorId;
        private String colorName;
        private String colorCode;
        private Integer count;
        private Double percentage;
    }
}
