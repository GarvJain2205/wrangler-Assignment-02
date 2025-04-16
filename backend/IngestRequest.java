@Data
public class IngestRequest {
    private String host;
    private int port;
    private String database;
    private String user;
    private String jwtToken;
    private String table;
    private List<String> columns;
    private String csvPath;
    private char delimiter;
}
