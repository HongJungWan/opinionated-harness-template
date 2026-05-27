# 하네스 엔지니어링 가이드 (Java/Spring)

AI 에이전트(Claude Code)를 결정론적으로 제어하는 5대 레버. 핵심은 **훅이 에이전트가 작성한
Java/Spring 코드를 DDD 원칙에 맞게 강제**하는 것이다. (대상: Java 21 / Spring Boot / Gradle / Flyway)

## 5대 레버

| 레버 | 위치 | 역할 |
|---|---|---|
| 시스템 프롬프트 | `CLAUDE.md` (≤60줄) | 세션 시작 시 주입되는 최상위 제약(카파시 4원칙 + 스택 + 규칙 + 명령) |
| 스킬(점진적 공개) | `.claude/skills/*/SKILL.md` | 도메인 지식을 모듈화해 *필요할 때만* 로드 → 컨텍스트 청결 |
| 서브에이전트(컨텍스트 방화벽) | `.claude/agents/*.md` | 헤비 태스크를 단기 에이전트에 위임, 결론만 회수 |
| **훅(결정론적 체크포인트)** | `.claude/hooks/` | **작성 코드를 자동 검증. 가장 큰 성능 레버.** |

## 훅 동작

| 시점 | 훅 | 동작 |
|---|---|---|
| 파일 수정 직전 | `protect` | 보호 경로(Flyway 마이그레이션) 수정/삭제 차단 |
| Bash 실행 직전 | `bash` | `mvn`/전역 `gradle` 차단 → `./gradlew` 강제 |
| **파일 수정 직후** | **`guard`** | **완성 파일 + 참조 타입(import-follow)을 읽어 검사. 🔒차단(exit2): 도메인 import 순수성·필드주입·캡슐화(@Setter/@Data/setter)·DIP(*RepositoryImpl, *Repository=interface)·VO/이벤트 불변성. ⚠️경고(exit0): 애그리거트 경계·ID참조·빈약모델·최소애그리거트·도메인서비스 무상태·팩토리·이벤트 과거형** |
| 완료 선언 직전 | `checklist` | 요구사항 대조 자가 점검 1회 강제 |

> **마커**: 애그리거트/VO/이벤트에 `ddd-markers/`의 `@AggregateRoot`·`@AggregateInternal`·`@ValueObject`·
> `@DomainEvent`·`@DomainService` 를 표시하면 훅(이름 기반)과 ArchUnit(클래스 기반)이 함께 인식한다.

위반 시 훅은 `exit 2 + stderr` 로 신호한다. Claude Code 는 그 stderr 를 에이전트에게 보여주고,
에이전트는 그 자리에서 코드를 고친다. 모든 로직은 `harness.mjs`(Node, 의존성 0) 하나에 있다.
Node 만 있으면 되고 빌드 도구(Gradle)와 무관하게 작동한다.

> **왜 PostToolUse 인가?** PreToolUse 는 Edit 의 조각(new_string)만 보여 import 전체를 분석할 수 없다.
> DDD 가드는 수정이 끝난 *완성 파일*을 읽어야 정확하다. (마이그레이션/명령 차단은 입력만으로 판단되어 PreToolUse.)
>
> **훅 vs CI:** 훅은 로컬 *에이전트 루프*의 빠른 강제다. 사람·CI 까지 포함한 권위 있는 게이트는
> **ArchUnit**(JVM 아키텍처 테스트)으로 상호보완하는 것을 권장한다.

## 복사-붙여넣기 도입 (3단계)

```bash
# 1. 대상 프로젝트 루트에 두 가지를 복사
cp -r opinionated-harness-template/.claude  <your-project>/.claude
cp    opinionated-harness-template/CLAUDE.md <your-project>/CLAUDE.md
```
2. `.claude/hooks/harness.config.json` 에서 **이것만** 수정:
   - `layers` / `forbiddenImports` — 프로젝트 패키지 구조에 맞게(예: `**/domain/**`). 순서 주의: `presentation`(컨트롤러)을 `domain` 앞에 둬 도메인 판정에서 제외.
   - `forbiddenContentPatterns` — 필드 주입 등 콘텐츠 정규식 규칙.
   - `protectedPaths` — Flyway/Liquibase 마이그레이션 경로.
   - `forbiddenCommands` / `commandRule` / `stack` — 빌드 도구(Gradle/Maven).
   - `importPatterns` — 다른 JVM 언어(Kotlin 등) 추가 시.
3. `CLAUDE.md` 의 `<...>` 스택/버전 플레이스홀더 채움.

끝. 다음 Edit/Write 부터 훅이 자동으로 돈다. (검증: `./scripts/verify-harness.sh`)

## 2중 방어 + DDD 22원칙 커버리지

**훅(로컬, 빠름, 휴리스틱 허용) + ArchUnit(CI, 정밀, 오탐 없음)** 으로 이중 강제한다. 겹치는 구조 규칙은
훅에선 경고, ArchUnit에선 차단 → 오탐 마찰 없이 최대 커버리지. ArchUnit 상세는 [`ARCHUNIT.md`](ARCHUNIT.md).

| 강제 수준 | 원칙(PRD #) |
|---|---|
| 🔒 훅 차단 | 도메인 순수성(#3) · DIP(#4) · 캡슐화/setter(#11·#18) · VO/이벤트 불변(#16·#20) · 필드주입 |
| ⚠️ 훅 경고 | 애그리거트 경계(#9) · ID참조(#13) · 빈약모델(#11) · 최소애그리거트(#10) · 도메인서비스(#22) · 팩토리(#15) · BC격리(#1) · 이벤트명(#20) |
| ✅ ArchUnit 정밀 | 레이어드/순수성(#3) · DIP(#4) · 애그리거트 접근(#9·#12) · ID참조(#13) · VO 불변(#16) |
| 📝 강제 불가 → 스킬/리뷰 | 서브도메인 분류(#2) · 응용 로직 침투(#6·#7) · Command(#8) · 단일TX-단일AR(#14) · 보편언어 명명(#17) · Tell-Don't-Ask(#21) |

- 실용 레이어드(기본값): 도메인 엔티티의 JPA(`@Entity`)·`@Getter`·Lombok 은 *허용*(가변 노출 @Setter/@Data만 금지).
  순수 헥사고날은 `forbiddenImports.domain` 에 `jakarta.persistence`/`org.springframework`/`lombok` 추가(엄격 토글).
- 📝 ~6개는 의미/런타임 분석이 필요해 정적 강제 불가 — 정직하게 `ddd-guidelines`(예방) + `ddd-reviewer`(감사)
  + Stop 체크리스트로만 다룬다. 흉내내지 않는다(false confidence 방지).
- 🟡 경고 규칙은 오탐 가능 → 기본 비차단. `harness.config.json` 의 `checks` 로 severity(block/warn/off) 조정.
