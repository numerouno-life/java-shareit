Проект: ShareIt (Учебный / Pet-project)
Аналог Airbnb для аренды вещей

Стек:

Backend: Java 17, Spring Boot (Web, Data JPA), Hibernate

Тестирование: JUnit 5, Mockito (многоуровневое тестирование)

Базы данных: H2 (для тестов), PostgreSQL

API: REST, Swagger

Инструменты: Docker, Maven, Git

Что сделано:
✅ Основной функционал:

Аренда вещей (бронирование, поиск, статусы).

Комментарии к арендованным вещам.

Валидация запросов через Spring Validation.

✅ Тестирование:

Unit-тесты: Покрытие сервисов (Mockito).

Интеграционные тесты: Проверка API (через @SpringBootTest).

Mock-тесты: Например, для ItemService и BookingService.

✅ Дополнительно:

Документация API (Swagger UI).

Контейнеризация (Docker).
