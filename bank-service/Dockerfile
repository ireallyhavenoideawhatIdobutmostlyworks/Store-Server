FROM openjdk:11

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} bank-service.jar

EXPOSE 8083

ENTRYPOINT ["java","-jar","/bank-service.jar"]
