@Service
public class ClickHouseService {

    public Connection connectToClickHouse(String host, int port, String db, String user, String jwtToken) throws SQLException {
        String url = String.format("jdbc:clickhouse://%s:%d/%s", host, port, db);

        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", jwtToken); // JWT is passed as password
        props.setProperty("ssl", "true");

        return DriverManager.getConnection(url, props);
    }

    public List<String> getTableNames(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        String query = "SHOW TABLES";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }

        return tables;
    }

    public List<String> getColumnNames(Connection conn, String table) throws SQLException {
        List<String> columns = new ArrayList<>();
        String query = "DESCRIBE TABLE " + table;

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                columns.add(rs.getString("name"));
            }
        }

        return columns;
    }

    public List<String> fetchTables(String host, int port, String db, String user, String jwtToken) throws SQLException {
        try (Connection conn = connectToClickHouse(host, port, db, user, jwtToken)) {
            return getTableNames(conn);
        }
    }

    public List<String> fetchColumns(String host, int port, String db, String user, String jwtToken, String table) throws SQLException {
        try (Connection conn = connectToClickHouse(host, port, db, user, jwtToken)) {
            return getColumnNames(conn, table);
        }
    }
}
