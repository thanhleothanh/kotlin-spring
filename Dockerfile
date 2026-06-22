FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew gradlew.bat ./
COPY gradle gradle/
RUN chmod +x gradlew

COPY build.gradle.kts settings.gradle.kts ./
RUN ./gradlew dependencies --no-daemon

COPY src src/
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app

RUN addgroup --system app && adduser --system --ingroup app app

COPY --chown=app:app --from=build /app/build/libs/*.jar app.jar

USER app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
