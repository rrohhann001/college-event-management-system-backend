# 🎓 College Event Management System - Backend

A **Spring Boot REST API** project for managing college events, students, and event registrations.  
This backend system allows administrators to create events and students to register for them securely using **JWT Authentication**.

---

## 🚀 Features

- Student Registration and Management
- Create and Manage Events
- Event Registration System
- JWT Authentication & Authorization
- RESTful APIs
- Swagger API Documentation
- Global Exception Handling
- Layered Architecture (Controller → Service → Repository)

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

## 🔐 Authentication Flow (JWT)

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

### Students

```
GET    /api/students
GET    /api/students/{id}
POST   /api/students
DELETE /api/students/{id}
```

### Events

```
GET  /api/events
POST /api/events
```

### Registrations

```
POST /api/registrations
GET  /api/registrations
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
src/main/resources/application.properties
```

Example:

```
spring.datasource.url=jdbc:mysql://localhost:3306/event_management
spring.datasource.username=your_username
spring.datasource.password=your_password
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

**Rohan Singh**

GitHub  
https://github.com/rrohhann001

LinkedIn  
https://www.linkedin.com/in/rohan-singh-1515693b4

---

## ⭐ Support

If you like this project, give it a **⭐ star on GitHub**.

---

## 👨‍💻 Normal Understanding Or Let me Explain In Simple Language

abhi mere project ka backend kya kya kar sakta hai

**Web Site Run(Backend Run)**

- sabse pehel tum application.properties mai ye paste karoge
- spring.application.name=eventManagement
spring.datasource.url=jdbc:mysql://localhost:3306/eventManagement
spring.datasource.username=root
spring.datasource.password=MySQL_ka_Password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

- ab tum run button se run kar ke http://localhost:8080/swagger-ui/index.html ish url par jaoge
- then tum ye sab neeche vali cheez kar sakte ho.
- 

**Student ke liye**

- sabse pehel website open kar ke student registration ya kah do sign up karega.
- fir ush ko emai and password ke through login karna hoga, jis se ush ko token milega, ush token ko copy kar ke autherize mai paste kar do.
- then to apni profile dekh sakte ho,
- All events dekh sakte ho.
- tum kitne events mai register(particiopate) ho un sabhi events ko dekh sakte ho.
- date ke through events ko dekh sakte hai.
- event id ke through event ko dekh sakte ho.

**Admin ke liye**

- pehle admin banane ke liye student jese regitration(sign Up) karna hoga
- then fir database mai jakar ek query run karni hogi (UPDATE students SET role = 'ADMIN' WHERE email = 'admin@gmail.com';)
- ye ish mai jo email hogi vo admin ki hogi.
- ab ye admin login karega fir ush ko token milega ush ko copy kar ke autherize mai paste kar dena.
- then tum sab kuchh access kar sakte ho like event post(create), delete, get.
- student get All students, get student by id, delete student by id.

**Register for events**
- student apne token ko authorize mai daal ke register kar sakte hai events mai event ki id daal kar.
