#  Library Management System

A production-ready RESTful backend API for managing a library — built with Spring Boot, secured with JWT authentication, documented via Swagger UI, and deployed on Render.

---

##  Live Demo

 Link :

Base URL - `https://library-management-system-r6gr.onrender.com ` 
Swagger UI - `https://library-management-system-r6gr.onrender.com/swagger-ui.html` 

---


##  Tech Stack

| Layer | Technology |

| Framework | Spring Boot |
| Security | Spring Security + JWT  |
| Database | JPA / Hibernate |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Deployment | Render |

---

##  Features

-  User registration & login with JWT-based authentication
-  Role-based access control (ADMIN / USER)
-  Full book management — add, update, delete, search, paginate
-  Borrow & return tracking with due date management
-  Automatic fine calculation for late returns (₹10/day)
-  Per-user borrow limit enforcement (max 3 books at a time)
-  Duplicate ISBN detection
- ️ Global custom exception handling
-  Swagger UI with Bearer token support

---

##  Project Structure

```
src/main/java/com/example/library_Management_System/
│
├── controller/
│   ├── AuthController.java          # Register & Login
│   ├── BookController.java          # Book CRUD & Search
│   └── BorrowController.java        # Borrow & Return
│
├── service/
│   ├── UserService.java
│   ├── BookService.java
│   ├── BorrowService.java
│   └── CustomUserDetailsService.java
│
├── dto/
│   ├── BookDto.java
│   ├── BorrowDto.java
│   ├── RegisterDto.java
│   └── UserDto.java
│
├── repo/
│   ├── UserRepo.java
│   ├── BookRepo.java
│   └── BorrowRecordRepo.java
│
├── entity/                          # User, Book, BorrowRecord, Role
├── exceptionHandler/                # Custom global exception handling
├── JwtUtil.java
├── JwtAuthFilter.java
└── OpenApiConfig.java
```

---

##  Authentication

This API uses JWT Bearer tokens.

### 1. Register

```http
POST /auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret123"
}
```

### 2. Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "secret123"
}
```

Response:

```json
{
  "token": "<jwt-token>",
  "type": "Bearer"
}
```

Use the token in all subsequent requests:

```
Authorization: Bearer <jwt-token>
```

---

##  API Endpoints

### Public (No Auth Required)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/register` | Register a new user |
| `POST` | `/auth/login` | Login and receive a JWT token |

---

###  Books

| Method | Endpoint | Role | Description |
|---|---|---|---|
| `GET` | `/books` | Any | Get all books (paginated) |
| `GET` | `/books/search?title=` | Any | Search books by title |
| `GET` | `/books/searchByAuthor?author=` | Any | Search books by author |
| `GET` | `/books/available` | Any | List books with available copies |
| `GET` | `/user/books` | USER | Get all books |
| `GET` | `/user/books/{id}` | USER | Get book by ID |
| `POST` | `/admin/books` | ADMIN | Add a new book |
| `PUT` | `/admin/books/{id}` | ADMIN | Update a book |
| `DELETE` | `/admin/books/{id}` | ADMIN | Delete a book |

**Book Request Body:**

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "availableCopies": 3
}
```

---

###  Borrow & Return

| Method | Endpoint | Role | Description |
|---|---|---|---|
| `POST` | `/user/borrow/{userId}/{bookId}` | USER | Borrow a book |
| `POST` | `/user/return/{recordId}` | USER | Return a borrowed book |

**Business Rules:**
- A user can borrow a maximum of **3 books** at a time
- Borrow period is **7 days**
- Late returns incur a fine of **₹10 per day**
- A book with 0 available copies cannot be borrowed

**Return Response:**

```json
{
  "id": 1,
  "userId": 2,
  "bookId": 5,
  "bookTitle": "Clean Code",
  "borrowDate": "2025-01-01",
  "dueDate": "2025-01-08",
  "returnDate": "2025-01-10",
  "status": "RETURNED",
  "fine": 20.0
}
```

---

##  Testing with Swagger UI

1. Navigate to `/swagger-ui/index.html`
2. Call `POST /auth/login` to receive your token
3. Click Authorize 🔒 at the top right
4. Enter: `Bearer <your-token>`
5. All secured endpoints are now accessible

---

## ⚙ Running Locally

### Prerequisites

- Java 17+
- Maven
- MySQL / PostgreSQL *(or H2 for in-memory dev)*

### Steps

```bash
# Clone the repository
git clone https://github.com/<your-username>/library-management-system.git
cd library-management-system

# Configure your database in src/main/resources/application.properties
# spring.datasource.url=jdbc:mysql://localhost:3306/librarydb
# spring.datasource.username=root
# spring.datasource.password=yourpassword

# Run the application
./mvnw spring-boot:run
```

The app will start at **http://localhost:8080**

---

##  Deployment (Render)

This project is deployed on Render as a Web Service.

| Step | Detail |

| 1 | Push code to GitHub |
| 2 | Create a new Web Service on Render |
| 3 | Build command: `./mvnw clean package -DskipTests` |
| 4 | Start command: `java -jar target/*.jar` |
| 5 | Add environment variables for DB credentials and JWT secret |

---

##  Validation Rules

| Field | Rule |
|---|---|
| `email` | Valid email format, not blank |
| `password` | Minimum 6 characters |
| `name` | Not null |
| `title` | Not blank |
| `author` | Not blank |
| `isbn` | Not blank, must be unique |
| `availableCopies` | Minimum value of 1 |

---

##  Error Handling

All errors are handled globally via `@RestControllerAdvice` in `GlobalExceptionHandler` for consistent API responses:

| Exception | HTTP Status | Trigger |
|---|---|---|
| `UserNotFoundException` | `404 Not Found` | User does not exist |
| `BookNotAvailableException` | `400 Bad Request` | Book has no available copies |
| `DuplicateIsbnException` | `400 Bad Request` | ISBN already exists in the system |
| `MethodArgumentNotValidException` | `400 Bad Request` | Bean validation failures (returns field → message map) |
| `RuntimeException` | `400 Bad Request` | Any other unhandled runtime error |

---

## Author
-  GitHub: [@Abarna2794](https://github.com/your-username)

