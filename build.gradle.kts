plugins {
	java
	id("org.springframework.boot") version "3.5.10"
	id("io.spring.dependency-management") version "1.1.7"
	id("jacoco")
	checkstyle
	id("org.sonarqube") version "6.2.0.5505"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot Core
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")


	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	implementation("org.springframework.security:spring-security-crypto")

	// DB
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// MapStruct (Важен порядок!)
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	// Эта строка критична для работы Lombok + MapStruct вместе
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	// JsonNullable (для частичного обновления)
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")

	// Tests
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.instancio:instancio-junit:3.3.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

checkstyle {
	toolVersion = "10.12.1"
	configFile = file("config/checkstyle/checkstyle.xml")
	isIgnoreFailures = false
	isShowViolations = true
}

sonar {
	properties {
		property("sonar.projectKey", "TechWizard-SS_java-project-99")
		property("sonar.organization", "techwizard-ss")
		property("sonar.host.url", "https://sonarcloud.io")

		property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
		property("sonar.java.binaries", "build/classes/java/main")
	}
}

tasks.jacocoTestReport {
	dependsOn(tasks.test) // Отчет строится только после прохождения тестов
	reports {
		xml.required.set(true)  // Обязательно для Sonar
		csv.required.set(false)
		html.required.set(true) // Полезно для тебя, чтобы смотреть покрытие локально
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

