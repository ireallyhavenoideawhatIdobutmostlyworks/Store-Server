FROM openjdk:11

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} pdf-service.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","/pdf-service.jar"]
