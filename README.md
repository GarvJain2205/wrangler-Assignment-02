# 🔄 ClickHouse ↔ CSV Ingestion Tool

A full-stack web application that allows users to perform **bidirectional data ingestion** between a ClickHouse database and flat files (CSV), with JWT-based authentication, column selection, and record count reporting.

## 📦 Project Structure

- `backend/`: Spring Boot API (Java) for ClickHouse & CSV handling
- `frontend/`: React UI with Axios integration and dynamic schema controls

---

## 🚀 Features

✅ ClickHouse → CSV with selected columns  
✅ CSV → ClickHouse (creates table & ingests data)  
✅ JWT authentication support  
✅ Table and column fetching  
✅ Clear status & record count display  
✅ Ready for Dockerization and further deployment

---

## 🔧 Setup Instructions

### Backend

```bash
cd backend
mvn spring-boot:run

Frontend:
cd frontend
npm install
npm start
Runs on http://localhost:3000
cd backend
mvn spring-boot:run


🧰 Libraries & Frameworks
🧠 Backend (Java):
Spring Boot: REST API framework

ClickHouse JDBC: Connects to ClickHouse via Java

OpenCSV: Reads/writes CSV files

Lombok (via @Data): Simplifies DTO creation (optional)

🎨 Frontend (React):
React (JSX + Hooks): UI framework

Axios: HTTP client for frontend-to-backend communication

TailwindCSS (optional): Utility-first CSS framework
