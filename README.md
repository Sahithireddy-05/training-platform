# Training Sessions Platform

Full-stack Spring Boot web application for internal training-topic requests, recommendations, speaker claiming, scheduling, enrollment, and post-session ratings.

## Setup & Run

Prerequisites:
- Java 17
- Maven
- Microsoft SQL Server
- SQL Server Management Studio (SSMS), optional but useful for inspecting data

Run locally:

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8081
```

The application uses SQL Server. Create a database named:

```text
TrainingPlatformDb
```

Then confirm `src/main/resources/application.properties` has the SQL Server connection details for your local machine:

```properties
server.port=8081
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=TrainingPlatformDb;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourStrongPassword
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.hibernate.ddl-auto=update
```

Update the username and password to match your SQL Server login.

## Tech Stack & Rationale

- Java 17 and Spring Boot 3 for a standard enterprise-style MVC application.
- Spring MVC and Thymeleaf for simple server-rendered screens.
- Spring Security for authentication and route protection.
- BCrypt password hashing through `BCryptPasswordEncoder`.
- Spring Data JPA and Hibernate for relational persistence.
- Microsoft SQL Server as the persistent relational database.
- Microsoft JDBC Driver for SQL Server for database connectivity.

## Architectural Overview

- `entity`: JPA models for users, topics, recommendations, enrollments, and ratings.
- `repository`: Spring Data repositories.
- `service`: lifecycle rules, authorization checks, validation, and persistence workflows.
- `controller`: MVC routes for auth, dashboard, topics, recommendations, scheduling, enrollments, and ratings.
- `security`: Spring Security configuration and user details integration.
- `templates`: Thymeleaf pages.
- `static`: CSS and small JavaScript helpers.

## Lifecycle Rules Implemented

- Open topics can be recommended, claimed, edited by requester, or cancelled by requester.
- Users cannot recommend their own topic.
- Claimed topics can be scheduled only by the speaker.
- Speakers can unclaim before scheduling.
- Scheduled sessions can be enrolled in by users other than the speaker.
- Capacity is enforced and cannot be reduced below current enrollment count.
- Scheduled sessions become Completed on next read/write after scheduled time passes.
- Only enrolled attendees can rate completed sessions.
- Speaker cannot rate their own session.
- Re-rating updates the existing rating.

## How AI Tools Were Used

This implementation was AI-assisted. The assistant generated the initial project files, service rules, controllers, templates, and README based on the supplied requirements. The lifecycle logic was reviewed and adjusted to keep the demo compact while preserving the required business rules.

## Assumptions

- All authenticated users have the same global role.
- Authorization is based on lifecycle ownership: requester, speaker, enrollee, or rater.
- A session is represented by the scheduled fields on `Topic`, rather than a separate `Session` table, to keep the six-hour implementation lean.
- SQL Server is expected to be running locally on port `1433`, with a database named `TrainingPlatformDb`.

## Trade-offs

- UI polish is intentionally simple.
- Pagination is implemented with a default page size of 20.
- No email notifications, calendar integration, waitlist, file uploads, or admin roles.
- Recommendation-count sorting is strongest in the Most Wanted page; the general list keeps simple JPA sorting.

## Future Work

- Add automated integration tests for every lifecycle rule.
- Add richer search and tags.
- Add a calendar view for upcoming sessions.
- Split scheduled sessions into a separate entity if recurring or repeated sessions are needed.
- Add Docker Compose for SQL Server.
