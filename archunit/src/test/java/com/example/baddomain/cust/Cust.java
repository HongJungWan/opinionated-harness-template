package com.example.baddomain.cust;

import com.example.shared.ddd.AggregateRoot;

@AggregateRoot
public class Cust {
    private Long id;
    public Cust(Long id) { this.id = id; }
}
