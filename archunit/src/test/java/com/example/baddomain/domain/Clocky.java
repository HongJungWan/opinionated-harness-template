package com.example.baddomain.domain;

import java.time.LocalDateTime;

// 위반: 도메인에서 비결정적 시간 API 직접 호출 (DOMAIN_NO_NONDETERMINISTIC_API).
public class Clocky {
    public LocalDateTime stamp() { return LocalDateTime.now(); }
}
