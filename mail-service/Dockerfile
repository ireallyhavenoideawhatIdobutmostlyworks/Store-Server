FROM openjdk:11

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} mail-service.jar

EXPOSE 8084

ENTRYPOINT ["java","-jar","/mail-service.jar"]
