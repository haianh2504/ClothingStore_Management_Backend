# Clothing Store Demo

A backend for a clothing store built to practice Spring Boot, JPA, Hibernate,
transaction management, PostgreSQL, Redis and REST API development.

## Project Status

Currently building the backend

## Technology Stack

### Language and Build

- Java 21
- Maven
- Lombok

### Backend

- Spring Boot 4.1.1
- Spring Web MVC for REST APIs
- Spring Data JPA
- Hibernate ORM
- Spring Validation
- Spring Security
- Spring Session with Redis
- Spring Data Redis
- Redis
- PostgreSQL

### Application Features

- Cookie-based administrator sessions using `ADMIN_SESSION`
- Role-based administrator authorization
- BCrypt password hashing
- Redis-backed fixed-window rate limiting
- Layered architecture with Controller, Service, Repository and DTOs
- Standardized API responses and exception handling

### Testing and Development

- JUnit Jupiter
- Spring Security Test
- Spring Data JPA Test
- Spring Validation Test
- MockMvc
- Integration testing
- Spring Boot DevTools

### Planned Frontend

- Next.js
- TypeScript
## Documentation

- [Business Requirements](./docs/preparation/01-business-requirements.md)
- [Domain Model](./docs/preparation/02-domain-model.md)
- [Entity Relationship Diagram](./docs/preparation/03-ERD.md)
- [Use Cases](./docs/preparation/04-use-cases.md)
- [API Contract](./docs/preparation/05-api-contract.md)
