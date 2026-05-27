package com.example.domain.customer;

import com.example.shared.ddd.AggregateRoot;

@AggregateRoot
public class Customer {
    private Long id;
    protected Customer() {}
    public Customer(Long id) { this.id = id; }
    public Long id() { return id; }
}
