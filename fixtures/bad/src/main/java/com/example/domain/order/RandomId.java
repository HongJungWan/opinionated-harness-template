package com.example.domain.order;

// 위반 샘플(룰 J): 도메인이 UUID.randomUUID() 직접 호출(비결정론).
import java.util.UUID;

public class RandomId {

    private final UUID id;

    public RandomId() {
        this.id = UUID.randomUUID();
    }

    public UUID value() {
        return id;
    }
}
