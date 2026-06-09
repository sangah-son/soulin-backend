package com.soulin.api.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RepresentativePostResponse {
    private LocalDate representDate;
    private Long representativePostId;
    private Integer representativeColorId;
    private String representativeColorName;
    private String representativeColorCode;
}
