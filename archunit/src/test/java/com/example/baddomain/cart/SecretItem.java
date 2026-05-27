package com.example.baddomain.cart;

import com.example.shared.ddd.AggregateInternal;

@AggregateInternal
public class SecretItem {
    public String sku() { return "x"; }
}
