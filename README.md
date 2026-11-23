# student-management-system
A Student Management System developed using Java Swing, JDBC and MySQL.


# Student Management System

A desktop-based Student Management System developed using Java Swing, JDBC, and MySQL. This application helps manage student records through a simple graphical user interface.

## Project Overview

The Student Management System is designed to simplify student record management. It allows users to add, view, search, update, and delete student information through an interactive GUI.

## Technologies Used

- Java
- Java Swing (GUI)
- JDBC (Java Database Connectivity)
- MySQL
- Visual Studio Code

## Features

- Add new student records
- View all student records
- Search students using Student ID
- Update existing student details
- Delete student records
- Clear input fields
- Store student information in MySQL database

## Database Structure

Database Name: `student_db`

Table Name: `students`

| Column Name | Data Type |
|---|---|
| student_id | INT (Primary Key) |
| name | VARCHAR(100) |
| department | VARCHAR(50) |
| age | INT |
| email | VARCHAR(100) |

## Project Structure

```text
StudentManagementSystem/
│
├── src/
│   └── StudentGUI.java
│
├── lib/
│   └── mysql-connector-j.jar
│
└── README.md
```

*Note: The actual folder structure may vary.*

## Installation and Setup

### 1. Install Requirements

- Install Java JDK.
- Install MySQL Server.
- Install Visual Studio Code.
- Add MySQL Connector/J to the project classpath.

### 2. Create Database

Execute the following SQL commands in MySQL:

```sql
CREATE DATABASE student_db;

USE student_db;

CREATE TABLE students (
    student_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50),
    age INT,
    email VARCHAR(100)
);
```

### 3. Configure Database Connection

Update the database connection details in `StudentGUI.java`:

```java
Connection con = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/student_db",
    "root",
    "YOUR_MYSQL_PASSWORD"
);
```

Replace `YOUR_MYSQL_PASSWORD` with your local MySQL password. Never upload your actual password to GitHub.

### 4. Run the Application

Open the project in Visual Studio Code.

Open `StudentGUI.java` and run the main method.

## Future Enhancements

- Login and authentication system
- Student profile management
- Attendance management
- Report generation
- Improved user interface

## Author

Developed as a Java and MySQL project to practice database connectivity, CRUD operations, and desktop application development.
