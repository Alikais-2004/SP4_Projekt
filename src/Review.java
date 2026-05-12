import java.time.LocalDateTime;

public class Review {

    private int id;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;



    public Review(int id, int rating, String comment, LocalDateTime createdAt){

        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;

    }

    public int getId(){
        return id;
    }

    public int getRating(){
        return rating;
    }

    public String getComment(){
        return comment;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
}
