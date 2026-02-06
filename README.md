<<<<<<< HEAD
NODE_ENV=development
PORT=5000
FRONTEND_URL=http://localhost:5173


MONGODB_URI=mongodb://localhost:27017/reconciliation-system


JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
JWT_EXPIRE=24h
JWT_REFRESH_EXPIRE=7d


MAX_FILE_SIZE=10485760
UPLOAD_DIR=./uploads


SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASS=your-email-password


AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_BUCKET_NAME=your-bucket-name
AWS_REGION=us-east-1


LOG_LEVEL=info
=======
# Student Management System - Backend (Java Spring Boot)

## Overview
This is the backend API for the Student Management System built with Spring Boot. It provides RESTful endpoints for authentication, student management, and leave request handling.

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## Setup Instructions

### 1. Database Setup
1. Install MySQL if not already installed
2. Create a database named `student_management` (or update the connection string in `application.properties`)
3. Update database credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### 2. Build and Run
```bash
# Navigate to the backend directory
cd StudentLoginBackend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

## API Endpoints

### Authentication
- **POST** `/api/auth/login` - User login
  ```json
  {
    "username": "S001",
    "password": "password",
    "role": "student"
  }
  ```

### Students
- **GET** `/api/students` - Get all students
- **GET** `/api/students/{studentId}` - Get student by ID
- **GET** `/api/students/username/{username}` - Get student by username
- **GET** `/api/students/{studentId}/subjects` - Get student subjects
- **GET** `/api/students/{studentId}/dashboard` - Get student dashboard data

### Leave Requests
- **POST** `/api/leaves/student/{studentId}` - Create leave request
  ```json
  {
    "startDate": "2024-12-20",
    "endDate": "2024-12-22",
    "reason": "Family function"
  }
  ```
- **GET** `/api/leaves/student/{studentId}` - Get leave requests for a student
- **GET** `/api/leaves` - Get all leave requests (optional query param: `status=PENDING|APPROVED|REJECTED`)
- **PUT** `/api/leaves/{leaveId}/status` - Update leave status
  ```json
  {
    "status": "APPROVED"
  }
  ```
- **GET** `/api/leaves/stats` - Get leave statistics

## Default Credentials
- **Admin**: username: `admin`, password: `admin123`
- **Student**: username: `S001`, password: `password`

## Project Structure
```
src/main/java/com/student/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── model/          # Entity models
├── repository/     # JPA repositories
├── service/        # Business logic
└── util/           # Utility classes
```

## Technologies Used
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL
- JWT (JSON Web Tokens)
- Lombok
- Maven

## Notes
- CORS is enabled for `http://localhost:5173` and `http://localhost:3000`
- The application auto-creates database tables on startup
- Sample data is initialized automatically via `DataInitializer`

<<<<<<< HEAD
=======
"# Student_management_Backend" 
>>>>>>> 0eaaef46b2b45e00cea312cbaefd0b1866c7e419
>>>>>>> 4693cf9228d3d04c8eabf491c9b85815ecd8c0cd
