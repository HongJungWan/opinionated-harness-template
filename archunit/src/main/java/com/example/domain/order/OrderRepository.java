package com.example.domain.order;

import java.util.Optional;

/** 포트(인터페이스)는 도메인 소유, 구현은 infrastructure(DIP). */
public interface OrderRepository {
    void save(Order order);

    Optional<Order> findById(OrderId id);
}
