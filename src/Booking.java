
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Booking {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private int id;
    private int customerId;
    private LocalDateTime dateTime;
    private Barber barber;
    private Service service;
    private BookingStatus status;
    private Review review;
    private double totalPrice;
    private double cancellationFee;


    public Booking(int id, LocalDateTime dateTime, BookingStatus status, double totalPrice) {
        this.id = id;
        this.dateTime = dateTime;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public Booking(int id, int customerId, Barber barber, Service service,
                   LocalDateTime dateTime, BookingStatus status, double totalPrice) {
        this(id, customerId, barber, service, dateTime, status, totalPrice, 0.0);
    }

    public Booking(int id, int customerId, Barber barber, Service service,
                   LocalDateTime dateTime, BookingStatus status, double totalPrice, double cancellationFee) {
        this.id = id;
        this.customerId = customerId;
        this.barber = barber;
        this.service = service;
        this.dateTime = dateTime;
        this.status = status;
        this.totalPrice = totalPrice;
        this.cancellationFee = cancellationFee;
    }

    public Booking(Barber barber, Service service, LocalDateTime dateTime) {
        this.barber = barber;
        this.service = service;
        this.dateTime = dateTime;
        this.status = BookingStatus.PENDING;
        this.totalPrice = service.getPrice();
    }


    public void complete(){
        this.status = BookingStatus.COMPLETED;
    }

    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
    }

    public void cancel(double cancellationFee) {
        this.status = BookingStatus.CANCELLED;
        this.cancellationFee = cancellationFee;
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

    public int getCustomerId() {
        return customerId;
    }

    public LocalDateTime getDateTime(){
        return dateTime;
    }

    public Barber getBarber() {
        return barber;
    }

    public Service getService() {
        return service;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public double getTotalPrice(){
        return totalPrice;
    }

    public double getCancellationFee() {
        return cancellationFee;
    }

    public String getDisplayDateTime() {
        return DISPLAY_FORMAT.format(dateTime);
    }


    public void attachReview(Review review) {
        this.review = review;
    }

    @Override
    public String toString() {
        String serviceName = service == null ? "Unknown service" : service.getName();
        String barberName = barber == null ? "Unknown barber" : barber.getName();
        String feeText = cancellationFee > 0 ? " - cancellation fee " + cancellationFee + " kr" : "";
        return DISPLAY_FORMAT.format(dateTime) + " - " + serviceName + " with " + barberName + " - " + status + feeText;
    }
}
