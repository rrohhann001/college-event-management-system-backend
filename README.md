# 🎓 College Event Management System — Full Stack

A complete **Full Stack Web Application** for managing college events, built with a **Spring Boot REST API** backend and a **modern Vanilla JS + HTML/CSS** frontend.

Students can register, verify their email via OTP, browse events, and book tickets. Admins can create/delete events, view attendees, and manage students — all from a clean, responsive dashboard.
 
---

## 🖼️ Preview

### Admin Dashboard — All Events
> Dark sidebar layout with event cards showing date, location, capacity, and admin action buttons (View Attendees / Delete).

### Admin Profile
> Shows admin name, email, and ADMIN badge in the profile tab.
 
---

## 🚀 Features

### 👨‍🎓 Student
- Register with Name, Email, Roll Number, Course, and Password
- OTP-based Email Verification (2-minute timer)
- Secure Login with JWT
- Browse all upcoming events
- Book a ticket for any event (🎟️ Get Ticket)
- View all booked tickets in "My Tickets"
- Cancel a ticket
- Delete own account from Profile (Danger Zone)
### ⚙️ Admin
- All student features +
- Create new events (Name, Date, Location, Capacity, Description)
- Delete any event
- View attendees for each event (in Admin Panel)
- View all registered students with Roll No. and Course
- Remove (delete) any student
---

## 🛠 Tech Stack

### Backend
| Technology | Purpose |
|---|---|
| Java + Spring Boot | REST API framework |
| Spring Security | Authentication & Authorization |
| JWT (jjwt 0.11.5) | Stateless token-based auth |
| Spring Data JPA + Hibernate | Database ORM |
| MySQL | Relational database |
| Spring Mail (Gmail SMTP) | OTP email delivery |
| Bucket4j | Rate limiting |
| Swagger / SpringDoc OpenAPI | API documentation |
| Lombok | Boilerplate reduction |
| Maven | Build tool |

### Frontend
| Technology | Purpose |
|---|---|
| HTML5 | Structure |
| CSS3 (Custom Design System) | Styling, animations, responsive layout |
| Vanilla JavaScript (ES6+) | Logic, API calls, DOM manipulation |
| Google Fonts (Sora + JetBrains Mono) | Typography |
| Fetch API | HTTP requests to backend |
| LocalStorage | JWT token persistence |
 
---

## 📂 Project Structure

This is a **single unified Spring Boot project**. The frontend files are served directly by Spring Boot from the `static/` folder — no separate frontend server needed.

```
eventManagement/
│
└── src/
    └── main/
        ├── java/com/cems/eventManagement/
        │   ├── controller/           ← REST API endpoints
        │   ├── service/              ← Business logic
        │   ├── repository/           ← JPA database operations
        │   ├── entity/               ← Database models (Student, Event, Registration)
        │   ├── dto/                  ← Data Transfer Objects
        │   ├── security/             ← JWT filter, config, UserDetailsService
        │   │   ├── CorsConfig.java
        │   │   ├── JwtFilter.java
        │   │   ├── JwtUtil.java
        │   │   ├── RateLimitingService.java
        │   │   ├── SecurityConfig.java
        │   │   └── SwaggerConfig.java
        │   ├── services/             ← AuthService, EmailService, EventService,
        │   │                            RegistrationService, StudentService
        │   ├── exception/            ← Global exception handling
        │   └── EventManagementApplication.java
        │
        └── resources/
            ├── static/               ← Frontend files (served at localhost:8080)
            │   ├── index.html        ← Single-page app structure
            │   ├── style.css         ← Full design system
            │   └── app.js            ← All API calls + UI logic
            └── application.properties ← DB + Mail config
```
 
---

## 📡 API Endpoints

### 🔐 Authentication
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/auth/login` | Login with email + password, returns JWT | ❌ |

### 📧 OTP & Registration
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/student/register` | Register student + send OTP to email | ❌ |
| POST | `/api/student/verify?email=&otp=` | Verify OTP → auto-login with JWT | ❌ |

### 👥 Students
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/student` | Get all students (paginated) | ✅ Admin |
| GET | `/api/student/{id}` | Get student by ID | ✅ |
| GET | `/api/student/my-profile` | Get logged-in student's profile | ✅ |
| DELETE | `/api/student/{id}` | Delete student by ID | ✅ |

### 📅 Events
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/events` | Get all events | ✅ |
| POST | `/api/events` | Create new event | ✅ Admin |
| GET | `/api/events/{id}` | Get event by ID | ✅ |
| DELETE | `/api/events/{id}` | Delete event | ✅ Admin |

### 🎟️ Registrations
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/registrations/event/{eventId}` | Book a ticket for an event | ✅ Student |
| GET | `/api/registrations/my-events` | Get all events booked by logged-in student | ✅ Student |
| GET | `/api/registrations/event/{eventId}` | Get all attendees for an event | ✅ Admin |
| DELETE | `/api/registrations/cancel/event/{eventId}` | Cancel a ticket | ✅ Student |
 
---

## 🔐 Authentication Flow

### Registration (Student)
```
1. Student fills form → POST /api/student/register
2. Backend sends OTP to email (valid 2 minutes)
3. Student enters OTP → POST /api/student/verify
4. On success → JWT token returned → stored in localStorage
5. Dashboard loads automatically
```

### Login (Existing User)
```
1. User enters email + password → POST /api/auth/login
2. Backend validates → returns JWT token
3. Token stored in localStorage
4. All subsequent requests include: Authorization: Bearer <token>
```

### Admin Role Detection
```
JWT payload is decoded on frontend.
If payload contains "ADMIN" → admin nav items shown (Admin Panel, All Students).
If student role → My Tickets + Danger Zone shown.
```
 
---

## 🗄️ Database Schema

> **Database used:** Aiven Cloud MySQL 8.0 (`defaultdb`)  
> Tables are auto-created by Hibernate (`ddl-auto=update`)

### `students` table
| Column | Type | Notes |
|---|---|---|
| id | INT (PK) | Auto-increment |
| name | VARCHAR | Full name |
| email | VARCHAR | Unique, college email |
| password | VARCHAR | BCrypt hashed |
| roll_number | VARCHAR | Unique roll number |
| course | VARCHAR | e.g. BCA, MCA |
| role | VARCHAR | `ROLE_ADMIN` or `STUDENT` |
| is_verified | TINYINT | 0 = unverified, 1 = verified (after OTP) |

> ⚠️ **Role values:** The database stores `ROLE_ADMIN` for admins and `STUDENT` for students. The frontend detects admin by checking if JWT payload contains the word "ADMIN" anywhere.

### `events` table
| Column | Type | Notes |
|---|---|---|
| id | INT (PK) | Auto-increment |
| event_name | VARCHAR | Event title |
| description | TEXT | Full description |
| event_date | DATE | Format: YYYY-MM-DD |
| location | VARCHAR | Venue name |
| capacity | INT | Max attendees |

### `registration` table
| Column | Type | Notes |
|---|---|---|
| id | INT (PK) | Auto-increment |
| student_id | INT (FK) | References students.id |
| event_id | INT (FK) | References events.id |

**Relationships:**
- One Student → Many Registrations
- One Event → Many Registrations
- One Student ↔ Many Events (through registration table)
---

## ⚙️ How to Run

### Prerequisites
- Java 17+ (project uses Java 23)
- Maven
- MySQL — local **or** cloud (e.g. Aiven)
- Gmail account with App Password enabled
---

### 1️⃣ Clone the repository
```bash
git clone https://github.com/rrohhann001/college-event-management-system-backend.git
cd college-event-management-system-backend
```

### 2️⃣ Create `application.properties`

Create the file at `src/main/resources/application.properties`.

**Option A — Local MySQL:**
```properties
spring.application.name=eventManagement
 
spring.datasource.url=jdbc:mysql://localhost:3306/eventManagement
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
 
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
 
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your@gmail.com
spring.mail.password=your_16_char_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Option B — Aiven Cloud MySQL (as used in this project):**
```properties
spring.application.name=eventManagement
 
spring.datasource.url=jdbc:mysql://<your-aiven-host>:<port>/defaultdb?ssl-mode=REQUIRED
spring.datasource.username=avnadmin
spring.datasource.password=YOUR_AIVEN_PASSWORD
 
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
 
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your@gmail.com
spring.mail.password=your_16_char_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> **Gmail App Password:** Google Account → Security → 2-Step Verification → App Passwords → Generate for "Mail".

### 3️⃣ Run the backend

Open in **IntelliJ IDEA** and run `EventManagementApplication.java`

Or via Maven terminal:
```bash
mvn spring-boot:run
```

Backend + Frontend both start at: **http://localhost:8080**

Open browser → `http://localhost:8080/index.html`

> Frontend files are in `src/main/resources/static/` so Spring Boot serves them automatically. No separate frontend server needed.
 
---

## 🧑‍💻 Making Yourself Admin

After registering as a student, run this SQL to promote your account:

```sql
USE defaultdb;   -- or your database name
 
UPDATE students
SET role = 'ROLE_ADMIN'
WHERE id = 1;    -- replace 1 with your actual student id
```

Then log out and log back in — **Admin Panel** and **All Students** tabs will appear in the sidebar.

> **Check your id** with: `SELECT id, name, email, role FROM students;`
 
---

## 🖥️ Frontend Pages & Tabs

### Auth Pages (Before Login)
| Page | Description |
|---|---|
| Login | Email + password login with show/hide password toggle |
| Register | Full registration form with Roll No. and Course |
| OTP Verify | 6-digit OTP input with 2-minute countdown timer |

### Dashboard (After Login)

**Student View:**
| Tab | Description |
|---|---|
| 📅 All Events | Browse events with date, location, capacity; Book ticket button |
| 🎟️ My Tickets | All booked events with Cancel Ticket option |
| 👤 Profile | Name, email, badge; Delete Account (Danger Zone) |

**Admin View:**
| Tab | Description |
|---|---|
| 📅 All Events | All events with View Attendees + Delete buttons |
| ⚙️ Admin Panel | Create Event form + Attendees viewer side by side |
| 👥 All Students | Table of all registered students with Remove button |
| 👤 Profile | Admin name, email, ADMIN badge |
 
---

## 📘 API Documentation (Swagger)

After starting the backend, visit:
```
http://localhost:8080/swagger-ui.html
```
Provides an interactive interface to test all endpoints directly from the browser.
 
---

## 🔒 Security Notes

- All passwords are **BCrypt hashed** — never stored in plain text
- JWT tokens expire after a configured duration — re-login required
- All dashboard API calls require `Authorization: Bearer <token>` header
- OTP expires in **1 minute** on the backend (2-minute timer shown on frontend for buffer)
- Frontend uses `escapeHtml()` on all user-generated content to prevent XSS
---

## 🐛 Common Issues & Fixes

| Problem | Fix |
|---|---|
| `Port 8080 already in use` | Run `netstat -ano \| findstr :8080` → `taskkill /PID <id> /F` |
| Students not loading | Backend returns paginated data — students are in `result.data.content[]` |
| OTP not received | Check spam folder; verify Gmail App Password is correct |
| CORS error on `file://` | Place files in `src/main/resources/static/` and use `localhost:8080` |
| `favicon.ico 403` | Harmless browser request — does not affect functionality |
 
---

## 👨‍💻 Author

**Rohan Singh**

- GitHub: [github.com/rrohhann001](https://github.com/rrohhann001)
- LinkedIn: [linkedin.com/in/rohansingh-dev](https://www.linkedin.com/in/rohansingh-dev/)
---

## 🔮 Planned Future Improvements

- [ ] Student can view event details page
- [ ] Admin dashboard with stats (total events, total students, total registrations)
- [ ] Search and filter events
- [ ] Pagination controls on All Students tab
- [ ] Edit event functionality for Admin
- [ ] Email confirmation on ticket booking
- [ ] Mobile-responsive improvements
- [ ] make front end more attractive and professional
---

## ⭐ Support

If you found this project helpful, give it a **⭐ star on GitHub!**
 




















