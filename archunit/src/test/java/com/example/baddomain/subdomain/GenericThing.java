package com.example.baddomain.subdomain;

import com.example.shared.ddd.Subdomain;
import com.example.shared.ddd.SubdomainType;

// GENERIC 서브도메인 — CORE 가 의존하면 안 되는 대상.
@Subdomain(SubdomainType.GENERIC)
public class GenericThing {
    public String value() {
        return "generic";
    }
}
