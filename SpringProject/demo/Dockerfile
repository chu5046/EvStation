FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar
RUN cp build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]