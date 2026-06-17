package com.example.baddomain.service;

import com.example.shared.ddd.DomainService;

// 위반: @DomainService 인데 가변(비-final) 인스턴스 필드 보유 (DOMAIN_SERVICE_STATELESS).
@DomainService
public class StatefulDomainService {
    private int counter;
    public int next() { return ++counter; }
}
