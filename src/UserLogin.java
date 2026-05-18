public class UserLogin {
    private int id;
    private String role;
    private String name;
    private String email;
    private String password;
    private String postalCode;

    public UserLogin(int id, String role, String name, String email, String password, String postalCode) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.email = email;
        this.password = password;
        this.postalCode = postalCode;
    }

    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPostalCode() {
        return postalCode;
    }
}
