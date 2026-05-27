package com.example.baddomain.other;

// 위반: 다른 애그리거트의 @AggregateInternal(SecretItem)을 직접 참조 (AGGREGATE_ACCESS).
import com.example.baddomain.cart.SecretItem;

public class Intruder {
    public String peek(SecretItem item) { return item.sku(); }
}
