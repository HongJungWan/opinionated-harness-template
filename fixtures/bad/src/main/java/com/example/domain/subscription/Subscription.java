package com.example.domain.subscription;

// 위반 샘플(ID 참조, 경고): 다른 애그리거트 루트(Customer)를 객체로 직접 참조 → ID로 참조해야 함.
import com.example.domain.customer.Customer;
import com.example.shared.ddd.AggregateRoot;

@AggregateRoot
public class Subscription {
    private Long id;
    private Customer customer; // 위반: CustomerId 로 참조해야 함

    protected Subscription() {}

    public Subscription(Long id, Customer customer) {
        this.id = id;
        this.customer = customer;
    }

    public void changeOwner(Customer customer) {
        this.customer = customer;
    }
}
