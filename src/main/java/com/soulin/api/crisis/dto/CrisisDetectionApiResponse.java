package com.soulin.api.crisis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrisisDetectionApiResponse {
    private Boolean critical;
    private String signal;
    private Double confidence;
    private String reason;
    private Boolean error;
}
