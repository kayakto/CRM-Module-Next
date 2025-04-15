plugins {
    id("java")
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.bitebuilders"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // JWT для авторизации
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // PostgreSQL driver
    implementation("org.postgresql:postgresql")

//    // Jakarta Servlet API для работы с HttpServletRequest и HttpServletResponse
//    implementation("jakarta.servlet:jakarta.servlet-api")

    // Tomcat 10.x (обязательно для Spring Boot 3.x)
    implementation("org.apache.tomcat.embed:tomcat-embed-core")

    // Lombok для сокращения кода
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Тестовые зависимости
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito")
    }
    testImplementation("org.junit.jupiter:junit-jupiter")
}

configurations {
    all {
        exclude(group = "javax.servlet", module = "javax.servlet-api")
    }
}

tasks.test {
    useJUnitPlatform()
    // previous JVM options
    jvmArgs(
        "-Djdk.instrument.traceUsage=false",
        "-XX:+DisableAttachMechanism"
    )
}

tasks.jar {
    manifest.attributes["Main-Class"] = "org.bitebuilders.UralinternCRMApiApplication"
}