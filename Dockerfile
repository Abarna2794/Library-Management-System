FROM openjdk:17-jdk-slim

WORKDIR /app

COPY library-Management-System .

RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/*.jar"]