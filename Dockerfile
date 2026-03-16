FROM gradle:8.5-jdk21
LABEL authors="Samir"

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN /gradlew installDist

CMD ./build/install/app/bin/app
