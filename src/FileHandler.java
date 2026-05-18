import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FileHandler {

    private static final Path DATA_DIR = Paths.get("data");

    private static final Path USER_LOGIN_FILE = DATA_DIR.resolve("userLogin.csv");
    private static final Path SALON_FILE = DATA_DIR.resolve("salon.csv");
    private static final Path SERVICES_FILE = DATA_DIR.resolve("services.csv");
    private static final Path BOOKINGS_FILE = DATA_DIR.resolve("bookings.csv");

    private static final String USER_LOGIN_HEADER = "id,role,name,email,password,postalCode";
    private static final String SALON_HEADER = "barberId,salonName,description,postalCode";
    private static final String SERVICES_HEADER = "id,barberId,name,category,price,durationMinutes";
    private static final String BOOKINGS_HEADER = "id,customerId,barberId,serviceName,dateTime,status,totalPrice,cancellationFee";
    private static final String CUSTOMER_ROLE = "customer";
    private static final String BARBER_ROLE = "barber";

    public static void initializeDataFiles() {
        try {
            if (Files.notExists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }

            createFileIfMissing(USER_LOGIN_FILE, USER_LOGIN_HEADER);
            createFileIfMissing(SALON_FILE, SALON_HEADER);
            createFileIfMissing(SERVICES_FILE, SERVICES_HEADER);
            createFileIfMissing(BOOKINGS_FILE, BOOKINGS_HEADER);

        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke oprette data-mappen eller CSV-filerne.", e);
        }
    }

    private static void createFileIfMissing(Path file, String header) throws IOException {
        if (Files.notExists(file) || Files.size(file) == 0) {
            Files.write(
                    file,
                    Arrays.asList(header),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE
            );
        }
    }

    public static List<Customer> readCustomersFromLogin() {
        initializeDataFiles();

        List<Customer> customers = new ArrayList<>();

        for (UserLogin login : readUserLogins()) {
            if (CUSTOMER_ROLE.equalsIgnoreCase(login.getRole())) {
                customers.add(new Customer(
                        login.getId(),
                        login.getName(),
                        login.getEmail(),
                        login.getPassword(),
                        login.getPostalCode()
                ));
            }
        }

        return customers;
    }

    public static List<Barber> readBarbersFromLoginAndSalon() {
        initializeDataFiles();

        List<Barber> barbers = new ArrayList<>();
        Map<Integer, List<String>> salonsByBarberId = readSalonsByBarberId();

        for (UserLogin login : readUserLogins()) {
            if (BARBER_ROLE.equalsIgnoreCase(login.getRole())) {
                List<String> salon = salonsByBarberId.get(login.getId());
                String salonName = salon == null ? "" : salon.get(0);
                String description = salon == null ? "" : salon.get(1);
                String postalCode = salon == null ? login.getPostalCode() : salon.get(2);

                barbers.add(new Barber(
                        login.getId(),
                        login.getName(),
                        login.getEmail(),
                        login.getPassword(),
                        salonName,
                        description,
                        postalCode
                ));
            }
        }

        addServicesToBarbers(barbers);
        return barbers;
    }

    public static void appendCustomerLogin(Customer customer) {
        initializeDataFiles();
        appendUserLogin(customer.getId(), CUSTOMER_ROLE, customer.getName(), customer.getEmail(),
                customer.getPasswordHash(), customer.getPostalCode());
    }

    public static void appendBarberLogin(Barber barber) {
        initializeDataFiles();
        appendUserLogin(barber.getId(), BARBER_ROLE, barber.getName(), barber.getEmail(),
                barber.getPasswordHash(), barber.getPostalCode());
    }

    public static void appendSalon(Barber barber) {
        initializeDataFiles();
        String line = toCsvLine(
                String.valueOf(barber.getId()),
                barber.getSalonName(),
                barber.getDescription(),
                barber.getPostalCode()
        );

        appendLine(SALON_FILE, line);
    }

    public static void appendService(Barber barber, Service service) {
        initializeDataFiles();
        String line = toCsvLine(
                String.valueOf(getNextServiceId()),
                String.valueOf(barber.getId()),
                service.getName(),
                service.getCategory().name(),
                String.valueOf(service.getPrice()),
                String.valueOf(service.getDurationMinutes())
        );

        appendLine(SERVICES_FILE, line);
    }

    public static Booking createBooking(Customer customer, Barber barber, Service service, java.time.LocalDateTime dateTime) {
        initializeDataFiles();

        Booking booking = new Booking(
                getNextBookingId(),
                customer.getId(),
                barber,
                service,
                dateTime,
                BookingStatus.PENDING,
                service.getPrice()
        );

        appendBooking(booking);
        return booking;
    }

    public static List<Booking> readBookingsForCustomer(int customerId, List<Barber> barbers) {
        List<Booking> bookings = new ArrayList<>();

        for (Booking booking : readBookings(barbers)) {
            if (booking.getCustomerId() == customerId) {
                bookings.add(booking);
            }
        }

        return bookings;
    }

    public static List<Booking> readBookingsForBarber(int barberId, List<Barber> barbers) {
        List<Booking> bookings = new ArrayList<>();

        for (Booking booking : readBookings(barbers)) {
            if (booking.getBarber() != null && booking.getBarber().getId() == barberId) {
                bookings.add(booking);
            }
        }

        return bookings;
    }

    public static Customer findCustomerById(int customerId) {
        for (Customer customer : readCustomersFromLogin()) {
            if (customer.getId() == customerId) {
                return customer;
            }
        }

        return null;
    }

    public static boolean hasConfirmedBookingAt(int barberId, java.time.LocalDateTime dateTime, List<Barber> barbers) {
        for (Booking booking : readBookingsForBarber(barberId, barbers)) {
            if (booking.getStatus() == BookingStatus.CONFIRMED && booking.getDateTime().equals(dateTime)) {
                return true;
            }
        }

        return false;
    }

    public static void confirmBooking(int bookingId, List<Barber> barbers) {
        List<Booking> bookings = readBookings(barbers);

        for (Booking booking : bookings) {
            if (booking.getId() == bookingId) {
                booking.confirm();
                break;
            }
        }

        writeBookings(bookings);
    }

    public static double cancelBookingByCustomer(int bookingId, List<Barber> barbers) {
        List<Booking> bookings = readBookings(barbers);
        double cancellationFee = 0.0;

        for (Booking booking : bookings) {
            if (booking.getId() == bookingId) {
                cancellationFee = isSameDay(booking.getDateTime()) ? booking.getTotalPrice() : 0.0;
                booking.cancel(cancellationFee);
                break;
            }
        }

        writeBookings(bookings);
        return cancellationFee;
    }

    public static void cancelBookingByBarber(int bookingId, List<Barber> barbers) {
        List<Booking> bookings = readBookings(barbers);

        for (Booking booking : bookings) {
            if (booking.getId() == bookingId) {
                booking.cancel(0.0);
                break;
            }
        }

        writeBookings(bookings);
    }

    public static boolean emailExists(String email) {
        for (UserLogin login : readUserLogins()) {
            if (login.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    private static void appendUserLogin(int id, String role, String name, String email, String password, String postalCode) {
        String line = toCsvLine(
                String.valueOf(id),
                role,
                name,
                email,
                password,
                postalCode
        );

        appendLine(USER_LOGIN_FILE, line);
    }

    private static List<UserLogin> readUserLogins() {
        initializeDataFiles();

        List<UserLogin> userLogins = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(USER_LOGIN_FILE, StandardCharsets.UTF_8);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> values = parseCsvLine(line);

                if (values.size() >= 6) {
                    userLogins.add(new UserLogin(
                            Integer.parseInt(values.get(0).trim()),
                            values.get(1).trim(),
                            values.get(2).trim(),
                            values.get(3).trim(),
                            values.get(4).trim(),
                            values.get(5).trim()
                    ));
                } else if (values.size() >= 4) {
                    userLogins.add(new UserLogin(
                            i,
                            CUSTOMER_ROLE,
                            values.get(0).trim(),
                            values.get(1).trim(),
                            values.get(2).trim(),
                            values.get(3).trim()
                    ));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke læse userLogin.csv.", e);
        }

        return userLogins;
    }

    private static Map<Integer, List<String>> readSalonsByBarberId() {
        initializeDataFiles();

        Map<Integer, List<String>> salons = new HashMap<>();

        try {
            List<String> lines = Files.readAllLines(SALON_FILE, StandardCharsets.UTF_8);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> values = parseCsvLine(line);

                if (values.size() < 4) {
                    continue;
                }

                int barberId = Integer.parseInt(values.get(0).trim());
                List<String> salonValues = new ArrayList<>();
                salonValues.add(values.get(1).trim());
                salonValues.add(values.get(2).trim());
                salonValues.add(values.get(3).trim());
                salons.put(barberId, salonValues);
            }

        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke læse salon.csv.", e);
        }

        return salons;
    }

    private static void addServicesToBarbers(List<Barber> barbers) {
        try {
            List<String> lines = Files.readAllLines(SERVICES_FILE, StandardCharsets.UTF_8);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> values = parseCsvLine(line);

                if (values.size() < 6) {
                    continue;
                }

                int barberId = Integer.parseInt(values.get(1).trim());
                String name = values.get(2).trim();
                Category category = Category.fromString(values.get(3).trim());
                double price = Double.parseDouble(values.get(4).trim());
                int duration = Integer.parseInt(values.get(5).trim());

                for (Barber barber : barbers) {
                    if (barber.getId() == barberId) {
                        barber.addService(name, category, price, duration);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke læse services.csv.", e);
        }
    }

    private static int getNextServiceId() {
        int maxId = 0;

        try {
            List<String> lines = Files.readAllLines(SERVICES_FILE, StandardCharsets.UTF_8);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> values = parseCsvLine(line);

                if (!values.isEmpty()) {
                    maxId = Math.max(maxId, Integer.parseInt(values.get(0).trim()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke læse services.csv.", e);
        }

        return maxId + 1;
    }

    private static List<Booking> readBookings(List<Barber> barbers) {
        initializeDataFiles();
        List<Booking> bookings = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(BOOKINGS_FILE, StandardCharsets.UTF_8);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> values = parseCsvLine(line);

                if (values.size() < 7) {
                    continue;
                }

                int id = Integer.parseInt(values.get(0).trim());
                int customerId = Integer.parseInt(values.get(1).trim());
                int barberId = Integer.parseInt(values.get(2).trim());
                String serviceName = values.get(3).trim();
                java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(values.get(4).trim());
                BookingStatus status = BookingStatus.fromString(values.get(5).trim());
                double totalPrice = Double.parseDouble(values.get(6).trim());
                double cancellationFee = values.size() >= 8 ? Double.parseDouble(values.get(7).trim()) : 0.0;

                Barber barber = findBarberById(barbers, barberId);
                Service service = findServiceByName(barber, serviceName, totalPrice);
                bookings.add(new Booking(id, customerId, barber, service, dateTime, status, totalPrice, cancellationFee));
            }
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke læse bookings.csv.", e);
        }

        return bookings;
    }

    private static void appendBooking(Booking booking) {
        appendLine(BOOKINGS_FILE, toBookingCsvLine(booking));
    }

    private static void writeBookings(List<Booking> bookings) {
        List<String> lines = new ArrayList<>();
        lines.add(BOOKINGS_HEADER);

        for (Booking booking : bookings) {
            lines.add(toBookingCsvLine(booking));
        }

        writeLines(BOOKINGS_FILE, lines);
    }

    private static String toBookingCsvLine(Booking booking) {
        return toCsvLine(
                String.valueOf(booking.getId()),
                String.valueOf(booking.getCustomerId()),
                String.valueOf(booking.getBarber().getId()),
                booking.getService().getName(),
                booking.getDateTime().toString(),
                booking.getStatus().name(),
                String.valueOf(booking.getTotalPrice()),
                String.valueOf(booking.getCancellationFee())
        );
    }

    private static boolean isSameDay(java.time.LocalDateTime dateTime) {
        return dateTime.toLocalDate().equals(java.time.LocalDate.now());
    }

    private static int getNextBookingId() {
        int maxId = 0;

        try {
            List<String> lines = Files.readAllLines(BOOKINGS_FILE, StandardCharsets.UTF_8);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> values = parseCsvLine(line);

                if (!values.isEmpty()) {
                    maxId = Math.max(maxId, Integer.parseInt(values.get(0).trim()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke læse bookings.csv.", e);
        }

        return maxId + 1;
    }

    private static Barber findBarberById(List<Barber> barbers, int barberId) {
        for (Barber barber : barbers) {
            if (barber.getId() == barberId) {
                return barber;
            }
        }

        return null;
    }

    private static Service findServiceByName(Barber barber, String serviceName, double totalPrice) {
        if (barber != null) {
            for (Service service : barber.getServices()) {
                if (service.getName().equalsIgnoreCase(serviceName)) {
                    return service;
                }
            }
        }

        return new Service(serviceName, Category.FADE, totalPrice, 0);
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
