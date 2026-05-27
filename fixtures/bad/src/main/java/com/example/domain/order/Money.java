package com.example.domain.order;

// 위반 샘플(VO 불변성): @ValueObject 인데 가변(setter) → 불변이어야 함(record 권장).
import com.example.shared.ddd.ValueObject;

@ValueObject
public class Money {
    private long amount;

    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; } // 위반: VO 가변
}
