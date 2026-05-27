package com.example.domain.cart;

// Cart 애그리거트의 내부 엔티티. 다른 애그리거트가 직접 참조하면 경계 위반.
import com.example.shared.ddd.AggregateInternal;

@AggregateInternal
public class CartItem {
    private final String sku;

    public CartItem(String sku) { this.sku = sku; }

    public String sku() { return sku; }
}
