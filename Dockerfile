# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# ---------- RUN STAGE ----------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# copy the jar explicitly
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
