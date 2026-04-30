package com.soulin.api.global.common;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * 응답 직렬화 시 timezone 마커(+09:00)를 강제로 부착하기 위한 헬퍼.
 * <p>
 * DB와 엔티티는 timezone 정보가 없는 LocalDateTime(KST 기준 시각)을 그대로 사용하고,
 * 외부에 노출되는 응답 DTO에서만 OffsetDateTime으로 변환하여 ISO-8601 마커를 부착한다.
 */
public final class TimeZoneUtils {

    public static final ZoneOffset KST = ZoneOffset.of("+09:00");

    private TimeZoneUtils() {
    }

    /**
     * LocalDateTime을 KST(+09:00) OffsetDateTime으로 변환한다.
     * null 입력은 null로 그대로 반환.
     */
    public static OffsetDateTime toKst(LocalDateTime ldt) {
        return ldt == null ? null : ldt.atOffset(KST);
    }
}
