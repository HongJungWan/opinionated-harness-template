package com.example.domain.event;

// 위반 샘플(룰 C): 도메인이 ApplicationEventPublisher 를 직접 import.
import org.springframework.context.ApplicationEventPublisher;

public class OrderShipped {

    private final ApplicationEventPublisher publisher;

    public OrderShipped(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void fire() {
        publisher.publishEvent(this);
    }
}
