import java.time.LocalTime;

public class OpeningHours {

    private int id;
    private WeekDay weekday;
    private LocalTime openTime;
    private LocalTime closeTime;

    public OpeningHours(WeekDay weekday, LocalTime openTime, LocalTime closeTime) {
        this.weekday = weekday;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public WeekDay getWeekday() {
        return weekday;
    }
    public void setWeekday(WeekDay weekday) {
        this.weekday = weekday;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }
    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }
    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }
}
