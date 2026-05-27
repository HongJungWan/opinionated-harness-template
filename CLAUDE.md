# CLAUDE.md

> Java/Spring 프로젝트용 하네스 템플릿. 스택/버전은 프로젝트에 맞게 조정하세요.
> DDD 세부 원칙은 `ddd-guidelines` 스킬을 로드해 참조하고, 레이어 경계·필드주입은 훅이 강제합니다.

## 1. 카파시 기반 핵심 행동 원칙 (Karpathy's Principles)
- **점진적 개발:** 변경은 컴파일·테스트 가능한 최소 단위로 쪼개 반영하고 즉시 커밋하라.
- **명확한 제약:** 모호한 해석 대신 아래 규칙과 스택을 그대로 따르라. 임의 확장 금지.
- **철저한 디버깅:** 에러 시 코드를 추측 수정하지 말고, 스택트레이스·로그를 처음부터 끝까지 정독하라.
- **실행 기반 검증:** 코드가 '맞아 보인다'고 믿지 말고 반드시 `./gradlew test` 등으로 실행 검증하라.

## 2. 기술 스택 (Tech Stack)
- Java 21 (LTS) · Spring Boot 3.5.x · Gradle (wrapper) · PostgreSQL · Flyway · QueryDSL

## 3. 개발 및 코딩 제약 규칙 (Strict Rules) — 훅이 결정론적으로 강제
- 기존 Flyway 마이그레이션(`V#__*.sql`) 수정/삭제 금지. 새 버전을 추가하라. (PreToolUse)
- 빌드/실행은 `./gradlew` 만 사용. 전역 `gradle`/`mvn` 금지. (PreToolUse·Bash)
- 도메인 레이어는 infra/adapter/application 어댑터를 import 하지 말 것(포트+DIP). (PostToolUse)
- 필드 주입(`@Autowired` 필드) 금지 — 생성자 주입만. (PostToolUse)
- 빈약한 도메인 모델 금지: 행위·불변식을 엔티티/VO 안에 캡슐화하라.
- "If X, then Y" 식 조건 분기 규칙 금지. AI 잡파일·비대한 보일러플레이트 금지.

## 4. 실행 및 테스트 명령어 (Commands)
- 전체 빌드(+테스트): `./gradlew clean build`
- 빠른 단위 테스트: `./gradlew test`  ·  인수 테스트(@Tag("slow")): `./gradlew slowTest`
- 로컬 실행: `./gradlew bootRun`

## 5. 하네스 레버 (이 저장소 구성)
- **Skills**(`.claude/skills/`): 필요 시 로드 — `ddd-guidelines` · `db-migration`(Flyway) · `api-generator`(REST) · `jpa-persistence`.
- **Subagents**(`.claude/agents/ddd-reviewer.md`): 전체 DDD 감사 등 헤비 태스크는 위임 후 결론만 회수.
- **Hooks**(`.claude/hooks/`): 작성 코드를 자동 검증(§3). 규칙 수정은 `harness.config.json` 한 곳.
- **완료 직전 자가 점검**(Stop 훅): 요구사항 대조·실행 검증·DDD 준수를 1회 점검한 뒤 종료한다.
