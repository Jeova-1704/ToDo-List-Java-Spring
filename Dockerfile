FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y

RUN apt-get install software-properties-common -y
RUN add-apt-repository universe
RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt-get update

FROM openjdk:17-jdk-slim

COPY . .

RUN apt-get install maven -y

EXPOSE 8080

RUN mvn clean install

COPY --from=build /target/todolist-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
