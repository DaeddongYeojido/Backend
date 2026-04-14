# ─── Stage 1: Build ───────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Gradle Wrapper + 의존성 캐시 레이어 분리
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon -q || true

# 소스 복사 & 빌드 (테스트 제외)
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test -q

# ─── Stage 2: Runtime ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 타임존 설정 (서울)
ENV TZ=Asia/Seoul

# 보안: 전용 유저로 실행
RUN addgroup --system daeddong && adduser --system --ingroup daeddong daeddong

# 빌드 결과물만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# Firebase 서비스 계정 JSON 경로 (Secret Manager에서 마운트되거나 직접 복사)
# 실제 운영 시 아래 주석을 해제하거나 Secret Manager 방식 사용
# COPY firebase-service-account.json ./firebase-service-account.json

USER daeddong

# Cloud Run은 8080 포트 사용 (application.yml과 일치)
EXPOSE 8080

# Prod 프로파일 활성화, JVM 메모리 조정 (Cloud Run 최소 인스턴스 기준)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Dspring.profiles.active=prod", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
