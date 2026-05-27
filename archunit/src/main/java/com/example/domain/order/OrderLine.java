package com.example.domain.order;

import com.example.shared.ddd.AggregateInternal;

/** 애그리거트 내부 엔티티 — 같은 패키지(Order 애그리거트)에서만 접근. */
@AggregateInternal
public final class OrderLine {
    private final String sku;
    private final int quantity;

    OrderLine(String sku, int quantity) {
        this.sku = sku;
        this.quantity = quantity;
    }

    int quantity() {
        return quantity;
    }
}
