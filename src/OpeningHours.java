import java.time.LocalTime;

public class OpeningHours {

    private int id;
    private Weekday weekday;
    private LocalTime openTime;
    private LocalTime closeTime;

    public OpeningHours(Weekday weekday, LocalTime openTime, LocalTime closeTime) {
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

    public Weekday getWeekday() {
        return weekday;
    }
    public void setWeekday(Weekday weekday) {
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
