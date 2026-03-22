# Social Network Backend (Microservices Architecture)

Backend система социальной сети, построенная на микросервисной
архитектуре с использованием event-driven взаимодействия.

Проект демонстрирует подходы к разработке production-ready
backend-систем: декомпозицию на сервисы, асинхронную коммуникацию через
брокер сообщений, безопасную аутентификацию и работу с медиа.

------------------------------------------------------------------------

## Architecture

Система разделена на независимые сервисы:

-   auth-service - аутентификация и авторизация пользователей (JWT)
-   user-service - управление пользователями и профилями
-   post-service - создание и управление постами
-   notification-service - обработка событий и генерация уведомлений
-   media-service - работа с файлами (S3/MinIO)

### Взаимодействие сервисов

-   Синхронное: REST API\
-   Асинхронное: Kafka (event-driven architecture)

------------------------------------------------------------------------

## Event-driven flow

1.  Пользователь подписывается на другого пользователя\
2.  Сервис публикует событие `UserFollowedEvent` в Kafka\
3.  notification-service получает событие\
4.  Создаётся уведомление для пользователя

------------------------------------------------------------------------

## Tech Stack

-   Java 17+
-   Spring Boot
-   Spring Security (JWT)
-   Apache Kafka
-   PostgreSQL
-   Redis
-   Docker / Docker Compose
-   MinIO (S3-compatible storage)

------------------------------------------------------------------------

## Features

-   Stateless JWT authentication
-   Role-based access control
-   Event-driven architecture (Kafka)
-   REST API
-   Pagination и фильтрация (Specification API)
-   Глобальная обработка ошибок
-   Контейнеризация сервисов (Docker)

------------------------------------------------------------------------

## Project Structure

/auth-service\
/user-service\
/post-service\
/notification-service\
/media-service

------------------------------------------------------------------------

## Run Locally

git clone https://github.com/wilddog1266/social_network_backend.git\
cd social_network_backend\
docker-compose up --build

------------------------------------------------------------------------

## Security

-   JWT access tokens\
-   Stateless authentication\
-   Проверка ownership данных

------------------------------------------------------------------------

## Current Status

Реализовано: - JWT authentication\
- Kafka event flow\
- notification-service

В процессе: - feed-service\
- комментарии и лайки

------------------------------------------------------------------------

## Roadmap

-   [x] JWT authentication\
-   [x] Kafka integration\
-   [x] Notification service\
-   [ ] Feed generation\
-   [ ] Comments & Likes\
-   [ ] Media optimization

------------------------------------------------------------------------

## Author

Vladislav Fomin\
GitHub: https://github.com/wilddog1266
