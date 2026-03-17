FROM gradle:8.7-jdk21

WORKDIR /

COPY / .

RUN gradle installDist

CMD ./build/install/app/bin/app


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app


COPY --from=builder /app/build/libs/*.jar app.jar


ENV JAVA_OPTS="-Xmx300M -Xms300M"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]