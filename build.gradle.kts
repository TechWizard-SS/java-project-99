plugins {
	java
	id("org.springframework.boot") version "3.5.10"
	id("io.spring.dependency-management") version "1.1.7"
	id("jacoco")
	checkstyle
	id("org.sonarqube") version "6.2.0.5505"
	id("io.sentry.jvm.gradle") version "6.0.0"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

sentry {
	// Включает загрузку исходного кода в Sentry для отображения контекста ошибки
	includeSourceContext.set(true)

	org.set("hexletio-0o")
	projectName.set("java-spring-boot")

	// Берем токен из переменной окружения
	authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))
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

		property("sonar.sources", "src/main/java")
		property("sonar.tests", "src/test/java")
	}
}
tasks.jacocoTestReport {
	dependsOn(tasks.test) // Твое существующее условие

	reports {
		xml.required.set(true)
		csv.required.set(false)
		html.required.set(true)
	}

	// Классы, которые не требуют покрытия тестами
//	classDirectories.setFrom(
//		sourceSets.main.get().output.asFileTree.matching {
//			exclude(
//				"hexlet/code/util/**",
//				"hexlet/code/component/**",
//				"hexlet/code/AppApplication.class",
//				"hexlet/code/model/**",
//				"hexlet/code/model/dto/**"
//			)
//		}
//	)
}

tasks.withType<Test> {
	useJUnitPlatform()
}

