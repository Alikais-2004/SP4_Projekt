public class BookingView {
    private Booking booking;
    private Customer customer;

    public BookingView(Booking booking, Customer customer) {
        this.booking = booking;
        this.customer = customer;
    }

    public Booking getBooking() {
        return booking;
    }

    @Override
    public String toString() {
        String customerText = customer == null
                ? "Unknown customer"
                : customer.getName() + " - " + customer.getEmail();
        return booking.getDisplayDateTime() + " - " +
                customerText + " - " +
                booking.getService().getName() + " - " +
                booking.getStatus();
    }
}
