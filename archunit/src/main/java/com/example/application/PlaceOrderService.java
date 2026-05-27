package com.example.application;

import com.example.domain.order.Order;
import com.example.domain.order.OrderId;
import com.example.domain.order.OrderRepository;

/** 응용 서비스 — 도메인 포트에만 의존, 생성자 주입, 흐름 제어만. */
public class PlaceOrderService {
    private final OrderRepository orders;

    public PlaceOrderService(OrderRepository orders) {
        this.orders = orders;
    }

    public OrderId place(String sku, int quantity) {
        Order order = new Order(new OrderId(java.util.UUID.randomUUID().toString()));
        order.addLine(sku, quantity);
        orders.save(order);
        return order.id();
    }
}
