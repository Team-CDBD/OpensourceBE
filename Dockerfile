# 1단계: 빌드 스테이지
FROM gradle:8.8-jdk17 AS build
WORKDIR /app

# 프로젝트 메타파일 복사 및 의존성 캐싱
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew build --no-daemon -x test || return 0

# 전체 소스 복사 및 JAR 빌드 (테스트 제외)
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon -x test

# 2단계: 실행 스테이지
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]