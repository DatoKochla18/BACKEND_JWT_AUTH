# BACKEND\_JWT\_AUTH

A backend REST API for user authentication and note management, cloned from Philip Lackner's Kotlin Spring Boot JWT tutorial and customized with SQL Server integration.

## Table of Contents

* [Features](#features)
* [Tech Stack](#tech-stack)
* [API Endpoints](#api-endpoints)
* [Authentication Flow](#authentication-flow)


## Features

* User Registration and Login using JWT (JSON Web Tokens)
* Secure password hashing
* CRUD operations for notes
* Role-based authorization support
* Refresh token mechanism
* Integration with SQL Server database

## Tech Stack

* **Language:** Kotlin
* **Framework:** Spring Boot
* **Authentication:** JWT (JSON Web Tokens)
* **Database:** SQL Server
* **ORM:** Spring Data JPA (Hibernate)
* **Dependency Injection:** Spring Context
* **Build Tool:** Gradle (Kotlin DSL)
* **Configuration:** `application.properties`
* **Logging:** Logback
*

## API Endpoints

### Auth

| Method | Endpoint           | Description                              |
| ------ | ------------------ | ---------------------------------------- |
| POST   | /api/auth/register | Register a new user                      |
| POST   | /api/auth/login    | Authenticate user and issue JWT          |
| POST   | /api/auth/refresh  | Get new access token using refresh token |

### Notes

| Method | Endpoint        | Description                          |
| ------ | --------------- | ------------------------------------ |
| GET    | /api/notes      | Get all notes for authenticated user |
| GET    | /api/notes/{id} | Get a single note by ID              |
| POST   | /api/notes      | Create a new note                    |
| PUT    | /api/notes/{id} | Update an existing note              |
| DELETE | /api/notes/{id} | Delete a note                        |

## Authentication Flow

1. **Register:** User sends credentials to `/api/auth/register`. Password is hashed and user saved in database.
2. **Login:** User sends credentials to `/api/auth/login`. On success, API returns an access token (JWT) and refresh token.
3. **Access Protected Routes:** Client includes Bearer token in `Authorization` header to access note endpoints.
4. **Refresh Token:** When access token expires, client calls `/api/auth/refresh` with refresh token to get a new access token.
