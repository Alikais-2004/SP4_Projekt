public class Service {

    private int id;
    private String name;
    private Category category;
    private double price;
    private int durationMinutes;

    public Service(String name, Category category, double price, int durationMinutes) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.durationMinutes = durationMinutes;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}