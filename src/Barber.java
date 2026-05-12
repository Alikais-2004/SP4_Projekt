import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Barber {

    private String salonName;
    private String description;
    private String postalCode;

    private List<Service> services = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    public Barber(String barberName, String description, String postalCode){
        this.salonName = barberName;
        this.description = description;
        this.postalCode = postalCode;
    }

    public Barber register(String name, String email, String password, String salonName, String postalCode){
        // Implement registration logic here
        return new Barber(name, salonName, postalCode);
    }

    public void updateSalon(String salonName, String description, String postalCode){
        this.salonName = salonName;
        this.description = description;
        this.postalCode = postalCode;
    }

    public Service addService(String name, Category category, double price, int duration){
        Service service = new Service(name, category, price, duration);
        services.add(service);
        return new Service(name, category, price, duration);
    }

    public void editService(Service service){
        // Implement service editing logic here
    }

    public void removeService(Service service){
        services.remove(service);
    }

    public void setOpeningHours(Weekday weekday, LocalTime open, LocalTime close){
        //mangler en struktur til åbningstider
    }

    public List<LocalTime> getAvailableSlots(LocalDate date, Service service){
        return new ArrayList<>();
    }

    public List<Booking> getUpcomingBookings(){
        return bookings;
    }

    public List<Review> getReviews(){
        return reviews;
    }

    public double getAverageRating(){
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double totalRating = 0.0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }
        return totalRating / reviews.size();

    }

    public double distanceTo(String postalCode){
        // Implement distance calculation logic here
        return 0.0;
    }


    public String getBarberName() {
        return salonName;
    }

    public String getDescription() {
        return description;
    }

    public String getPostalCode() {
        return postalCode;
    }


}
