FROM gradle:8.7-jdk21

WORKDIR /

COPY / .

RUN gradle installDist

CMD ./build/install/app/bin/app