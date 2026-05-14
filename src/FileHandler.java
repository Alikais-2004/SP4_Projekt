import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileHandler {

    private static final Path DATA_DIR = Paths.get("data");

    private static final Path CUSTOMERS_FILE = DATA_DIR.resolve("customers.csv");
    private static final Path BARBERS_FILE = DATA_DIR.resolve("barbers.csv");

    private static final String CUSTOMER_HEADER = "id,name,email,passwordHash,postalCode";
    private static final String BARBER_HEADER = "id,name,email,passwordHash,salonName,description,postalCode";

    public static void initializeDataFiles() {
        try {
            if (Files.notExists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }

            createFileIfMissing(CUSTOMERS_FILE, CUSTOMER_HEADER);
            createFileIfMissing(BARBERS_FILE, BARBER_HEADER);

        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke oprette data-mappen eller CSV-filerne.", e);
        }
    }

    private static void createFileIfMissing(Path file, String header) throws IOException {
        if (Files.notExists(file)) {
            Files.write(
                    file,
                    Arrays.asList(header),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE
            );
        }
    }

    public static List<Customer> readCustomers() {
        initializeDataFiles();

        List<Customer> customers = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(CUSTOMERS_FILE, StandardCharsets.UTF_8);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> values = parseCsvLine(line);

                if (values.size() < 5) {
                    continue;
                }

                int id = Integer.parseInt(values.get(0));
                String name = values.get(1);
                String email = values.get(2);
                String passwordHash = values.get(3);
                String postalCode = values.get(4);

                Customer customer = new Customer(id, name, email, passwordHash, postalCode);
                customers.add(customer);
            }

        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke læse customers.csv.", e);
        }

        return customers;
    }

    public static void writeCustomers(List<Customer> customers) {
        initializeDataFiles();

        List<String> lines = new ArrayList<>();
        lines.add(CUSTOMER_HEADER);

        for (Customer customer : customers) {
            lines.add(toCsvLine(
                    String.valueOf(customer.getId()),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getPasswordHash(),
                    customer.getPostalCode()
            ));
        }

        writeLines(CUSTOMERS_FILE, lines);
    }

    public static void appendCustomer(Customer customer) {
        initializeDataFiles();

        String line = toCsvLine(
                String.valueOf(customer.getId()),
                customer.getName(),
                customer.getEmail(),
                customer.getPasswordHash(),
                customer.getPostalCode()
        );

        appendLine(CUSTOMERS_FILE, line);
    }

    public static List<Barber> readBarbers() {
        initializeDataFiles();

        List<Barber> barbers = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(BARBERS_FILE, StandardCharsets.UTF_8);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> values = parseCsvLine(line);

                if (values.size() < 7) {
                    continue;
                }

                int id = Integer.parseInt(values.get(0));
                String name = values.get(1);
                String email = values.get(2);
                String passwordHash = values.get(3);
                String salonName = values.get(4);
                String description = values.get(5);
                String postalCode = values.get(6);

                Barber barber = new Barber(id, name, email, passwordHash, salonName, description, postalCode);
                barbers.add(barber);
            }

        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke læse barbers.csv.", e);
        }

        return barbers;
    }

    public static void writeBarbers(List<Barber> barbers) {
        initializeDataFiles();

        List<String> lines = new ArrayList<>();
        lines.add(BARBER_HEADER);

        for (Barber barber : barbers) {
            lines.add(toCsvLine(
                    String.valueOf(barber.getId()),
                    barber.getName(),
                    barber.getEmail(),
                    barber.getPasswordHash(),
                    barber.getSalonName(),
                    barber.getDescription(),
                    barber.getPostalCode()
            ));
        }

        writeLines(BARBERS_FILE, lines);
    }

    public static void appendBarber(Barber barber) {
        initializeDataFiles();

        String line = toCsvLine(
                String.valueOf(barber.getId()),
                barber.getName(),
                barber.getEmail(),
                barber.getPasswordHash(),
                barber.getSalonName(),
                barber.getDescription(),
                barber.getPostalCode()
        );

        appendLine(BARBERS_FILE, line);
    }

    private static void writeLines(Path file, List<String> lines) {
        try {
            Files.write(
                    file,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke skrive til filen: " + file, e);
        }
    }

    private static void appendLine(Path file, String line) {
        try {
            Files.write(
                    file,
                    Arrays.asList(line),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke tilføje linje til filen: " + file, e);
        }
    }

    private static String toCsvLine(String... values) {
        List<String> escapedValues = new ArrayList<>();

        for (String value : values) {
            escapedValues.add(escapeCsvValue(value));
        }

        return String.join(",", escapedValues);
    }

    private static String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }

        boolean mustBeQuoted =
                value.contains(",") ||
                        value.contains("\"") ||
                        value.contains("\n") ||
                        value.contains("\r");

        String escaped = value.replace("\"", "\"\"");

        if (mustBeQuoted) {
            return "\"" + escaped + "\"";
        }

        return escaped;
    }

    private static List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();

        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);

            if (currentChar == '"') {
                if (insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentValue.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (currentChar == ',' && !insideQuotes) {
                values.add(currentValue.toString());
                currentValue.setLength(0);
            } else {
                currentValue.append(currentChar);
            }
        }

        values.add(currentValue.toString());

        return values;
    }
}
