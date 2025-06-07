# TaskFlow

Project for managing projects and tasks using Spring Boot.

## Description

This project provides an API for managing projects and tasks. It includes authentication using JWT, CRUD operations, role
management based on ABAC model, asynchronous operation with RabbitMQ and scheduled tasks.

## Technologies

- Java 21
- Spring Boot
- Spring Security
- JWT
- Redis
- RabbitMQ
- Gradle
- Docker

## Security Configuration

This project uses JWT for authentication and authorization. All requests sent to protected endpoints go through the 
`OncePerRequestFilter` class, which checks the correctness of the tokens signature. To access the API, you need to get a
token through the endpoint `/api/auth/login`. `Access tokens` are used to access protected endpoints, and 
`refresh tokens` are used to refresh access tokens.

## Asynchronous operations

The project supports asynchronous operations for generating reports. First, the API makes a quick check of access rights
and a check for the existence of resources, then the user receives a quick response. After the user receives the response,
the API begins collecting data and generating a report. After the report is ready, it will be sent to the user's email.

## Task Scheduler

The project includes scheduled tasks that are executed automatically according to a schedule.