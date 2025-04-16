import React, { useState } from "react";
import axios from "axios";

function App() {
  const [sourceType, setSourceType] = useState("ClickHouse");
  const [host, setHost] = useState("");
  const [port, setPort] = useState(8443);
  const [database, setDatabase] = useState("");
  const [user, setUser] = useState("");
  const [jwtToken, setJwtToken] = useState("");
  const [table, setTable] = useState("");
  const [csvPath, setCsvPath] = useState("");
  const [delimiter, setDelimiter] = useState(",");
  const [status, setStatus] = useState("Idle");
  const [result, setResult] = useState("");
  const [tables, setTables] = useState([]);
  const [availableColumns, setAvailableColumns] = useState([]);
  const [selectedColumns, setSelectedColumns] = useState([]);

  const fetchTables = async () => {
    const res = await axios.get("http://localhost:8080/api/ingest/tables", {
      params: { host, port, database, user, jwtToken }
    });
    setTables(res.data);
  };

  const fetchColumns = async (selectedTable) => {
    const res = await axios.get("http://localhost:8080/api/ingest/columns", {
      params: { host, port, database, user, jwtToken, table: selectedTable }
    });
    setAvailableColumns(res.data);
  };

  const handleColumnCheck = (col) => {
    setSelectedColumns((prev) =>
      prev.includes(col) ? prev.filter((c) => c !== col) : [...prev, col]
    );
  };

  const handleIngestCHtoCSV = async () => {
    setStatus("Ingesting ClickHouse → CSV...");
    try {
      const response = await axios.post("http://localhost:8080/api/ingest/clickhouse-to-csv", {
        host, port, database, user, jwtToken, table, columns: selectedColumns, csvPath, delimiter
      });
      setResult(response.data);
      setStatus("Completed");
    } catch (err) {
      setStatus("Error");
      setResult(err.response?.data || err.message);
    }
  };

  const handleIngestCSVtoCH = async () => {
    setStatus("Ingesting CSV → ClickHouse...");
    try {
      const response = await axios.post("http://localhost:8080/api/ingest/csv-to-clickhouse", {
        host, port, database, user, jwtToken, table, csvPath, delimiter
      });
      setResult(response.data);
      setStatus("Completed");
    } catch (err) {
      setStatus("Error");
      setResult(err.response?.data || err.message);
    }
  };

  return (
    <div className="p-6 max-w-3xl mx-auto">
      <h1 className="text-2xl font-bold mb-4">Data Ingestion Tool</h1>

      <select className="border p-2 mb-4 w-full" value={sourceType} onChange={(e) => setSourceType(e.target.value)}>
        <option>ClickHouse</option>
        <option>CSV</option>
      </select>

      <input className="border p-2 mb-2 w-full" placeholder="Host" value={host} onChange={(e) => setHost(e.target.value)} />
      <input className="border p-2 mb-2 w-full" placeholder="Port" type="number" value={port} onChange={(e) => setPort(e.target.value)} />
      <input className="border p-2 mb-2 w-full" placeholder="Database" value={database} onChange={(e) => setDatabase(e.target.value)} />
      <input className="border p-2 mb-2 w-full" placeholder="User" value={user} onChange={(e) => setUser(e.target.value)} />
      <input className="border p-2 mb-2 w-full" placeholder="JWT Token" value={jwtToken} onChange={(e) => setJwtToken(e.target.value)} />

      <button onClick={fetchTables} className="bg-gray-300 p-2 mb-2 w-full">Load Tables</button>
      <select className="border p-2 mb-2 w-full" value={table} onChange={(e) => {
        setTable(e.target.value);
        fetchColumns(e.target.value);
      }}>
        <option value="">Select Table</option>
        {tables.map((t) => <option key={t}>{t}</option>)}
      </select>

      <div className="mb-2">
        <strong>Select Columns</strong><br />
        {availableColumns.map((col) => (
          <label key={col} className="block">
            <input type="checkbox" checked={selectedColumns.includes(col)} onChange={() => handleColumnCheck(col)} />
            <span className="ml-2">{col}</span>
          </label>
        ))}
      </div>

      <input className="border p-2 mb-2 w-full" placeholder="CSV File Path" value={csvPath} onChange={(e) => setCsvPath(e.target.value)} />
      <input className="border p-2 mb-4 w-full" placeholder="Delimiter" value={delimiter} onChange={(e) => setDelimiter(e.target.value)} />

      <div className="space-x-4">
        <button className="bg-blue-600 text-white px-4 py-2 rounded" onClick={handleIngestCHtoCSV}>
          Ingest ClickHouse → CSV
        </button>
        <button className="bg-green-600 text-white px-4 py-2 rounded" onClick={handleIngestCSVtoCH}>
          Ingest CSV → ClickHouse
        </button>
      </div>

      <div className="mt-4">
        <strong>Status:</strong> {status}<br />
        <strong>Result:</strong> <pre>{result}</pre>
      </div>
    </div>
  );
}

export default App;
