package com.example.baddomain.domain;

import org.springframework.stereotype.Service;

// 위반: 도메인에 스프링 스테레오타입 (NO_SPRING_STEREOTYPES_IN_DOMAIN).
@Service
public class SpringyService {
    public void run() {}
}
