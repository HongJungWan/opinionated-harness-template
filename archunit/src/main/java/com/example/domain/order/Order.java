package com.example.domain.order;

import com.example.shared.ddd.AggregateRoot;
import java.util.ArrayList;
import java.util.List;

/** 애그리거트 루트 — 내부 엔티티(OrderLine)는 루트를 통해서만 생성/수정. */
@AggregateRoot
public class Order {
    private final OrderId id;
    private final List<OrderLine> lines = new ArrayList<>();

    public Order(OrderId id) {
        this.id = id;
    }

    public void addLine(String sku, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        lines.add(new OrderLine(sku, quantity));
    }

    public int totalQuantity() {
        return lines.stream().mapToInt(OrderLine::quantity).sum();
    }

    public OrderId id() {
        return id;
    }
}
