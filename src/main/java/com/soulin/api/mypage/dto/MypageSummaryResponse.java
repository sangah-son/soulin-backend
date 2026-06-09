package com.soulin.api.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class MypageSummaryResponse {
    private String userName;
    private Integer monthlyPostCount;
    private Integer totalReceivedReactionCount;
    private CalendarColorResponse monthlyTopColor;
    private List<CalendarDayResponse> calendarDays;
    private List<ColorStatResponse> colorStats;

    @Getter
    @AllArgsConstructor
    public static class CalendarDayResponse {
        private LocalDate date;
        private Integer postCount;
        private Long representativePostId;
        private Integer representativeColorId;
        private String representativeColorName;
        private String representativeColorCode;
        private List<SelectablePostColorResponse> selectableColors;
    }

    @Getter
    @AllArgsConstructor
    public static class SelectablePostColorResponse {
        private Long postId;
        private String title;
        private Integer colorId;
        private String colorName;
        private String colorCode;
    }

    @Getter
    @AllArgsConstructor
    public static class ColorStatResponse {
        private Integer colorId;
        private String colorName;
        private String colorCode;
        private Integer count;
        private Double percentage;
    }

    @Getter
    @AllArgsConstructor
    public static class CalendarColorResponse {
        private Integer colorId;
        private String colorName;
        private String colorCode;
        private Integer count;
        private Double percentage;
    }
}
