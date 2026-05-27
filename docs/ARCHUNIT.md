# ArchUnit 게이트 (CI 정밀 강제)

훅(로컬, 정규식·휴리스틱)이 못 잡는 **cross-file 구조 규칙**을 전체 클래스 그래프로 *정밀* 강제한다.
훅과 역할 분담: **훅 = 빠른 로컬 피드백(경고 허용)**, **ArchUnit = 권위 있는 CI 게이트(오탐 없음)**.

## 강제 규칙 (`archunit/.../DddRules.java`)
| 코드 | 규칙 | PRD |
|---|---|---|
| `DDD_DOMAIN_PURITY` | 도메인 → application/infrastructure/presentation 의존 금지 | #3 |
| `DDD_DIP` | `*RepositoryImpl` 은 infrastructure 에 | #4 |
| `DDD_AGGREGATE_ACCESS` | `@AggregateInternal` 은 같은 패키지(애그리거트) 안에서만 접근 | #9·#12 |
| `DDD_ID_REFERENCE` | 애그리거트 루트 필드가 다른 AR 을 객체로 직접 참조 금지(ID로) | #13 |
| `DDD_VO_IMMUTABLE` | `@ValueObject` 필드는 final | #16 |
| `DDD_NO_FIELD_INJECTION` | 필드 주입 금지 | — |

마커 어노테이션은 `ddd-markers/`(훅과 공용). 애그리거트/VO/이벤트에 `@AggregateRoot`·`@AggregateInternal`·
`@ValueObject`·`@DomainEvent` 를 표시하면 규칙이 동작한다.

## 이 저장소에서 검증
```bash
cd archunit && ./gradlew test
```
- `DddArchitectureTest`(6규칙) → 깨끗한 헥사고날 샘플(`src/main`)에서 **GREEN**.
- `DddRulesNegativeTest`(3건) → 의도적 위반 샘플(`src/test/.../baddomain`)을 룰이 **잡는지** 단언.

## 타 Spring/Gradle 프로젝트에 드롭인
1. `ddd-markers/`의 마커를 프로젝트 패키지로 복사(또는 공유 모듈로).
2. `DddRules.java` + `DddArchitectureTest.java` 를 `src/test/java` 에 복사하고 `@AnalyzeClasses(packages="<your.base>")` 로 베이스 패키지 지정.
3. 의존성 추가: `testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")`.
4. CI 에 `.github/workflows/ddd-archunit.yml` 의 archunit 잡을 추가, branch protection 에서 required check 로.

## 레거시 온보딩 (baseline 래칫)
기존 코드 위반이 많으면 규칙을 `FreezingArchRule.freeze(...)` 로 감싸 동결하고 *새* 위반만 차단한다:
```java
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;
@ArchTest static final ArchRule domainPurity = freeze(DddRules.DOMAIN_PURITY);
```
최초 1회 실행으로 `archunit_store/` baseline 생성·커밋 → 이후 신규 위반만 RED. (CI 는 store 생성 비활성.)
