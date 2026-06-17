# ArchUnit 게이트 (CI 정밀 강제)

훅이 보지 못하는 *여러 파일에 걸친 구조 위반*을 컴파일된 클래스 그래프 전체로 잡아내는 CI 게이트예요. 훅이 빠른 1차 강제(휴리스틱·로컬)로 단일 파일 안의 도메인 순수성·DIP·캡슐화·값 객체 불변 같은 패턴을 잡는다면, ArchUnit은 오탐 없이 권위 있는 최종 게이트(정확·CI)로 클래스 그래프 단위 구조를 잡아요. 훅이 잡는 룰 전체 목록은 [`HARNESS.md`](HARNESS.md)에 정리돼 있어요.

<br>

## ArchUnit이 잡는 규칙 18가지

규칙 정의는 `archunit/src/test/java/com/example/archunit/DddRules.java` 한 파일에 모여 있어요.

| 코드 | 잡는 것 | 원칙 # |
|---|---|---|
| `DDD_DOMAIN_PURITY` | 도메인이 application · infrastructure · presentation에 의존하면 차단 | #3 |
| `DDD_DIP` | `*RepositoryImpl`이 infrastructure가 아닌 곳에 있으면 차단 | #4 |
| `DDD_AGGREGATE_ACCESS` | `@AggregateInternal`을 같은 애그리거트(패키지) 밖에서 접근하면 차단 | #9·#12 |
| `DDD_ID_REFERENCE` | 애그리거트 루트 필드가 다른 애그리거트 루트를 객체로 직접 참조하면 차단 (ID로 바꾸라는 신호) | #13 |
| `DDD_VO_IMMUTABLE` | `@ValueObject`의 필드가 `final`이 아니면 차단 | #16 |
| `DDD_NO_FIELD_INJECTION` | 필드 주입(`@Autowired` 필드)을 발견하면 차단 | — |
| `DDD_DOMAIN_ENTITY_MARKED` | 도메인 `@Entity`가 `@AggregateRoot`/`@AggregateInternal`로 표시되지 않으면 차단 (jakarta 비의존: 문자열 매칭) | — |
| `DDD_AGGREGATE_ROOT_HAS_FACTORY` | `@AggregateRoot`에 자기 타입을 반환하는 `public static` 팩토리가 없으면 차단 | — |
| `DDD_CORE_NOT_DEPEND_ON_GENERIC` | `@Subdomain(CORE)` 클래스가 `@Subdomain(GENERIC)` 클래스에 의존하면 차단 (전략적 의존 방향) | — |
| `DDD_REQUEST_INPUT_IS_COMMAND` | `..application..`의 입력이 `*Request`로 명명되면 차단 (Command 명명 권장) | — |
| `DDD_NO_SPRING_IN_DOMAIN` | 도메인에 스프링 스테레오타입(`@Service`·`@Component`·`@Repository`·`@Controller`·`@RestController`)/`@Transactional`이 붙으면 차단 (문자열 매칭, 스프링 비의존) | — |
| `DDD_NO_PUBLIC_SETTER` | 도메인의 public setter(`set[A-Z]*`)를 차단 — Lombok 생성 setter도 바이트코드로 포착 (빈약 모델 방지) | — |
| `DDD_NO_NONDETERMINISTIC_API` | 도메인에서 시간(`*.now()`·`System.currentTimeMillis/nanoTime`)·난수(`UUID.randomUUID()`·`new Random()`) 직접 호출을 차단 (호출자가 주입) | — |
| `DDD_DOMAIN_SERVICE_STATELESS` | `@DomainService`의 인스턴스 필드가 `final`이 아니면 차단 (무상태 유지) | — |
| `DDD_TYPED_ID` | `@AggregateRoot`/`@AggregateInternal`의 **다른 애그리거트 참조 식별자**(`customerId` 등 `*Id`)가 원시 타입이면 차단. **자체 surrogate 키**(`id` 또는 JPA `@Id`/`@EmbeddedId`)는 실용 레이어드 허용을 위해 면제 | — |
| `DDD_NO_AUTOWIRED_IN_DOMAIN` | 도메인 멤버(필드·생성자·메서드) 어디에든 `@Autowired`가 붙으면 차단 (필드주입 외 생성자/메서드 주입까지) | — |
| `DDD_NO_EXPOSED_COLLECTION` | `@AggregateRoot`의 public **인스턴스** 메서드가 내부 컬렉션을 `List`·`Set`·`Map`으로 반환하면 차단(정적 메서드 제외). `unmodifiableList` 같은 불변 래퍼라도 반환 타입이 raw 컬렉션이면 잡히니 `Stream`·`Iterable`·복사로 노출하세요 | — |
| `DDD_COMMAND_IMMUTABLE` | `..application..`의 `*Command`가 불변이 아니면(record 아님 + setter/비-final 필드) 차단 | — |

> 규칙 11~15는 [참고 아티클](https://day-t.tistory.com/85)이 "ArchUnit으로 강제한다"고 명시한 도메인 클린코드 규칙이에요(훅이 이미 잡지만 ArchUnit으로도 승격 → **Claude를 거치지 않은 직접 커밋까지 CI에서 막음**, 이중 강제). 규칙 16~18은 같은 아티클의 "@Autowired 금지·내부 컬렉션 비노출·불변 Command" 원칙을 추가로 ArchUnit화한 것이에요.

마커 어노테이션은 `ddd-markers/`에 있어요(`@AggregateRoot`·`@AggregateInternal`·`@ValueObject`·`@DomainService`·`@DomainEvent`·`@Subdomain`). 훅과 ArchUnit이 같은 마커를 봐서, 도메인 모델 한 곳에 표시해두면 양쪽이 함께 인식해요. `@Subdomain(CORE|SUPPORTING|GENERIC)`는 전략적 설계(서브도메인 분류)를 표시하며, `CORE→GENERIC` 의존을 ArchUnit이 막아요.

<br>

## 이 저장소에서 직접 돌려보기

```bash
cd archunit && ./gradlew test
```

두 테스트가 함께 돌아요.

- `DddArchitectureTest`는 깨끗한 헥사고날 샘플(`src/main`)에 18가지 규칙을 적용해 GREEN인지 확인해요.
- `DddRulesNegativeTest`는 의도적으로 규칙을 위반한 샘플(`src/test/.../baddomain`)을 룰이 정말 잡아내는지 역검증하고, 면제돼야 할 패턴(`goodjpa` — JPA surrogate ID는 Typed ID 규칙에서 제외)은 오탐 없이 통과하는지도 확인해요. 게이트 자체가 멍하게 통과하지 않는다는 걸 보장하는 장치예요.

> 참고: 훅은 `fixtures/bad/**`로 자가검증하고(로컬·휴리스틱·빠름), ArchUnit은 `archunit/src/test/.../baddomain/**`로 역검증해요(CI·정밀·오탐 0). 픽스처 트리가 둘로 나뉜 건 두 게이트의 검증 수준이 달라서예요.

<br>

## 다른 Spring/Gradle 프로젝트에 가져다 쓰기

1. `ddd-markers/`의 마커 클래스를 프로젝트 패키지로 복사해요. 공용 모듈로 떼어둬도 좋아요.
2. `DddRules.java`와 `DddArchitectureTest.java`를 `src/test/java`에 복사한 뒤, `@AnalyzeClasses(packages="<your.base>")`로 베이스 패키지를 지정해요.
3. 빌드 스크립트에 테스트 의존성을 추가해요:
   - `testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")`
   - `testImplementation("jakarta.persistence:jakarta.persistence-api:3.1.0")` — 도메인 `@Entity` 표시 규칙용
   - `testImplementation("org.springframework:spring-context:6.2.x")` — 스프링 스테레오타입/`@Autowired` 위반 픽스처 컴파일용(규칙 자체는 문자열 매칭이라 스프링 비의존)
4. `DddRulesNegativeTest.java`와 위반 픽스처(`src/test/.../baddomain/**`)·면제 픽스처(`goodjpa/**`)도 함께 복사해요. 빠뜨리면 규칙이 위반을 *정말* 잡는지 검증이 사라져 게이트가 잠든 채 통과할 수 있어요.
5. CI에 `.github/workflows/ddd-archunit.yml`의 archunit 잡을 추가하고, 브랜치 보호 설정에서 required check로 묶어요.

<br>

## 기존 코드에 위반이 많을 땐 (baseline 래칫)

이미 만들어진 코드에서 위반이 잔뜩 나올 수 있어요. 그럴 땐 `FreezingArchRule.freeze(...)`로 기존 위반을 동결하고 *새로 생기는 위반만* 차단하면 돼요.

```java
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;
@ArchTest static final ArchRule domainPurity = freeze(DddRules.DOMAIN_PURITY);
```

최초 1회 실행으로 `archunit_store/` baseline이 만들어져요. 이 디렉토리를 커밋해 두면, 이후엔 새 위반만 RED로 잡혀요. CI 환경에선 store 자동 생성을 꺼두세요.
