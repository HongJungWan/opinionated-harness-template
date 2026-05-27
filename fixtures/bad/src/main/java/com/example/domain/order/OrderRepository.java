package com.example.domain.order;

// 위반 샘플(DIP): 도메인의 *Repository 가 인터페이스가 아니라 클래스 → 도메인엔 인터페이스만.
public class OrderRepository {
    public void save(Order order) {
        // 구현은 infrastructure 로
    }
}
