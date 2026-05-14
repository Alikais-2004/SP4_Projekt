import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private Scanner scanner = new Scanner(System.in);

    private List<Customer> customers = new ArrayList<>();
    private List<Barber> barbers = new ArrayList<>();

    private User currentUser = null;

    public Menu() {
        FileHandler.initializeDataFiles();
        customers = FileHandler.readCustomers();
        barbers = FileHandler.readBarbers();
    }

    // === Hovedløkke ===
    public void start() {
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

    // === Menu når ingen er logget ind ===
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
        System.out.print("Navn: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Adgangskode: ");
        String password = scanner.nextLine();
        System.out.print("Postnummer: ");
        String postalCode = scanner.nextLine();

        Customer customer = Customer.register(name, email, password, postalCode);

        customers.add(customer);
        FileHandler.appendCustomer(customer);

        System.out.println("Kundekonto oprettet!");
    }

    private void registerBarber() {
        System.out.print("Navn: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Adgangskode: ");
        String password = scanner.nextLine();
        System.out.print("Salonnavn: ");
        String salonName = scanner.nextLine();
        System.out.print("Postnummer: ");
        String postalCode = scanner.nextLine();

        Barber barber = Barber.register(name, email, password, salonName, postalCode);

        barbers.add(barber);
        FileHandler.appendBarber(barber);

        System.out.println("Frisørkonto oprettet!");
    }

    private void login() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Adgangskode: ");
        String password = scanner.nextLine();

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

    // === Kunde-menu ===
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
            case "1": System.out.println("TODO: vis frisører"); break;
            case "2": System.out.println("TODO: book tid"); break;
            case "3": System.out.println("TODO: vis bookinger"); break;
            case "4": System.out.println("TODO: skriv anmeldelse"); break;
            case "9": currentUser = null; break;
            default: System.out.println("Ugyldigt valg.");
        }
    }

    // === Frisør-menu ===
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
}