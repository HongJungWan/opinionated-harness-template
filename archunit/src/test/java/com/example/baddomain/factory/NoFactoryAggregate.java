package com.example.baddomain.factory;

import com.example.shared.ddd.AggregateRoot;

// 위반: @AggregateRoot 인데 public static 팩토리가 없음 (AGGREGATE_ROOT_HAS_FACTORY).
@AggregateRoot
public class NoFactoryAggregate {
}
