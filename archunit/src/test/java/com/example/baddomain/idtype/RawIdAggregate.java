package com.example.baddomain.idtype;

import com.example.shared.ddd.AggregateRoot;

// 위반: 애그리거트 식별자 필드가 원시 타입(String) (AGGREGATE_ID_FIELD_IS_TYPED).
@AggregateRoot
public class RawIdAggregate {
    private final String orderId;
    private RawIdAggregate(String orderId) { this.orderId = orderId; }
    public static RawIdAggregate of(String orderId) { return new RawIdAggregate(orderId); }
    public String orderId() { return orderId; }
}
