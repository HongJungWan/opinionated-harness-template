// ArchUnit 드롭인 — cross-file 구조 규칙을 *정밀* 강제하는 CI 게이트(훅과 상호보완).
// 마커는 ../ddd-markers 를 공유한다(훅과 동일 어노테이션).
plugins { java }

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

repositories { mavenCentral() }

sourceSets { main { java { srcDir("../ddd-markers") } } }

dependencies {
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
    testImplementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.3")
}

tasks.test { useJUnitPlatform() }
