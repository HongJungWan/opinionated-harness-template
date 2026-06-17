package com.example.goodjpa;

import com.example.shared.ddd.AggregateRoot;
import jakarta.persistence.Id;

// 통과: JPA 자체 surrogate 키(@Id Long id)는 Typed-ID 규칙에서 면제(실용 레이어드).
@AggregateRoot
public class JpaOrder {
    @Id
    private Long id;

    protected JpaOrder() {}

    public static JpaOrder create() { return new JpaOrder(); }

    public Long id() { return id; }
}
