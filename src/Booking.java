
import java.time.LocalDateTime;

public class Booking {

    private int id;
    private LocalDateTime dateTime;

    private BookingStatus status;

    private double totalPrice;


    public Booking(int id, LocalDateTime dateTime, BookingStatus status, double totalPrice){

        this.id = id;
        this.dateTime = dateTime;
        this.status = status;
        this.totalPrice = totalPrice;


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



}
