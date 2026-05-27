package com.example.domain.order;

// 위반 샘플(DIP): 리포지토리 *구현체*가 도메인에 위치 → 도메인엔 인터페이스만 있어야 함.
public class OrderRepositoryImpl {
    public void save(Order order) {
        // 구현은 infrastructure 레이어로 이동해야 함
    }
}
