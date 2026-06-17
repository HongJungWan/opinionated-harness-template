package com.example.archunit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

/** 룰이 실제로 위반을 잡는지 증명(의도적 위반 샘플 com.example.baddomain 대상). */
class DddRulesNegativeTest {
    private final JavaClasses bad = new ClassFileImporter().importPackages("com.example.baddomain");
    private final JavaClasses goodJpa = new ClassFileImporter().importPackages("com.example.goodjpa");

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

    @Test
    void noSpringInDomain_catchesStereotype() {
        assertTrue(DddRules.NO_SPRING_STEREOTYPES_IN_DOMAIN.evaluate(bad).hasViolation(), "도메인의 스프링 스테레오타입을 잡아야 함");
    }

    @Test
    void domainNoPublicSetter_catchesSetter() {
        assertTrue(DddRules.DOMAIN_NO_PUBLIC_SETTER.evaluate(bad).hasViolation(), "도메인 public setter 를 잡아야 함");
    }

    @Test
    void domainNoNondeterministicApi_catchesTimeNow() {
        assertTrue(DddRules.DOMAIN_NO_NONDETERMINISTIC_API.evaluate(bad).hasViolation(), "도메인의 시간/난수 직접 호출을 잡아야 함");
    }

    @Test
    void domainServiceStateless_catchesMutableField() {
        assertTrue(DddRules.DOMAIN_SERVICE_STATELESS.evaluate(bad).hasViolation(), "가변 필드를 가진 도메인 서비스를 잡아야 함");
    }

    @Test
    void aggregateIdFieldIsTyped_catchesRawForeignId() {
        assertTrue(DddRules.AGGREGATE_ID_FIELD_IS_TYPED.evaluate(bad).hasViolation(), "원시 타입 참조 식별자(*Id) 필드를 잡아야 함");
    }

    @Test
    void aggregateIdFieldIsTyped_allowsJpaSurrogateId() {
        assertFalse(DddRules.AGGREGATE_ID_FIELD_IS_TYPED.evaluate(goodJpa).hasViolation(),
                "JPA 자체 surrogate 키(@Id Long id)는 면제되어야 함");
    }

    @Test
    void noAutowiredInDomain_catchesConstructorInjection() {
        assertTrue(DddRules.NO_AUTOWIRED_IN_DOMAIN.evaluate(bad).hasViolation(), "도메인 생성자 자동주입 애너테이션을 잡아야 함");
    }

    @Test
    void aggregateNoExposedCollection_catchesRawListReturn() {
        assertTrue(DddRules.AGGREGATE_NO_EXPOSED_MUTABLE_COLLECTION.evaluate(bad).hasViolation(), "AR 의 날것 컬렉션 반환을 잡아야 함");
    }

    @Test
    void commandIsImmutable_catchesMutableCommand() {
        assertTrue(DddRules.COMMAND_IS_IMMUTABLE.evaluate(bad).hasViolation(), "가변 Command 를 잡아야 함");
    }
}
