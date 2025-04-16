@Service
public class CsvService {

    public List<String[]> readCsv(String path, char delimiter) throws IOException {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(path))
                .withCSVParser(new CSVParserBuilder().withSeparator(delimiter).build())
                .build()) {
            return reader.readAll();
        }
    }

    public void writeCsv(String path, List<String[]> data, char delimiter) throws IOException {
        try (CSVWriter writer = new CSVWriterBuilder(new FileWriter(path))
                .withSeparator(delimiter)
                .build()) {
            writer.writeAll(data);
        }
    }
}
