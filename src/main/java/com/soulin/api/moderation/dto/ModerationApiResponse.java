package com.soulin.api.moderation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ModerationApiResponse {
    //감지된 유해 유형들
    private List<String> overall_labels;
    //이 글의 유해 여부
    private boolean is_harmful;
    //문장별 분석 결과
    private List<SentenceResult> sentence_results;

    public boolean is_harmful(){
        return is_harmful;
    }

    //문장 하나마다 결과 나옴, 그 한 줄 결과 담음
    @Getter
    @NoArgsConstructor
    public static class SentenceResult{
        private String sentence;
        private List<String> labels;
        private boolean is_harmful;
    }
}
