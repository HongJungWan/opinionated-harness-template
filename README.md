# opinionated-harness-template

AI 에이전트가 짜는 Java/Spring 코드를 DDD 규칙에 맞게 잡아주는 가드레일 템플릿이에요.

변경 사항은 아래 릴리스 노트에서 확인할 수 있어요.

<br>
<br>

## 릴리스 노트

<br>

### v0.1.0

`2026년 5월 27일` · 첫 공개 릴리스

로컬 훅과 CI(ArchUnit) 두 곳에서 DDD 위반을 잡는 첫 버전을 공개했어요. `.claude/` 와 `CLAUDE.md` 를
프로젝트에 복사하고 설정 몇 줄만 고치면 바로 쓸 수 있어요.

<br>

**`새 기능`  로컬 훅 가드레일**

에이전트가 파일을 고치면 그 즉시 훅이 코드를 읽고 DDD 위반을 짚어줘요. 위반이면 에이전트에게 메시지를
돌려주고, 에이전트는 같은 흐름에서 스스로 고쳐요. 사람이 매번 리뷰로 잡지 않아도 돼요.

- 도메인이 인프라·외부 레이어를 참조하거나, 필드 주입(`@Autowired`)·`@Setter`/`@Data`로 캡슐화를 깨거나, 값 객체를 가변으로 두면 바로 짚어줘요.
- 애그리거트 경계 침범, 다른 애그리거트 직접 참조, 빈약한 모델 같은 건 경고로 알려줘요. 정규식 휴리스틱이라 오탐을 고려해 기본은 막지 않아요.
- 마이그레이션 파일(`V#__*.sql`) 수정과 전역 `mvn`/`gradle` 실행은 실행 전에 막아요(Wrapper 사용 강제).
- 작업을 끝내기 직전엔 자가 점검 체크리스트를 한 번 띄워줘요.

```mermaid
flowchart TD
    A["AI가 코드를 고치거나 명령을 실행"] --> B{"무엇을 하려는가"}
    B -->|"DB 마이그레이션 파일 수정"| C["실행 전에 막음"]
    B -->|"mvn·전역 gradle 실행"| C
    B -->|"그 외 코드 수정"| D["수정한 코드를 DDD 규칙으로 검사"]
    D -->|"꼭 고쳐야 할 위반"| E["바로 알려줌 → AI가 다시 고침"]
    D -->|"참고용 경고"| F["알려만 주고 막지는 않음"]
    D -->|"문제 없음"| G["통과"]
    E --> H["작업 끝내기 직전, 빠진 것 없는지 스스로 점검"]
    F --> H
    G --> H
```

<br>

**`새 기능`  ArchUnit CI 게이트**

CI에서 컴파일된 클래스 그래프 전체를 보고 구조 위반을 막아요. 레이어 의존성, DIP, 애그리거트 접근,
ID 참조, 값 객체 불변성을 검사해요. 여러 클래스에 걸친 구조 문제나 훅을 거치지 않은 변경은 여기서 걸려요.

규칙이 진짜로 위반을 잡는지까지 `DddRulesNegativeTest`로 역검증해 둬서, 게이트 자체를 믿고 쓸 수 있어요.

```mermaid
flowchart TD
    A["코드를 올림 (PR·main 푸시)"] --> B["CI가 자동으로 검사"]
    B --> C["프로젝트 코드 구조를 규칙으로 확인<br/>레이어·의존 방향·애그리거트·값 객체"]
    B --> D["그 규칙들이 위반을 진짜로 잡는지 함께 확인"]
    C --> E{"통과?"}
    D --> E
    E -->|"통과"| F["합치기 허용"]
    E -->|"위반"| G["합치기 차단"]
```

<br>

**`새 기능`  공용 마커 어노테이션**

`@AggregateRoot` · `@AggregateInternal` · `@ValueObject` · `@DomainEvent` · `@DomainService` 를 제공해요.
도메인 모델에 이 마커를 붙이면 훅과 ArchUnit이 같은 기준으로 애그리거트·값 객체·이벤트 규칙을 검사해요.

```java
// 주문(Order) = 하나의 애그리거트. 바깥에서는 항상 Order를 거쳐서만 다룬다.
@AggregateRoot
public class Order {

    private final OrderId id;                       // 주문 번호 (값 객체)
    private final List<OrderLine> lines = new ArrayList<>();

    public Order(OrderId id) {
        this.id = id;
    }

    // 상태를 바꾸는 통로는 이런 "의미 있는 메서드" 하나뿐.
    // setter로 아무 값이나 꽂는 걸 막아, '수량은 1개 이상' 같은 규칙을 항상 지키게 한다.
    public void addLine(String sku, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
        }
        lines.add(new OrderLine(sku, quantity));
    }
}

// 주문에 딸린 부품. Order를 통해서만 만들어지고, 바깥에서 직접 못 만든다.
@AggregateInternal
class OrderLine {                                   // public 아님 = 같은 묶음 안에서만 사용
    private final String sku;
    private final int quantity;

    OrderLine(String sku, int quantity) {
        this.sku = sku;
        this.quantity = quantity;
    }
}

// 값 그 자체. record라서 한번 만들면 바뀌지 않는다.
@ValueObject
public record OrderId(String value) {}

// "주문이 접수됐다" 같은 사건 기록. 과거형으로 이름 짓고, 만든 뒤엔 바뀌지 않는다.
@DomainEvent
public record OrderPlaced(OrderId orderId) {}

// 한 객체에 담기 애매한 계산 규칙을 모아두는 곳. 자기 상태 없이 입력으로만 결과를 낸다.
@DomainService
public class ShippingFeePolicy {
    public int feeFor(int totalQuantity) {          // 수량당 1,000원
        return totalQuantity * 1000;
    }
}
```

<br>

**`새 기능`  스킬과 리뷰 서브에이전트**

에이전트가 필요할 때 불러 쓰는 스킬 4종(`ddd-guidelines` · `db-migration` · `api-generator` ·
`jpa-persistence`)을 넣었어요. 자동 검사로 판정하기 어려운 영역은 `ddd-reviewer` 서브에이전트가 리뷰로 맡아요.

```mermaid
flowchart TD
    A["AI가 DDD 관련 작업"] --> B{"어떻게 챙기나"}
    B -->|"기계가 딱 잡아내는 규칙"| C["훅·ArchUnit이 자동 검사"]
    B -->|"이럴 땐 이렇게 짜라는 안내가 필요"| D["필요한 안내서(스킬)를 그때 꺼내 봄<br/>DDD 설계·DB 변경·API·영속성"]
    B -->|"사람 판단이 필요한 깊은 리뷰"| E["리뷰 전담 AI에게 맡기고 결과만 받음"]
```

<br>

**`정책`  실용적 레이어드**

도메인 엔티티의 JPA(`@Entity`)·`@Getter`·Lombok은 허용하고, 가변을 여는 `@Setter`/`@Data`만 막아요.
순수 헥사고날로 더 조이고 싶으면 `harness.config.json`의 `forbiddenImports.domain`만 손보면 돼요.

```mermaid
flowchart TD
    OUT["인프라 · 웹 · 응용 계층"] -->|"도메인을 가져다 씀"| DOM
    subgraph DOM["도메인 (비즈니스 규칙)"]
        OK["허용: JPA(@Entity)·@Getter·Lombok"]
        NO["금지: @Setter·@Data·public setter (밖에서 값 함부로 못 바꾸게)"]
    end
    NOTE["방향은 한쪽으로만 — 도메인은 인프라·웹 계층을 가져다 쓰지 않음<br/>더 순수하게 조이려면 설정 파일 한 곳만 수정"]
```

<br>

**`참고`  알아두면 좋은 점**

- 파일 수정 직후 도는 훅(`guard`)은 최초 작성 자체를 막지는 못해요. 위반을 돌려줘 다음 턴에 고치게 해요. 사람이 IDE로 직접 쓴 코드엔 훅이 안 도니, CI의 ArchUnit이 받쳐줘요.
- 경고 규칙은 정규식 기반이라 오탐·누락이 있을 수 있어요. 그래서 기본을 경고로 뒀어요.
- DDD 원칙 중 맥락 해석이 필요한 약 10개는 자동 검사 대신 리뷰 서브에이전트에 맡겨요. 무리하게 흉내내지 않았어요.
- ArchUnit은 대상 프로젝트의 빌드에 연결해야 게이트로 동작해요.

<br>
<br>

요구사항은 훅 실행에 Node.js, ArchUnit 모듈에 JDK 21 이상이에요.
쓰는 법은 [`docs/HARNESS.md`](docs/HARNESS.md), ArchUnit 연결은 [`docs/ARCHUNIT.md`](docs/ARCHUNIT.md)에 정리해 뒀어요.
