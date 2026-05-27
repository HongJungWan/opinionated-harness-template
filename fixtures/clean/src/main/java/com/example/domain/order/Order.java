package com.example.domain.order;

// 통과 샘플(실용 레이어드 + 리치 모델): JPA/Lombok 허용, infra 의존·setter 없음, 행위 캡슐화.
import com.example.shared.ddd.AggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@AggregateRoot
public class Order {

    @Id
    private Long id;
    private int totalAmount;

    protected Order() {}

    public Order(Long id, int totalAmount) {
        if (totalAmount < 0) throw new IllegalArgumentException("totalAmount must be >= 0");
        this.id = id;
        this.totalAmount = totalAmount;
    }

    // 행위(상태 전이를 캡슐화) — 빈약 모델이 아님
    public void applyDiscount(int amount) {
        if (amount < 0 || amount > totalAmount) throw new IllegalArgumentException("invalid discount");
        this.totalAmount -= amount;
    }

    public int totalAmount() {
        return totalAmount;
    }
}
