# opinionated-harness-template (Java/Spring)

AI 에이전트가 생성하는 Java/Spring 코드에 DDD 아키텍처 규칙을 강제하는 가드레일 템플릿.

**로컬 파일 훅** + **CI 단계 ArchUnit** 을 공용 마커 어노테이션으로 연동해 아키텍처 오염 자동 차단.

<br>


## 1. 해결하려는 문제

AI 코드 생성 시 발생하는 아키텍처 부패(도메인의 인프라 직접 참조, 캡슐화 파괴, DIP 위반)를 **코드 리뷰 전** 차단.

- **실시간 피드백 루프** — AI 코드 작성 즉시 훅이 위반 감지·에러 반환 → 컨텍스트 유지한 채 자가 수정, 사람 리뷰 공수 절감
- **우회·우발 파괴 차단** — 컴파일된 클래스 의존성 그래프 전체 검사(ArchUnit)로 훅 우회·다중 클래스 구조 꼬임까지 포착
- **낮은 오탐률** — 정적 분석으로 확실히 판정 가능한 규칙만 검사. 맥락 해석이 필요한 영역은 리뷰 전용 서브에이전트에 위임

<br>


## 2. 검사 규칙 및 정책

로컬 훅 2단계 동작.

- **차단(Block)** — `exit 2`, 작업 중단 + 피드백 반환
- **경고(Warn)** — 메시지만 남기고 통과(`exit 0`). 정규식 휴리스틱이라 오탐 고려해 기본 비차단

<br>

| 분류 | 대상 규칙 | DDD 원칙 |
| :--- | :--- | :--- |
| 🔒 **훅 차단** | 도메인→외부 레이어 참조 · 필드 주입(`@Autowired`) · 캡슐화 파괴(`@Setter`/`@Data`) · DIP 위반 · VO/이벤트 가변 | #3·#4·#11·#16·#18·#20 |
| ⚠️ **훅 경고** | 애그리거트 경계 외부 참조 · AR 객체 직접 참조 · 빈약한 도메인 모델 · 도메인 서비스 상태 보유 · 정적 팩토리 부재 · 이벤트 비-과거형 명명 | #9·#10·#11·#13·#15·#20·#22 |
| 🚧 **사전 차단** | 마이그레이션 파일(`V#__*.sql`) 수정/삭제 · 전역 `mvn`/`gradle`(Wrapper 강제) | — |
| ✅ **ArchUnit** | 레이어드 의존성 · DIP · 애그리거트 접근 · ID 참조 · VO 불변 (전체 클래스 그래프) | #3·#4·#9·#12·#13·#16 |
| 📝 **리뷰 위임** | 바운디드 컨텍스트 분리 · 서브도메인 분류 · 컨텍스트 맵/ACL · 응용 로직 침투 · 단일 트랜잭션-단일 AR · 보편 언어 · Tell, Don't Ask | #1·#2·#5·#6·#7·#8·#14·#17·#19·#21 |

<br>

> 💡 **설계 방침 — 실용적 레이어드**
>
> 도메인 엔티티의 JPA(`@Entity`)·`@Getter`·Lombok 허용, 가변 노출(`@Setter`/`@Data`)만 차단.
> 순수 헥사고날 격리 → `harness.config.json` 의 `forbiddenImports.domain` 조정.

<br>


## 3. 퀵 스타트

**① 적용** — 대상 프로젝트에 핵심 파일 복사

```bash
cp -r .claude <your-project>/ && cp CLAUDE.md <your-project>/
```

**② 설정** — `.claude/hooks/harness.config.json` 수정

- `layers` — 패키지 구조 글롭 (예: `**/domain/**`)
- `forbiddenImports.domain` — 도메인에서 참조 금지할 패키지/타입
- `protectedPaths` — 보호할 마이그레이션 경로

> ⚠️ 애그리거트·VO·이벤트 규칙 활성화 → 도메인 클래스에 `@AggregateRoot` 등 마커 부착 필수

**③ 검증·통합**

- 훅 자체 검증 — `./scripts/verify-harness.sh`
- ArchUnit 통합 — 독립 Gradle 모듈 `archunit/` 을 대상 CI 에 연결 → [`docs/ARCHUNIT.md`](docs/ARCHUNIT.md)

<br>


## 4. 프로젝트 구조

```
├── CLAUDE.md                    # 에이전트 시스템 프롬프트
├── .claude/                     # [대상 프로젝트 복사 대상]
│   ├── settings.json            # 이벤트별 훅 매핑
│   ├── hooks/
│   │   ├── harness.mjs          # 훅 디스패처 (Node.js, 의존성 0)
│   │   └── harness.config.json  # 프로젝트별 룰셋 데이터
│   ├── skills/                  # 도메인 가이드·코드 생성 스킬
│   └── agents/ddd-reviewer.md   # 정적 분석 불가 영역 심사 서브에이전트
├── ddd-markers/                 # 훅·ArchUnit 공용 마커 어노테이션
└── archunit/                    # CI 검증용 ArchUnit 룰 + Good/Bad 샘플 모듈
```

<br>


## 5. 동작 원리

### 로컬 훅 (Claude Code)

에이전트 도구 호출 이벤트 가로채 검증.

- **PreToolUse** (`protect`/`bash`) — 수정 전 보호 경로 검증 · `mvn`/전역 `gradle` 사전 차단
- **PostToolUse** (`guard`) — 디스크 작성 직후 검증, 위반 시 `exit 2` → 다음 턴 자가 수정 강제 (import 타입 소스까지 추적해 마커 확인)
- **Stop** (`checklist`) — 완료 직전 최종 자가 점검 요구

### CI 검증 (ArchUnit)

- `@AnalyzeClasses` — 컴파일된 바이트코드 읽어 규칙 평가
- 애그리거트 참조 오염 등 정규식 난점 — 커스텀 룰 검증
- `DddRulesNegativeTest` — 위반 샘플로 규칙 자체 역검증

<br>


## 6. 제약 사항

- **시점 한계** — `guard` 는 작성 후(PostToolUse) 동작 → 최초 작성 물리 차단 불가, 다음 턴 유도. 사람 IDE 직접 수정엔 미작동 → CI ArchUnit 필수
- **정규식 기반** — 경고 규칙은 AST 아닌 휴리스틱 → 오탐·누락 가능
- **분석 범위** — import 추적은 소스 파일 기준, 생성 코드·외부 jar·타 모듈 타입 미추적
- **명령어 오탐** — `bash` 가드 문자열 매칭, `echo "gradle"` 같은 텍스트도 차단

<br>


**요구사항** — Node.js(훅 실행) · JDK 21+(ArchUnit 모듈)
