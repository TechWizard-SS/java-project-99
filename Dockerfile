FROM gradle:8.7-jdk21 AS builder

WORKDIR /app

COPY . .

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV JAVA_OPTS="-Xmx300M -Xms300M"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]