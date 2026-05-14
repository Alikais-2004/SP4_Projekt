import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Menu {

    private Scanner scanner = new Scanner(System.in);

    private List<Customer> customers = new ArrayList<>();
    private List<Barber> barbers = new ArrayList<>();

    private User currentUser = null;

    public void start() {
        loadBarbersFromFile();
        loadServicesFromFile();
        System.out.println("Velkommen til Frisørbooking!");
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = showLoginMenu();
            } else if (currentUser instanceof Customer) {
                showCustomerMenu();
            } else if (currentUser instanceof Barber) {
                showBarberMenu();
            }
        }
        System.out.println("På gensyn!");
    }

    private String readInput(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) {
                return null;
            }
            if (input.isEmpty()) {
                System.out.println("Feltet må ikke være tomt. Prøv igen.");
                continue;
            }
            return input;
        }
    }

    private boolean showLoginMenu() {
        System.out.println("\n--- LOGIN ---");
        System.out.println("1. Log ind");
        System.out.println("2. Opret kunde");
        System.out.println("3. Opret frisør");
        System.out.println("0. Afslut");
        System.out.print("Vælg: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": login(); break;
            case "2": registerCustomer(); break;
            case "3": registerBarber(); break;
            case "0": return false;
            default: System.out.println("Ugyldigt valg.");
        }
        return true;
    }

    private void registerCustomer() {
        String name = readInput("Navn");
        if (name == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }

        String email = readInput("Email");
        if (email == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }

        String password = readInput("Adgangskode");
        if (password == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }

        String postalCode = readInput("Postnummer");
        if (postalCode == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }

        Customer customer = Customer.register(name, email, password, postalCode);
        customers.add(customer);
        System.out.println("Kundekonto oprettet!");
    }

    private void registerBarber() {
        String name = readInput("Navn");
        if (name == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }

        String email = readInput("Email");
        if (email == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }

        String password = readInput("Adgangskode");
        if (password == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }

        String salonName = readInput("Salonnavn");
        if (salonName == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }

        String postalCode = readInput("Postnummer");
        if (postalCode == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }

        Barber barber = Barber.register(name, email, password, salonName, postalCode);
        barbers.add(barber);
        System.out.println("Frisørkonto oprettet!");
    }

    private void login() {
        String email = readInput("Email");
        if (email == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }

        String password = readInput("Adgangskode");
        if (password == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }

        for (Customer c : customers) {
            if (c.getEmail().equals(email) && c.login(password)) {
                currentUser = c;
                return;
            }
        }
        for (Barber b : barbers) {
            if (b.getEmail().equals(email) && b.login(password)) {
                currentUser = b;
                return;
            }
        }
        System.out.println("Ugyldigt login.");
    }

    private void showCustomerMenu() {
        System.out.println("\n--- KUNDE: " + currentUser.getName() + " ---");
        System.out.println("1. Se frisører");
        System.out.println("2. Book tid");
        System.out.println("3. Se mine bookinger");
        System.out.println("4. Skriv anmeldelse");
        System.out.println("9. Log ud");
        System.out.print("Vælg: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": showBarbers(); break;
            case "2": bookTime(); break;
            case "3": showMyBookings(); break;
            case "4": writeReview(); break;
            case "9": currentUser = null; break;
            default: System.out.println("Ugyldigt valg.");
        }
    }

    private void showBarberMenu() {
        System.out.println("\n--- FRISØR: " + currentUser.getName() + " ---");
        System.out.println("1. Rediger profil");
        System.out.println("2. Administrer ydelser");
        System.out.println("3. Angiv åbningstider");
        System.out.println("4. Se min kalender");
        System.out.println("5. Se anmeldelser");
        System.out.println("9. Log ud");
        System.out.print("Vælg: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": System.out.println("TODO: rediger profil"); break;
            case "2": System.out.println("TODO: administrer ydelser"); break;
            case "3": System.out.println("TODO: angiv åbningstider"); break;
            case "4": System.out.println("TODO: se kalender"); break;
            case "5": System.out.println("TODO: se anmeldelser"); break;
            case "9": currentUser = null; break;
            default: System.out.println("Ugyldigt valg.");
        }


    }


    private void loadBarbersFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/barbers.csv"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String email = parts[2];
                String passwordHash = parts[3];
                String salonName = parts[4];
                String description = parts[5];
                String postalCode = parts[6];
                Barber barber = new Barber(id, name, email, passwordHash, salonName, description, postalCode);
                barbers.add(barber);
            }
        } catch (IOException e) {
            System.out.println("Kunne ikke læse barbers.csv: " + e.getMessage());
        }
    }
    private void showBarbers() {
        System.out.println("\n--- FRISØRER ---");
        if (barbers.isEmpty()) {
            System.out.println("Ingen frisører fundet.");
            return;
        }
        for (int i = 0; i < barbers.size(); i++) {
            Barber b = barbers.get(i);
            System.out.println((i + 1) + ". " + b.getSalonName()
                    + " (" + b.getName() + ") - Postnr: " + b.getPostalCode()
                    + " - Rating: " + b.getAverageRating());
        }
    }
    private void showMyBookings() {
        Customer customer = (Customer) currentUser;
        List<Booking> bookings = customer.getBookings();

        System.out.println("\n--- MINE BOOKINGER ---");
        if (bookings.isEmpty()) {
            System.out.println("Du har ingen bookinger endnu.");
            return;
        }
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            System.out.println((i + 1) + ". " + b.getDateTime()
                    + " - Status: " + b.getStatus()
                    + " - Pris: " + b.getTotalPrice() + " kr");
        }
    }
    private void bookTime() {
        Customer customer = (Customer) currentUser;

        // Vælg frisør
        showBarbers();
        if (barbers.isEmpty()) return;

        String barberChoice = readInput("Vælg frisør (nummer)");
        if (barberChoice == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }
        int barberIndex = Integer.parseInt(barberChoice) - 1;
        if (barberIndex < 0 || barberIndex >= barbers.size()) {
            System.out.println("Ugyldigt valg.");
            return;
        }
        Barber barber = barbers.get(barberIndex);

        // Vælg ydelse
        List<Service> services = barber.getServices();
        if (services.isEmpty()) {
            System.out.println("Denne frisør har ingen ydelser.");
            return;
        }
        System.out.println("\n--- YDELSER ---");
        for (int i = 0; i < services.size(); i++) {
            Service s = services.get(i);
            System.out.println((i + 1) + ". " + s.getName()
                    + " - " + s.getPrice() + " kr - " + s.getDurationMinutes() + " min");
        }

        String serviceChoice = readInput("Vælg ydelse (nummer)");
        if (serviceChoice == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }
        int serviceIndex = Integer.parseInt(serviceChoice) - 1;
        if (serviceIndex < 0 || serviceIndex >= services.size()) {
            System.out.println("Ugyldigt valg.");
            return;
        }
        Service service = services.get(serviceIndex);

        // Indtast dato og tid
        String dateInput = readInput("Dato og tid (format: 2026-05-20 14:30)");
        if (dateInput == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }
        LocalDateTime dateTime = LocalDateTime.parse(dateInput.replace(" ", "T"));

        // Opret booking
        Booking booking = customer.bookTime(barber, service, dateTime);
        System.out.println("Booking oprettet! " + service.getName()
                + " hos " + barber.getSalonName() + " den " + dateTime);
    }
    private void writeReview() {
        Customer customer = (Customer) currentUser;
        List<Booking> bookings = customer.getBookings();

        if (bookings.isEmpty()) {
            System.out.println("Du har ingen bookinger at anmelde.");
            return;
        }

        System.out.println("\n--- VÆLG BOOKING AT ANMELDE ---");
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            System.out.println((i + 1) + ". " + b.getDateTime()
                    + " - Status: " + b.getStatus());
        }

        String bookingChoice = readInput("Vælg booking (nummer)");
        if (bookingChoice == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }
        int bookingIndex = Integer.parseInt(bookingChoice) - 1;
        if (bookingIndex < 0 || bookingIndex >= bookings.size()) {
            System.out.println("Ugyldigt valg.");
            return;
        }
        Booking booking = bookings.get(bookingIndex);

        if (!booking.canBeReviewed()) {
            System.out.println("Denne booking kan ikke anmeldes endnu.");
            return;
        }

        String ratingInput = readInput("Rating (1-5)");
        if (ratingInput == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }
        int rating = Integer.parseInt(ratingInput);
        if (rating < 1 || rating > 5) {
            System.out.println("Rating skal være mellem 1 og 5.");
            return;
        }

        String comment = readInput("Kommentar");
        if (comment == null) {
            System.out.println("Afbrudt — tilbage til menu.");
            return;
        }

        customer.writeReview(booking, rating, comment);
        System.out.println("Tak for din anmeldelse!");
    }
    private void loadServicesFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/services.csv"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int barberId = Integer.parseInt(parts[1]);
                String name = parts[2];
                Category category = Category.fromString(parts[3]);
                double price = Double.parseDouble(parts[4]);
                int duration = Integer.parseInt(parts[5]);

                for (Barber barber : barbers) {
                    if (barber.getId() == barberId) {
                        barber.addService(name, category, price, duration);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Kunne ikke læse services.csv: " + e.getMessage());
        }
    }
}