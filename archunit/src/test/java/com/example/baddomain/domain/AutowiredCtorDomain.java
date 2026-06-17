package com.example.baddomain.domain;

import org.springframework.beans.factory.annotation.Autowired;

// 위반: 도메인 생성자에 @Autowired (NO_AUTOWIRED_IN_DOMAIN).
public class AutowiredCtorDomain {
    private final Object dep;

    @Autowired
    public AutowiredCtorDomain(Object dep) {
        this.dep = dep;
    }

    public Object dep() { return dep; }
}
