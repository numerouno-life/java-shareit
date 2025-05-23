# ShareIt: Сервис аренды вещей  

**Стек:**  
- Java 21, Spring Boot (Web, Data JPA, Validation), Docker, Hibernate  
- Базы данных: PostgreSQL, H2 (тесты)  
- Тестирование: **JUnit 5**, **Mockito**, SpringBootTest  
- API: REST, Swagger UI  
- Инструменты: Docker, Maven  

## 📌 Функционал  
- **Бронирование вещей** с подтверждением владельцем.  
- **Поиск вещей** по названию/описанию.  
- **Комментарии** к арендованным вещам.  
- **Валидация** всех запросов.  

## 🧪 Тестирование  
Проект включает:  
- **Unit-тесты** (Mockito):  
  - Тестирование сервисов (`ItemService`, `BookingService`).  
  - Мокирование репозиториев и внешних вызовов.  
- **Интеграционные тесты** (`@SpringBootTest`):  
  - Проверка работы API от контроллеров до БД.  

## 🚀 Запуск  
```bash
docker-compose up -d  # Запуск PostgreSQL и приложения
