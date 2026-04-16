# 💰 Expense Tracker Application (Full-Stack)

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot%203.5-brightgreen)
![Angular](https://img.shields.io/badge/Frontend-Angular%2018-red)
![Kafka](https://img.shields.io/badge/Messaging-Apache%20Kafka-black)

A high-performance, enterprise-grade personal finance management system. This application provides a seamless experience for tracking daily transactions, managing monthly budgets, and generating data-driven financial insights.

---

## 🏗️ System Architecture

The application is built on a decoupled **Client-Server Architecture** designed for scalability:

* **Frontend (Angular 18):** Utilizes **Standalone Components**, **SSR (Server-Side Rendering)** for SEO/Performance, and **RxJS** for reactive data handling.
* **Backend (Spring Boot 3.5):** A RESTful API built with **Java 21**, managing business logic, security, and integration with external services.
* **Asynchronous Processing (Kafka):** Heavy tasks like report generation and email notifications are offloaded to Kafka topics to ensure a lag-free UI.
* **Persistence (PostgreSQL):** Relational data management with optimized indexing for financial records.

---

## 🎯 Advanced Core Features

### 💎 Smart Dashboard & Analytics
* **KPI Metrics:** Instant visibility into Total Balance, Monthly Income vs. Expense, and Budget Utilization.
* **Data Visualization:** Real-time charts powered by `ng2-charts` showing category-wise breakdowns and spending trends.
* **Activity Heatmaps:** Track your financial consistency with a frequency heatmap of your transactions.

### 📑 Professional Reporting Engine
* **Synchronous Exports:** Generate on-the-fly **PDF** (iText7) or **Excel** (Apache POI) files with custom branding and formatted data tables.
* **Asynchronous Exports:** For large datasets, the system uses **Kafka** to process reports in the background and notifies the user via email once ready.

### 🛡️ Enterprise-Grade Security
* **Stateless Authentication:** Secure login using **JWT (JSON Web Tokens)** with a 7-day expiration and automatic refresh token logic.
* **Spring Security:** Comprehensive protection including BCrypt password hashing, CORS management, and role-based access control (RBAC).

---

## 🛠️ Technical Stack

### **Backend Components**
* **Framework:** Spring Boot 3.5.7 (Java 21)
* **Security:** Spring Security & JWT
* **ORM:** Spring Data JPA (Hibernate)
* **Messaging:** Apache Kafka
* **Mailing:** Spring Boot Starter Mail

### **Frontend Components**
* **Framework:** Angular 18.2.0 (Standalone)
* **Styling:** Bootstrap 5.3 & Bootstrap Icons
* **Charts:** Chart.js (via ng2-charts)
* **State Management:** RxJS Observables

---

## 🚀 Getting Started

### Prerequisites
* **JDK 21** & **Maven 3.8+**
* **Node.js 18+** & **npm**
* **PostgreSQL** instance
* **Apache Kafka** (optional for async reports)

### Installation Steps

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/sundram1612/Expense-Tracker-Application.git](https://github.com/sundram1612/Expense-Tracker-Application.git)
    ```

2.  **Backend Configuration:**
    Navigate to `ExpenseTracker-backend/src/main/resources/` and create an `application.properties` file. You will need to define:
    * `spring.datasource.url/username/password`
    * `jwt.secretKey` (Min. 32 characters)
    * `spring.mail.*` credentials
    * `spring.kafka.bootstrap-servers`

3.  **Run Backend:**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

4.  **Run Frontend:**
    ```bash
    cd ExpenseTracker-frontend
    npm install
    npm start
    ```

---

## 📋 API Architecture Overview

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/auth/**` | POST | Registration, Login, and Password Resets |
| `/api/expenses` | GET/POST | Transaction CRUD operations |
| `/api/expenses/dashboard` | GET | Aggregated metrics for charts |
| `/api/expenses/report/pdf` | GET | Generate formatted PDF reports |
| `/api/kafka/report/async` | POST | Request background report via Kafka |

---

## 🔄 Business Logic Flow: Add Expense
1.  **Client:** Submits form via Angular Standalone component.
2.  **Auth:** `authGuard` verifies valid JWT in the header.
3.  **Controller:** Receives DTO and maps to the `Expense` Entity.
4.  **Service:** Validates the amount against the user's `MonthlyBudget`.
5.  **Kafka:** (Optional) Produces a notification event for the user.
6.  **Response:** Returns updated financial state to refresh the UI charts instantly.

---

## 🤝 Contributing
Contributions are welcome! Please fork the repository and use a feature branch. Pull requests are reviewed regularly.

**Developed with ❤️ by [sundram1612](https://github.com/sundram1612)**
