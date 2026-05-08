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
-   post-service - создание и управление постами, управление медиа файлами
-   notification-service - обработка событий и генерация уведомлений
-   feed-service - лента постов из подписок
-   reaction-service - управление реакциями (лайк/дизлайк)
-   comment-service - управление комментариями под постами

### Взаимодействие сервисов

-   Синхронное: REST API\
-   Асинхронное: Kafka (event-driven architecture)

------------------------------------------------------------------------

## Event-driven flow

1.  Пользователь подписывается на другого пользователя\
2.  Сервис публикует событие `UserFollowedEvent` в Kafka\
3.  notification-service получает событие\
4.  Создаётся уведомление для пользователя
   (TODO: Реакция -> уведомление / Комментарий -> уведомление)

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
- JWT-based stateless authentication with ownership-based authorization for protected resources.
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
/feed-service\
/comment-service\
/reaction-service

------------------------------------------------------------------------

## Run Locally

git clone https://github.com/wilddog1266/social_network_backend.git\

cd social_network_backend\

### Backend:
docker-compose up --build

### Frontend
cd frontend\
npm run dev

frontend: localhost:5173

------------------------------------------------------------------------

## Security

-   JWT access tokens\
-   Stateless authentication\
-   Проверка ownership данных

------------------------------------------------------------------------

## Current Status

В процессе: 
- уведомления на реакции и комментарии\
- подписки из ленты\
- отдельная страница пользователя


------------------------------------------------------------------------

## Roadmap

-   [x] JWT authentication\
-   [x] Kafka integration\
-   [x] Notification service\
-   [x] Feed generation\
-   [x] Comments & Likes\
-   [x] Comments & Reactions notifications\
-   [x] Media optimization\
-   [x] Move the media management to a separate service\
-   [ ] Media debug\

------------------------------------------------------------------------

## Author

Vladislav Fomin\
GitHub: https://github.com/wilddog1266
