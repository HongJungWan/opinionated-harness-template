# opinionated-harness-template (Java/Spring)

DDD 원칙을 강제하는 AI 에이전트 하네스 엔지니어링 템플릿.
`CLAUDE.md` + `.claude/` 를 그대로 복사하면 어떤 Java/Spring 프로젝트든 즉시 하네스가 깔린다.

**핵심:** DDD 22원칙을 **2중 방어**로 강제한다 — ① **훅**(로컬, 즉시): 에이전트가 Edit/Write 하면
`exit 2 + stderr` 로 되먹여 그 자리에서 고치게 함. ② **ArchUnit**(CI, 정밀): cross-file 구조 규칙을
오탐 없이 차단하는 권위 게이트. (전체 커버리지 매트릭스: [`docs/HARNESS.md`](docs/HARNESS.md))

대상: Java 21 · Spring Boot 3.5.x · Gradle(`./gradlew`) · Flyway. 기본 정책 = **실용 레이어드**
(도메인 엔티티의 JPA·Lombok 허용, 도메인→infra/adapter/application 의존·필드 주입 금지).

## 훅이 강제하는 것

| 시점 | 훅 | 규칙 |
|---|---|---|
| 파일 수정 직전 | `protect` | Flyway 마이그레이션(`V#__*.sql`) 수정/삭제 차단 |
| Bash 실행 직전 | `bash` | `mvn`/전역 `gradle` 차단 → `./gradlew` 강제 |
| **파일 수정 직후** | **`guard`** | 🔒차단: import 순수성·필드주입·캡슐화·DIP·VO/이벤트 불변. ⚠️경고: 애그리거트 경계·ID참조·빈약모델·최소애그리거트·도메인서비스·팩토리(휴리스틱, import-follow) |
| 완료 선언 직전 | `checklist` | 요구사항 대조·실행 검증·DDD 점검 1회 강제 |

마커(`ddd-markers/`: `@AggregateRoot`/`@AggregateInternal`/`@ValueObject`/`@DomainEvent`/`@DomainService`)를
도메인에 표시하면 훅·ArchUnit 둘 다 인식한다.

## 구성 (5대 레버)

```
CLAUDE.md                       # 시스템 프롬프트(카파시 4원칙 + 스택 + 규칙 + 명령, ≤60줄)
.claude/
  settings.json                 # 훅 배선(커밋, 팀 공유)
  hooks/harness.mjs             # 단일 디스패처 guard|protect|bash|checklist (Node, 의존성 0)
  hooks/harness.config.json     # 프로젝트별로 이것만 수정 (layers/forbiddenImports/...)
  skills/                       # ddd-guidelines · db-migration(Flyway) · api-generator(REST) · jpa-persistence
  agents/ddd-reviewer.md        # 컨텍스트 방화벽 서브에이전트
ddd-markers/                    # DDD 마커 어노테이션(훅·ArchUnit 공용, 복사용)
archunit/                       # ArchUnit CI 게이트 드롭인(자립 Gradle 모듈) — docs/ARCHUNIT.md
.github/workflows/ddd-archunit.yml  # 훅 자가검증 + ArchUnit 게이트 CI
── 템플릿 개발/검증용 (복사 대상 아님) ──
fixtures/{clean,bad}/           # 훅 자가검증 샘플(Java)
scripts/verify-harness.sh       # 훅 차단/통과 e2e 단언 (현재 16/16 통과)
docs/HARNESS.md · docs/ARCHUNIT.md  # 가이드(커버리지 매트릭스 / ArchUnit 드롭인)
```

## 빠른 시작

```bash
# 1) 도입: 복사 단위 = CLAUDE.md + .claude/
cp -r .claude <your-project>/ && cp CLAUDE.md <your-project>/

# 2) .claude/hooks/harness.config.json 에서 layers/forbiddenImports/protectedPaths 만 조정

# 3) 검증(이 저장소에서): 훅이 실제로 차단/통과하는지
./scripts/verify-harness.sh
```

자세한 도입/커스터마이즈는 [`docs/HARNESS.md`](docs/HARNESS.md), DDD 원칙은
[`.claude/skills/ddd-guidelines/SKILL.md`](.claude/skills/ddd-guidelines/SKILL.md).

요구사항: `node` (훅 실행). npm 의존성 없음. CI 권위 게이트는 ArchUnit 으로 보완 권장.
