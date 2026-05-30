# ArchUnit 게이트 (CI 정밀 강제)

훅이 보지 못하는 *여러 파일에 걸친 구조 위반*을 컴파일된 클래스 그래프 전체로 잡아내는 CI 게이트예요. 훅이 빠른 1차 강제(휴리스틱·로컬)라면, ArchUnit은 오탐 없이 권위 있는 최종 게이트(정확·CI) 역할을 맡아요.

<br>

## ArchUnit이 잡는 규칙 6가지

규칙 정의는 `archunit/src/test/java/com/example/archunit/DddRules.java` 한 파일에 모여 있어요.

| 코드 | 잡는 것 | 원칙 # |
|---|---|---|
| `DDD_DOMAIN_PURITY` | 도메인이 application · infrastructure · presentation에 의존하면 차단 | #3 |
| `DDD_DIP` | `*RepositoryImpl`이 infrastructure가 아닌 곳에 있으면 차단 | #4 |
| `DDD_AGGREGATE_ACCESS` | `@AggregateInternal`을 같은 애그리거트(패키지) 밖에서 접근하면 차단 | #9·#12 |
| `DDD_ID_REFERENCE` | 애그리거트 루트 필드가 다른 애그리거트 루트를 객체로 직접 참조하면 차단 (ID로 바꾸라는 신호) | #13 |
| `DDD_VO_IMMUTABLE` | `@ValueObject`의 필드가 `final`이 아니면 차단 | #16 |
| `DDD_NO_FIELD_INJECTION` | 필드 주입(`@Autowired` 필드)을 발견하면 차단 | — |

마커 어노테이션은 `ddd-markers/`에 있어요. 훅과 ArchUnit이 같은 마커를 봐서, 도메인 모델 한 곳에 표시해두면 양쪽이 함께 인식해요.

<br>

## 이 저장소에서 직접 돌려보기

```bash
cd archunit && ./gradlew test
```

두 테스트가 함께 돌아요.

- `DddArchitectureTest`는 깨끗한 헥사고날 샘플(`src/main`)에 6가지 규칙을 적용해 GREEN인지 확인해요.
- `DddRulesNegativeTest`는 의도적으로 규칙을 위반한 샘플(`src/test/.../baddomain`)을 룰이 정말 잡아내는지 역검증해요. 게이트 자체가 멍하게 통과하지 않는다는 걸 보장하는 장치예요.

<br>

## 다른 Spring/Gradle 프로젝트에 가져다 쓰기

1. `ddd-markers/`의 마커 클래스를 프로젝트 패키지로 복사해요. 공용 모듈로 떼어둬도 좋아요.
2. `DddRules.java`와 `DddArchitectureTest.java`를 `src/test/java`에 복사한 뒤, `@AnalyzeClasses(packages="<your.base>")`로 베이스 패키지를 지정해요.
3. 빌드 스크립트에 의존성을 추가해요: `testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")`.
4. CI에 `.github/workflows/ddd-archunit.yml`의 archunit 잡을 추가하고, 브랜치 보호 설정에서 required check로 묶어요.

<br>

## 기존 코드에 위반이 많을 땐 (baseline 래칫)

이미 만들어진 코드에서 위반이 잔뜩 나올 수 있어요. 그럴 땐 `FreezingArchRule.freeze(...)`로 기존 위반을 동결하고 *새로 생기는 위반만* 차단하면 돼요.

```java
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;
@ArchTest static final ArchRule domainPurity = freeze(DddRules.DOMAIN_PURITY);
```

최초 1회 실행으로 `archunit_store/` baseline이 만들어져요. 이 디렉토리를 커밋해 두면, 이후엔 새 위반만 RED로 잡혀요. CI 환경에선 store 자동 생성을 꺼두세요.
