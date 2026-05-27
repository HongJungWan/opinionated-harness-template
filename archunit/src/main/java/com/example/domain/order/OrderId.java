package com.example.domain.order;

import com.example.shared.ddd.ValueObject;
import java.util.Objects;

/** VO — record 라 불변. */
@ValueObject
public record OrderId(String value) {
    public OrderId {
        Objects.requireNonNull(value, "value");
    }
}
