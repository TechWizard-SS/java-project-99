### Hexlet tests and linter status:
[![Actions Status](https://github.com/TechWizard-SS/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/TechWizard-SS/java-project-99/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=TechWizard-SS_java-project-99&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=TechWizard-SS_java-project-99)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=TechWizard-SS_java-project-99&metric=bugs)](https://sonarcloud.io/summary/new_code?id=TechWizard-SS_java-project-99)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=TechWizard-SS_java-project-99&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=TechWizard-SS_java-project-99)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=TechWizard-SS_java-project-99&metric=coverage)](https://sonarcloud.io/summary/new_code?id=TechWizard-SS_java-project-99)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=TechWizard-SS_java-project-99&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=TechWizard-SS_java-project-99)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=TechWizard-SS_java-project-99&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=TechWizard-SS_java-project-99)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=TechWizard-SS_java-project-99&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=TechWizard-SS_java-project-99)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=TechWizard-SS_java-project-99&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=TechWizard-SS_java-project-99)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=TechWizard-SS_java-project-99&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=TechWizard-SS_java-project-99)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=TechWizard-SS_java-project-99&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=TechWizard-SS_java-project-99)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=TechWizard-SS_java-project-99&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=TechWizard-SS_java-project-99)
![Deploy Status](https://img.shields.io/badge/Render-Deployed-brightgreen)

# Task Manager (Менеджер задач)

**Live Demo:** [https://java-project-99-w1aw.onrender.com](https://java-project-99-w1aw.onrender.com)
https://java-project-99-w1aw.onrender.com

## 🚀 Стек технологий

* **Java 21** & **Spring Boot 3**
* **Spring Security** (JWT Authentication)
* **Spring Data JPA** (Hibernate)
* **MapStruct** & **JsonNullable** (для гибкого обновления сущностей)
* **Lombok**
* **H2** (Development/Tests) & **PostgreSQL** (Production)
* **Sentry** (Мониторинг ошибок)
* **GitHub Actions** (CI/CD)

---

## 🛠 Установка и запуск

### Требования
* **JDK 21** или выше
* **Gradle 8.x**

### Локальный запуск (Development профиль)
По умолчанию используется база данных H2 в памяти.



## 📖 Функциональные возможности
### Панель управления
При запуске приложения автоматически создается администратор. Обладая правами администратора, вы можете управлять учетными записями других пользователей.

<img width="534" height="434" alt="image" src="https://github.com/user-attachments/assets/1a4b6e70-7637-417b-8bd3-a3af41055fd4" />

<img width="1212" height="267" alt="image" src="https://github.com/user-attachments/assets/7891607c-0d64-4903-b6c1-dd7a247c46eb" />


### Управление пользователями
Для регистрации нового пользователя необходимо заполнить следующие поля:

<img width="418" height="520" alt="image" src="https://github.com/user-attachments/assets/164cf6a3-9408-4b29-8473-b1c23285ea78" />

### Метки (Labels)
По умолчанию в системе доступны предустановленные метки. Их можно привязывать к задачам для быстрой фильтрации и удобной навигации.

<img width="325" height="225" alt="image" src="https://github.com/user-attachments/assets/34a2267f-e69f-44b2-b8db-493bfea97f4e" />

Вы также можете создавать собственные метки, заполнив форму:

### Статусы задач
В приложении реализована система встроенных статусов, отражающих жизненный цикл задачи.

<img width="1586" height="330" alt="image" src="https://github.com/user-attachments/assets/6de355b0-9084-47fe-b874-00cde677a148" />

При необходимости вы можете добавить уникальные статусы, соответствующие вашим внутренним процессам:

<img width="399" height="333" alt="image" src="https://github.com/user-attachments/assets/425aa4de-da5e-4c9d-922d-dfec9f4fc134" />

### Работа с задачами
Центральная часть приложения — управление задачами. Вы можете создавать задачи, назначать исполнителей, устанавливать дедлайны и отслеживать прогресс выполнения.

<img width="1880" height="496" alt="image" src="https://github.com/user-attachments/assets/9098a7ea-49ba-416a-9ee0-eb2343039382" />

<img width="1332" height="716" alt="image" src="https://github.com/user-attachments/assets/dbcdfbef-a6c2-40ef-9ccd-192ff8e22dea" />

<img width="1896" height="382" alt="image" src="https://github.com/user-attachments/assets/74383a74-0919-41a0-8a8b-3f86daa82a17" />


## 🧪 Тестирование
Проект покрыт Unit и интеграционными тестами. Для анализа покрытия кода используется JaCoCo.

## Запуск тестов: 
``` ./gradlew test ``` 

# 📖 Технические особенности
### Частичные обновления (Partial Updates): Использование JsonNullable позволяет обновлять только те поля, которые были переданы в запросе.

### Безопасность: Реализована аутентификация на основе JWT-токенов.

### Мониторинг: Интеграция с Sentry обеспечивает мгновенное получение уведомлений об ошибках в реальном времени.
