package com.example.baddomain.domain;

// 위반: 도메인 @Entity 인데 @AggregateRoot/@AggregateInternal 표시가 없음 (DOMAIN_ENTITY_MARKED).
@jakarta.persistence.Entity
public class UnmarkedEntity {
}
