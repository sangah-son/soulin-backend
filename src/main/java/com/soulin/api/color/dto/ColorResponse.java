package com.soulin.api.color.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ColorResponse {
    private Integer colorId;
    private String colorName;
    private String colorCode;
}
