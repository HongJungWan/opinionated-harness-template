package com.example.archunit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

/** 룰이 실제로 위반을 잡는지 증명(의도적 위반 샘플 com.example.baddomain 대상). */
class DddRulesNegativeTest {
    private final JavaClasses bad = new ClassFileImporter().importPackages("com.example.baddomain");

    @Test
    void domainPurity_catchesDomainToInfra() {
        assertTrue(DddRules.DOMAIN_PURITY.evaluate(bad).hasViolation(), "도메인→infra 의존을 잡아야 함");
    }

    @Test
    void aggregateAccess_catchesCrossAggregateInternalAccess() {
        assertTrue(DddRules.AGGREGATE_ACCESS.evaluate(bad).hasViolation(), "애그리거트 경계 침범을 잡아야 함");
    }

    @Test
    void idReference_catchesDirectAggregateRootReference() {
        assertTrue(DddRules.ID_REFERENCE_BETWEEN_AGGREGATES.evaluate(bad).hasViolation(), "AR 직접 참조를 잡아야 함");
    }

    @Test
    void aggregateRootHasFactory_catchesMissingFactory() {
        assertTrue(DddRules.AGGREGATE_ROOT_HAS_FACTORY.evaluate(bad).hasViolation(), "팩토리 없는 AR 을 잡아야 함");
    }

    @Test
    void coreNotDependOnGeneric_catchesCoreToGenericDependency() {
        assertTrue(DddRules.CORE_NOT_DEPEND_ON_GENERIC.evaluate(bad).hasViolation(), "CORE→GENERIC 의존을 잡아야 함");
    }

    @Test
    void requestInputIsCommand_catchesRequestNamedInput() {
        assertTrue(DddRules.REQUEST_INPUT_IS_COMMAND.evaluate(bad).hasViolation(), "application 의 *Request 명명을 잡아야 함");
    }

    @Test
    void domainEntityMarked_catchesUnmarkedEntity() {
        assertTrue(DddRules.DOMAIN_ENTITY_MARKED.evaluate(bad).hasViolation(), "표시 없는 도메인 @Entity 를 잡아야 함");
    }
}
