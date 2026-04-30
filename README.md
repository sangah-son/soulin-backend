## 인증/인가

- 현재는 JWT Access Token 기반 인증을 사용합니다.
- 로그아웃은 서버 세션 만료 방식이 아니라, 클라이언트에서 Access Token을 삭제하는 방식으로 처리합니다.
- 현재 Refresh Token은 구현되어 있지 않습니다.
- 비밀번호 변경 시 기존 Access Token 강제 만료 기능은 아직 구현되어 있지 않습니다.

## 시간/타임존 처리

- 모든 시간 데이터는 한국 시간(KST, +09:00)을 기준으로 저장하고 응답합니다.
- DB 컬럼(`DATETIME`)과 엔티티(`BaseEntity`의 `LocalDateTime`)는 timezone 정보 없이 시각 값만 보관합니다.
- 응답 DTO는 `OffsetDateTime`을 사용하여 `+09:00` 마커가 포함된 ISO-8601 형식으로 직렬화됩니다.
    - 예: `"createdAt": "2026-04-30T14:56:58.359716+09:00"`
- `LocalDateTime → OffsetDateTime` 변환은 `global/common/TimeZoneUtils.toKst()` 헬퍼를 통해 응답 DTO 빌드 시점에 일관되게 수행합니다.

### 설계 의도

`LocalDateTime`은 timezone 정보를 갖지 않는 타입이므로 직렬화 시 마커 없이 `"2026-04-30T14:56:58"` 형태로만 출력됩니다. 마커가 없으면 클라이언트가 UTC로 오해해 시간이 9시간 어긋나게 표시되는 문제가 발생합니다.

엔티티/DB는 단순한 시각 값만 다루는 책임이고, timezone 마커는 외부와의 인터페이스에서만 필요한 정보이므로, 응답 DTO 단계에서만 `OffsetDateTime`으로 변환하여 책임을 분리했습니다. 이 방식은 JVM 기본 타임존 설정에 의존하지 않으며, BaseEntity와 모든 엔티티의 변경 없이 응답 형식만 정상화할 수 있다는 장점이 있습니다.

## 추후 개선 예정

- Refresh Token 도입
- 토큰 재발급 API 구현
- 로그아웃 시 토큰 무효화 처리
- 비밀번호 변경 시 기존 세션/토큰 만료 처리
