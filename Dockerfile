FROM openjdk:17 as builder

WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY src src

RUN ./gradlew clean bootJar

FROM openjdk:17-jdk

COPY --from=builder /workspace/app/build/libs/*.jar /app/application.jar

EXPOSE 8080 8000

ENTRYPOINT ["java", "-jar", "/app/application.jar"]
