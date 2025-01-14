FROM bellsoft/liberica-openjdk-debian:21


LABEL authors="edzeeee"

COPY ./.mvn ./.mvn
COPY ./mvnw ./mvnw
COPY ./mvnw.cmd ./mvnw.cmd
COPY ./pom.xml ./pom.xml
COPY ./src ./src

EXPOSE 8082

RUN ./mvnw clean install -DskipTests
RUN ./mvnw vaadin:prepare-frontend
RUN ./mvnw vaadin:build-frontend

ENV TZ=Europe/Moscow
RUN cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone


CMD ./mvnw spring-boot:run
