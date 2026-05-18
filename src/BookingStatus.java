public enum BookingStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED;

    public boolean canBeReviewed() {
        return this == COMPLETED;
    }

    public boolean isActive() {
        return this == PENDING || this == CONFIRMED;
    }

    public static BookingStatus fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("BookingStatus cannot be null");
        }

        return BookingStatus.valueOf(
                value.trim()
                        .toUpperCase()
        );
    }
}
