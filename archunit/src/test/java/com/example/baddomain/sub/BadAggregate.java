package com.example.baddomain.sub;

// 위반: 애그리거트 루트가 다른 애그리거트 루트(Cust)를 객체로 직접 참조 (ID_REFERENCE).
import com.example.baddomain.cust.Cust;
import com.example.shared.ddd.AggregateRoot;

@AggregateRoot
public class BadAggregate {
    private Cust customer; // CustId 로 참조해야 함
    public BadAggregate(Cust customer) { this.customer = customer; }
}
