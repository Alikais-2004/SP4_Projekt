import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Customer extends User {

    String postalCode;
    private List<Booking>bookings;

    public Customer(int id, String name, String email, String passwordHash, String postalCode) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.postalCode = postalCode;
        this.bookings = new ArrayList<>();
    }

    public static Customer register(String name, String email, String password, String postalCode) {
        int generatedId = (int)(Math.random()*1000000);
        return new Customer(generatedId, name, email, password, postalCode);

    }

    public List<Barber>getBarbers (Category category) {
        return new ArrayList<>();
    }

    Booking bookTime(Barber barber, Service service, LocalDateTime dateTime){
        Booking booking = new Booking(barber, service, dateTime);
        bookings.add(booking);
        return booking;
    }

    Review writeReview(Booking booking, int rating, String comment){
        if (!booking.canBeReviewed()){
            throw new RuntimeException("You can't review your booking");
        }
        int generatedId = (int)(Math.random()*1000000);
        Review review = new Review(generatedId, rating, comment, LocalDateTime.now());
        booking.attachReview(review);
        return review;
    }

    List<Booking>getBookings(){
        return bookings;
    }

    public String getPostalCode() {
        return postalCode;
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

    @Override
    public boolean login(String password) {
        if (this.passwordHash.equals(password)) {
            System.out.println(name + " logged in.");
            return true;
        }
        return false;
    }

}
