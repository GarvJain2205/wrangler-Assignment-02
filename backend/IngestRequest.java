@RestController
@RequestMapping("/api/ingest")
public class IngestionController {

    @Autowired
    private ClickHouseService clickHouseService;

    @Autowired
    private CsvService csvService;

    @PostMapping("/clickhouse-to-csv")
    public ResponseEntity<String> exportToCsv(@RequestBody IngestRequest req) {
        try (Connection conn = clickHouseService.connectToClickHouse(
                req.getHost(), req.getPort(), req.getDatabase(), req.getUser(), req.getJwtToken())) {

            String sql = String.format("SELECT %s FROM %s", String.join(", ", req.getColumns()), req.getTable());
            List<String[]> result = new ArrayList<>();

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();

                String[] header = new String[cols];
                for (int i = 1; i <= cols; i++) header[i - 1] = meta.getColumnName(i);
                result.add(header);

                while (rs.next()) {
                    String[] row = new String[cols];
                    for (int i = 1; i <= cols; i++) row[i - 1] = rs.getString(i);
                    result.add(row);
                }
            }

            csvService.writeCsv(req.getCsvPath(), result, req.getDelimiter());

            return ResponseEntity.ok("Export complete. Rows: " + (result.size() - 1));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/csv-to-clickhouse")
    public ResponseEntity<String> csvToCH(@RequestBody IngestRequest req) {
        try (Connection conn = clickHouseService.connectToClickHouse(
                req.getHost(), req.getPort(), req.getDatabase(), req.getUser(), req.getJwtToken())) {

            List<String[]> csvData = csvService.readCsv(req.getCsvPath(), req.getDelimiter());
            if (csvData.isEmpty()) return ResponseEntity.badRequest().body("CSV is empty");

            String tableName = req.getTable();
            List<String> columnNames = Arrays.asList(csvData.get(0));

            String createTableSql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    columnNames.stream().map(col -> col + " String").collect(Collectors.joining(", ")) +
                    ") ENGINE = MergeTree() ORDER BY tuple()";

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSql);
            }

            String insertSql = "INSERT INTO " + tableName + " (" + String.join(",", columnNames) + ") VALUES ";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                for (int i = 1; i < csvData.size(); i++) {
                    for (int j = 0; j < columnNames.size(); j++) {
                        pstmt.setString(j + 1, csvData.get(i)[j]);
                    }
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            return ResponseEntity.ok("Imported rows: " + (csvData.size() - 1));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/tables")
    public ResponseEntity<List<String>> getTables(@RequestParam String host,
                                                  @RequestParam int port,
                                                  @RequestParam String database,
                                                  @RequestParam String user,
                                                  @RequestParam String jwtToken) {
        try {
            List<String> tables = clickHouseService.fetchTables(host, port, database, user, jwtToken);
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/columns")
    public ResponseEntity<List<String>> getColumns(@RequestParam String host,
                                                   @RequestParam int port,
                                                   @RequestParam String database,
                                                   @RequestParam String user,
                                                   @RequestParam String jwtToken,
                                                   @RequestParam String table) {
        try {
            List<String> columns = clickHouseService.fetchColumns(host, port, database, user, jwtToken, table);
            return ResponseEntity.ok(columns);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of("Error: " + e.getMessage()));
        }
    }
}
