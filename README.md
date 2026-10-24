# HMCTS Task Management — Backend

A REST API for an HMCTS caseworker task management application. The service allows users to create, retrieve, update and delete tasks.

## Features

- Create tasks with a title, optional description, status and due date.
- Retrieve all tasks or an individual task by ID.
- Update a task's status.
- Delete tasks.
- Validate input and return appropriate HTTP error responses.
- Persist tasks in a PostgreSQL database.
- Provide API documentation through Swagger UI.
- Include automated tests.

## Prerequisites

- Java compatible with the project's Gradle configuration.
- PostgreSQL installed and running.
- IntelliJ IDEA or another Java IDE (optional).

## Database setup

1. Install and start PostgreSQL.
2. Create a database named `hmcts_tasks`.
3. Use an existing PostgreSQL user or create a dedicated user with permission to access this database.
4. Configure the database connection using the environment variables described below.

The application uses the following environment variables:

| Variable | Purpose | Default |
|---|---|---|
| `DB_HOST` | PostgreSQL host | `localhost` |
| `DB_PORT` | PostgreSQL port | `5432` |
| `DB_NAME` | Database name | `hmcts_tasks` |
| `DB_USER_NAME` | PostgreSQL username | Required |
| `DB_PASSWORD` | PostgreSQL password | Required |

The application creates or updates the database tables automatically through Hibernate when it starts.

## Getting started

Open a terminal in the backend project directory.

### Build the project

On Windows, run:

```powershell
.\gradlew.bat build
```

### Run the tests

Run the unit tests:

```powershell
.\gradlew.bat test
```

Run the integration tests:

```powershell
.\gradlew.bat integration
```

### Configure and run the application

In IntelliJ IDEA:

1. Open **Run → Edit Configurations**.
2. Select the Spring Boot `application` configuration.
3. Set the environment variables `DB_USER_NAME` and `DB_PASSWORD` to your own PostgreSQL credentials.
4. Set `DB_NAME` to `hmcts_tasks` if you want to specify it explicitly.
5. Run the application.

Alternatively, in a PowerShell terminal, set the environment variables for that terminal session and start the application:

```powershell
$env:DB_USER_NAME = "your-postgres-username"
$env:DB_PASSWORD = "your-local-password"
$env:DB_NAME = "hmcts_tasks"
.\gradlew.bat bootRun
```

Replace the example username and password with your own credentials. These commands set the variables only for the current PowerShell session.

The API runs locally on port `4000`.

## API endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/tasks` | Create a task |
| GET | `/api/tasks` | Retrieve all tasks |
| GET | `/api/tasks/{id}` | Retrieve a task by ID |
| PATCH | `/api/tasks/{id}/status?status=TODO` | Update a task's status |
| DELETE | `/api/tasks/{id}` | Delete a task |

Supported task statuses are `TODO`, `IN_PROGRESS` and `DONE`.

Tasks require a non-blank title and a valid status. Invalid input returns `400 Bad Request`. Requests for tasks that do not exist return `404 Not Found` where applicable. Successful creation returns `201 Created`, and successful deletion returns `204 No Content`.

## API documentation

With the application running, open Swagger UI:

`http://localhost:4000/swagger-ui/index.html`

Swagger UI allows you to inspect the endpoints, review request and response formats, and try API requests.

## Project structure

The application uses Spring Boot and includes:

- Controllers for handling HTTP requests.
- A task model for representing task data.
- A repository for database access.
- Exception handling and input validation.
- Automated tests.

See the source code and Gradle configuration for implementation details.
