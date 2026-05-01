## 인증(Auth)
이 프로젝트는 JWT 기반 인증을 사용하며, Access Token과 Refresh Token을 함께 발급한다.

### 토큰 정책

| 구분          | 만료 시간  | 용도             |
|-------------|--------|----------------|
| Access Token | 15분    | 인증이 필요한 API 요청 |
| Refresh Token | 14일    | Access Token 재발급 |

- Access Token은 `Authorization` 헤더에 `Bearer` 방식으로 전달한다.
- Refresh Token은 모바일 클라이언트에서 안전한 저장소에 보관한다.
- Refresh Token은 DB에 원문이 아닌 해시값으로 저장한다.
- Refresh Token Rotation을 사용한다. 토큰 재발급 시 Refresh Token도 새로 발급되며, 기존 Refresh Token은 더 이상 사용할 수 없다.
- Access Token이 만료되면 Refresh Token으로 새 토큰 발급받는다.

### 비밀번호 변경 성공 시 처리
- 사용자의 모든 Refresh Token을 무효화한다.
- 사용자의 tokenVersion을 증가시킨다.
- 기존 Access Token은 더 이상 인증에 사용할 수 없다.
- 새 비밀번호로 다시 로그인해야 한다.

### 세션 만료 처리
- 로그아웃 시 현재 세션의 Refresh Token이 무효화되며, 해당 세션의 기존 Access Token도 더 이상 인증에 사용할 수 없다.
- 비밀번호 변경 시 기존 Access Token을 즉시 차단하기 위해 tokenVersion을 사용한다.
- Access Token 발급 시 현재 사용자의 tokenVersion을 claim에 포함한다.
- 요청 인증 시 JWT의 tokenVersion과 DB의 tokenVersion을 비교한다.
- 값이 다르면 인증하지 않는다.

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
