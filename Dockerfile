FROM node:20-alpine AS frontend
WORKDIR /app


COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build


FROM gradle:8.5-jdk21-alpine AS builder
WORKDIR /app


COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle


COPY src ./src


COPY --from=frontend /app/dist ./src/main/resources/static

RUN ./gradlew --no-daemon bootJar -x test -Dorg.gradle.jvmargs="-Xmx256m"


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app


COPY --from=builder /app/build/libs/*.jar app.jar


ENV JAVA_OPTS="-Xmx300M -Xms300M"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]