package com.example.infrastructure;

import com.example.domain.order.Order;
import com.example.domain.order.OrderId;
import com.example.domain.order.OrderRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** 포트의 어댑터(구현)는 infrastructure 에. 도메인은 이 클래스를 모른다(의존 방향=안쪽). */
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<OrderId, Order> store = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        store.put(order.id(), order);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(store.get(id));
    }
}
