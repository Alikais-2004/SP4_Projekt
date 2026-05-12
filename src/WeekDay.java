import java.time.DayOfWeek;
import java.time.LocalDate;

public enum WeekDay {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static WeekDay fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Weekday cannot be null");
        }

        return WeekDay.valueOf(
                value.trim()
                        .toUpperCase()
        );
    }

    public static WeekDay fromLocalDate(LocalDate date) {
        return WeekDay.valueOf(date.getDayOfWeek().name());
    }

    public DayOfWeek toDayOfWeek() {
        return DayOfWeek.valueOf(this.name());
    }
}
