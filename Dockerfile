FROM openjdk:17-jdk-alpine
COPY ./target/contas-0.0.1-SNAPSHOT.jar app.jar
CMD ["java","-jar","app.jar"]
