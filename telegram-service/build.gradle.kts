plugins {
    id("java")
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.bitebuilders.telegram"
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
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.postgresql:postgresql")
    implementation("org.telegram:telegrambots-spring-boot-starter:6.8.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveBaseName.set("telegram-service")
    archiveVersion.set("1.0-SNAPSHOT")
    archiveClassifier.set("")
}

springBoot {
    mainClass.set("org.bitebuilders.telegram.TelegramBotApplication") // ← укажи свой класс с main()
}
