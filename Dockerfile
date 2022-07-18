FROM openjdk:11.0.13

RUN mkdir -p /var/services/service
WORKDIR /var/services/service
COPY target/api-gateway-1.0-SNAPSHOT.jar service.jar
COPY src/main/resources/gw-config.yaml config.yml
CMD java -jar service.jar start ./config.yml
