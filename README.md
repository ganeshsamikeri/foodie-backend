# 🍔 Foodie Backend (Spring Boot)

A **secure, production-ready backend** for a Swiggy-like food ordering application built using **Spring Boot**.  
It provides **REST APIs**, **JWT-based authentication**, **role-based authorization**, and integrates with a **React frontend** deployed on Vercel.

---

## 🚀 Live Backend URL

🔗 **Backend Base URL:**  
👉 https://foodie-backend-ys7x.onrender.com

🔗 **Health Check:**  
👉 https://foodie-backend-ys7x.onrender.com/api/health

---

## 🛠 Tech Stack

- ☕ **Java 17**
- 🌱 **Spring Boot**
- 🔐 **Spring Security + JWT**
- 🗄 **MySQL**
- 📦 **Spring Data JPA (Hibernate)**
- 🌐 **REST APIs**
- ☁️ **Render Deployment**
- 🔄 **CORS Configuration for Vercel Frontend**

---

## ✨ Features

- 🔐 JWT Authentication (Login & Register)
- 👤 Role-based Authorization (USER / ADMIN)
- 🛒 Order Management
- 📦 My Orders API
- 🧑‍💼 Admin APIs (Order Status Update)
- 🩺 Health Check Endpoint
- 🌍 CORS enabled for frontend
- 🔒 Stateless Session Management

---

## 🔐 Authentication Flow

1. User logs in with email & password
2. Backend generates **JWT token**
3. Token includes:
   - Email (subject)
   - Role (USER / ADMIN)
4. Frontend sends token in headers

```http
Authorization: Bearer <JWT_TOKEN>

📂 Project Structure
backend/
├── config/
│   ├── SecurityConfig.java
│   ├── PasswordConfig.java
├── controller/
│   ├── AuthController.java
│   ├── OrderController.java
│   ├── AdminOrderController.java
│   ├── HealthController.java
├── dto/
├── model/
├── repository/
├── security/
│   ├── JwtFilter.java
│   ├── JwtUtil.java
├── FoodieBackendApplication.java
└── pom.xml

🌍 API Endpoints
🔓 Public APIs
Method	Endpoint	Description
GET	/	Home
GET	/api/health	Health check
POST	/api/auth/register	User registration
POST	/api/auth/login	User login
🔐 User APIs
Method	Endpoint	Description
GET	/api/orders/my-orders	Get logged-in user's orders
POST	/api/orders	Place order
🛡 Admin APIs
Method	Endpoint	Description
PUT	/api/admin/orders/{id}/status	Update order status
⚙️ Environment Variables

Set the following variables in Render Dashboard or .env file:

DB_URL=jdbc:mysql://<host>:<port>/<dbname>
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET=your_secret_key
PORT=8080

▶️ Run Locally
git clone https://github.com/ganeshamikeri/foodie-backend.git
cd foodie-backend
mvn clean install
mvn spring-boot:run

🔗 Frontend Repository

👉 https://github.com/ganeshamikeri/foodie-app

🔗 Live Frontend:
👉 https://foodie-app-navy.vercel.app

🩺 Health Check Example
✅ Application is healthy! Database connection successful.

🔒 Security Highlights

JWT validation via OncePerRequestFilter

Stateless sessions

Role-based route protection

CORS restricted to frontend domain

Passwords encrypted using BCrypt

👨‍💻 Author

Ganesh Gani
GitHub: https://github.com/ganeshamikeri

⭐ Notes

Backend and frontend are deployed separately

Designed following real-world backend architecture

Suitable for learning & portfolio projects

💡 This backend demonstrates secure authentication, authorization, and production deployment best practices.
