package com.example.domain.order;

// 위반 샘플(룰 I): 도메인이 LocalDateTime.now() 직접 호출(테스트 가능성/결정론 위배).
import java.time.LocalDateTime;

public class TimeBomb {

    private final LocalDateTime createdAt;

    public TimeBomb() {
        this.createdAt = LocalDateTime.now();
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }
}
