# 📥 Zeotap Data Ingestion Assignment

This project showcases a simple data ingestion pipeline using **FastAPI** as the backend and **an HTML-based web interface** as the frontend. The system supports uploading a CSV file and exporting data from a ClickHouse table — simulating real-world data ingestion and extraction tasks.

---

## 📁 Project Structure

zeotap-assignment/ ├── main.py # FastAPI app with upload/export logic ├── templates/ │ └── index.html # Web UI for interacting with API ├── uploads/ # Directory to store uploaded/exported files ├── requirements.txt # Python dependencies └── README.md

## ⚙️ Setup Instructions

### ✅ Requirements

- Python 3.8+
- Pip or any Python package manager

### 📦 Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/zeotap-assignment.git
   cd zeotap-assignment

  Install required packages:

2. Install required packages:
 pip install -r requirements.txt

3.Start the FastAPI development server:
 uvicorn main:app --reload

4.Now open your browser and navigate to:
http://localhost:8000

💡 Features
📤 Upload CSV → Insert into ClickHouse

📥 Export from ClickHouse → Download CSV

✅ Supports specifying host, user, password, DB, and table

🎨 Easy HTML-based UI, no JS/React overhead
