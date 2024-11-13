# Event Management System

## Project Overview
The Event Management System is a Java-based application designed to streamline the management of various types of events, such as workshops, seminars, and competitions. This system provides an organized structure to handle user roles, event approval, venue allocation, and participant registration. Key modules include User Management, Event Management, Approval System, Venue and Department Management, and System Administration.

## Features
### 1. User Management
- Supports three user roles: **Admin**, **Organizer**, and **Viewer**.
- Manages user authentication, registration, and role-based login functionality.

### 2. Event Management
- Manages different event types, including **Workshop**, **Seminar**, and **Competition**.
- Tracks event details, schedules, and allows updates.
- Handles conflict-checking for event timings.

### 3. Approval and Request Management
- Allows Organizers to submit event requests for Admin approval.
- Admins can approve, reject, or request adjustments to avoid scheduling conflicts.

### 4. Venue and Department Management
- Tracks and allocates venues based on availability.
- Associates events with specific departments for streamlined tracking.

### 5. Participation and Registration
- Viewers can register for approved events, adhering to participant capacity limits.
- Tracks event registrations and prevents over-registration.

### 6. System Management
- Central management of data, including users, events, venues, and departments.
- Maintains an efficient system for handling and updating event information.

## Object-Oriented Principles
This project heavily uses Object-Oriented Programming (OOP) principles:
- **Abstraction:** Base classes `User` and `Event` are abstract, allowing specific roles and event types to inherit essential functionality.
- **Encapsulation:** Private fields with public getters and setters ensure controlled access to data.
- **Inheritance:** Specialized classes like `Admin`, `Organizer`, and `Viewer` extend the `User` class, while `Seminar`, `Workshop`, and `Competition` extend the `Event` class.
- **Polymorphism:** Method overriding enables customized functionality for subclasses, particularly in role-based login and event-specific operations.

## Exception Handling
A custom exception package is included:
- `alertException` serves as the base exception class, displaying errors in alert boxes.
- Derived exceptions provide specific error handling for various scenarios (e.g., invalid input, scheduling conflicts).

## Installation and Setup
1. Download the project zip file.
2. Unzip the file.
3. Open the project in your preferred Java IDE.
4. Run the command 'mvn exec:java' to start the application.