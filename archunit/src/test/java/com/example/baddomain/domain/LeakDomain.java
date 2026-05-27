package com.example.baddomain.domain;

// 위반: 도메인이 infra 직접 의존 (DOMAIN_PURITY).
import com.example.baddomain.infra.LeakInfra;

public class LeakDomain {
    private final LeakInfra infra = new LeakInfra();
    public String use() { return infra.conn(); }
}
