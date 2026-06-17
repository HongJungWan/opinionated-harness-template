package com.example.baddomain.leak;

import com.example.shared.ddd.AggregateRoot;
import java.util.ArrayList;
import java.util.List;

// 위반: AR 이 내부 컬렉션을 날것(List)으로 노출 (AGGREGATE_NO_EXPOSED_MUTABLE_COLLECTION).
@AggregateRoot
public class ExposingAggregate {
    private final List<String> items = new ArrayList<>();

    public static ExposingAggregate create() { return new ExposingAggregate(); }

    public List<String> items() { return items; }
}
