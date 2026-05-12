
import java.time.LocalDateTime;

public class Booking {

    private int id;
    private LocalDateTime dateTime;
    private Barber barber;
    private Service service;
    private BookingStatus status;
    private Review review;
    private double totalPrice;


    public Booking(int id, LocalDateTime dateTime, BookingStatus status, double totalPrice){

        this.id = id;
        this.dateTime = dateTime;
        this.status = status;
        this.totalPrice = totalPrice;


    }

    public Booking(Barber barber, Service service, LocalDateTime dateTime) {
        this.barber = barber;
        this.service = service;
        this.dateTime = dateTime;
        this.status = BookingStatus.CONFIRMED;
        this.totalPrice = service.getPrice();
    }


    public void complete(){
        this.status = BookingStatus.COMPLETED;
    }

    public boolean isInPast(){
        return dateTime.isBefore(LocalDateTime.now());
    }


    public boolean canBeReviewed() {
        return isInPast() && status != BookingStatus.CANCELLED;

    }


    public int getId(){
        return id;
    }
    public LocalDateTime getDateTime(){
        return dateTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public double getTotalPrice(){
        return totalPrice;
    }


    public void attachReview(Review review) {
        this.review = review;
    }
}
