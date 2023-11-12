FROM openjdk:17-jdk

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8086

CMD ["java", "-jar", "app.jar"]