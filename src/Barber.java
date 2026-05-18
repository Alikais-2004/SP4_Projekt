import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Barber extends User {

    private String salonName;
    private String description;
    private String postalCode;

    private List<Service> services = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    public Barber(int id, String name, String email, String passwordHash,
                  String salonName, String description, String postalCode) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salonName = salonName;
        this.description = description;
        this.postalCode = postalCode;
    }

    public static Barber register(String name, String email, String password,
                                  String salonName, String postalCode) {
        int generatedId = (int)(Math.random() * 1000000);
        return new Barber(generatedId, name, email, password, salonName, "", postalCode);
    }

    public void updateSalon(String salonName, String description, String postalCode) {
        this.salonName = salonName;
        this.description = description;
        this.postalCode = postalCode;
    }

    public Service addService(String name, Category category, double price, int duration) {
        Service service = new Service(name, category, price, duration);
        services.add(service);
        return service;
    }

    public void editService(Service service) {
        // Implement service editing logic here
    }

    public void removeService(Service service) {
        services.remove(service);
    }

    public void setOpeningHours(WeekDay weekday, LocalTime open, LocalTime close) {
        // mangler en struktur til åbningstider
    }

    public List<LocalTime> getAvailableSlots(LocalDate date, Service service) {
        return new ArrayList<>();
    }

    public List<Booking> getUpcomingBookings() {
        return bookings;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public double getAverageRating() {
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double totalRating = 0.0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }
        return totalRating / reviews.size();
    }

    public double distanceTo(String postalCode) {
        // Implement distance calculation logic here
        return 0.0;
    }

    // === Metoder arvet fra User ===

    @Override
    public boolean login(String password) {
        if (this.passwordHash.equals(password)) {
            System.out.println(name + " logged in.");
            return true;
        }
        return false;
    }

    @Override
    public void logOut() {
        System.out.println(name + " has been logged out");
    }

    @Override
    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // === Getters ===

    public String getSalonName() {
        return salonName;
    }

    public String getDescription() {
        return description;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public List<Service> getServices() {
        return services;
    }

    @Override
    public String toString() {
        if (salonName == null || salonName.trim().isEmpty()) {
            return name;
        }
        return salonName + " - " + name;
    }
}
