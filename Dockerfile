# VK2/Dockerfile (или просто Dockerfile)

# Секция сборки (builder stage)
FROM eclipse-temurin:21-jdk-alpine AS builder

# Установка зависимостей сборки
RUN apk update && \
    apk add --no-cache maven bash

# Установка рабочей директории
WORKDIR /app

# Копируем pom.xml и исходники
COPY pom.xml .
COPY src/ src/

# Собираем проект, пропуская тесты
RUN mvn clean package -DskipTests

# Секция выполнения (runtime stage)
FROM eclipse-temurin:21-jre-alpine

# Установка рабочей директории
WORKDIR /app

# Копируем собранный JAR-файл из промежуточного образа
COPY --from=builder /app/target/*.jar marketplace-1.0-SNAPSHOT.jar

# Expose порт, на котором работает ваше приложение (обычно 8080 для Spring Boot)
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "marketplace-1.0-SNAPSHOT.jar"]
