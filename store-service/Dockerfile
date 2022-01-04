FROM openjdk:11

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} store-service.jar

EXPOSE 8082

ENTRYPOINT ["java","-jar","/store-service.jar"]
