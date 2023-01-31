FROM ubuntu:latest

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update -y

RUN apt-get install -y docker-compose
RUN apt-get install -y openjdk-17-jre
RUN mkdir -p /opt/factorio/
RUN chmod 777 /opt/factorio/

COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
