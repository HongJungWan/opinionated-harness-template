package com.example.domain.order;

// 위반 샘플(룰 A): 도메인 클래스에 Spring 스테레오타입/@Transactional 부착.
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnnotatedOrder {

    private final String id;

    public AnnotatedOrder(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}
