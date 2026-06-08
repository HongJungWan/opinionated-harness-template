package com.example.baddomain.subdomain;

import com.example.shared.ddd.Subdomain;
import com.example.shared.ddd.SubdomainType;

// 위반: CORE 가 GENERIC 에 직접 의존 (CORE_NOT_DEPEND_ON_GENERIC).
@Subdomain(SubdomainType.CORE)
public class CoreThing {
    private final GenericThing generic = new GenericThing();

    public String use() {
        return generic.value();
    }
}
