# 🎓 College Event Management System - Backend

A **Spring Boot REST API** project for managing college events, students, and event registrations.  
This backend system allows administrators to create events and students to register for them securely using **JWT Authentication**.

---

## 🚀 Features

- Student Registration and Management
- OTP-based Email Verification (1-minute expiry)
- Secure Registration with Email OTP Validation
- Automatic JWT Token Generation after Successful Registration
- Create and Manage Events
- Event Registration System
- JWT Authentication & Authorization
- RESTful APIs
- Swagger API Documentation
- Global Exception Handling
- RESTful APIs
- Swagger API Documentation
- Global Exception Handling
- Layered Architecture (Controller → Service → Repository)

---

## 🔐 OTP + Authentication Flow

### 📌 Registration Flow (New Feature)

1. Student submits registration details (including email)
2. Server sends **OTP to the provided email**
3. OTP is valid for **1 minute only**
4. Student verifies OTP using verify API
5. On successful verification:
   - Student is registered
   - **JWT Token is generated automatically**

---

### 🔐 Login Flow (JWT)

1. User logs in using credentials
2. Server generates a JWT token
3. Client sends the token in request headers
4. Server validates the token before processing the request

Example Header:

```
Authorization: Bearer <JWT_TOKEN>
```

---

## 📡 API Endpoints

### Authentication

```
POST /api/auth/login
```

---

### OTP Verification (New)

```
POST /api/student/register       → Send OTP to email
POST /api/student/verify         → Verify OTP & Complete Registration
```

---

### Students

```
GET    /api/student
GET    /api/student/{id}
DELETE /api/student/{id}
GET    /api/student/my-profile
```

---

### Events

```
GET  /api/events
POST /api/events
GET  /api/events/{id}
DELETE /api/events/{id}
```

---

### Registrations

```
POST /api/registrations/event/{eventId}
GET  /api/registrations/my-events
DELETE /api/registrations/cancel/event/{eventId}
```

---

## 🛠 Tech Stack

- **Java**
- **Spring Boot**
- **Spring Security**
- **JWT Authentication**
- **MySQL**
- **Maven**
- **Swagger / OpenAPI**

---

## 📂 Project Structure

```
src
 ├── controller        -> REST API endpoints
 ├── service           -> Business logic
 ├── repository        -> Database operations
 ├── entity            -> Database models
 ├── dto               -> Data Transfer Objects
 ├── security          -> JWT authentication and configuration
 ├── exception         -> Global exception handling
 └── resources
      └── application.properties
```

---

## 📘 API Documentation

Swagger UI available at:

```
http://localhost:8080/swagger-ui.html
```

Swagger provides an interactive interface to test APIs directly from the browser.

---

## ⚙️ How to Run the Project

### 1️⃣ Clone the repository

```
git clone https://github.com/rrohann001/college-event-management-system-backend.git
```

### 2️⃣ Open the project

Open the project using **IntelliJ IDEA** or any Java IDE.

### 3️⃣ Configure Database

Update your MySQL credentials inside:

```
firstly create a folder(package) resource in main folder, becase in this project I deleted this folder due to my privacy.
then in this package(resourse) create a file called application.properties.
src/main/resources/application.properties

write the same code as below, means copy paste 
```

Example:

```
spring.application.name=eventManagement
spring.datasource.url=jdbc:mysql://localhost:3306/eventManagement
spring.datasource.username=root
spring.datasource.password=Your_MySQL_Password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

spring.mail.host=smtp.gmail.com
spring.mail.port=587

spring.mail.username=your@gmail.com
spring.mail.password=your_app_password

spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 4️⃣ Run the application

Run the main class:

```
EventManagementApplication.java
```

Server will start at:

```
http://localhost:8080
```

---

## 🧪 Testing APIs

You can test APIs using:

- Swagger UI
- Postman
- cURL

---

## 🗄 Example Database Tables

- students
- events
- registrations

Relationship:

- One student can register for multiple events
- One event can have multiple student registrations

---

## 👨‍💻 Author

**Rohan**

GitHub  
https://github.com/rrohhann001

LinkedIn  
https://www.linkedin.com/in/rohan-singh-1515693b4

---

## ⭐ Support

If you like this project, give it a **⭐ star on GitHub**.
