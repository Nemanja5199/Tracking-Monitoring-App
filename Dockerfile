
FROM eclipse-temurin:17-jdk-jammy as builder


WORKDIR /app


COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle


RUN chmod +x ./gradlew


COPY src src


RUN ./gradlew build -x test


FROM eclipse-temurin:17-jre-jammy

WORKDIR /app


COPY --from=builder /app/build/libs/*.jar app.jar


EXPOSE 8080


ENTRYPOINT ["java", "-jar", "app.jar"]