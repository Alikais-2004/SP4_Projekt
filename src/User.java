public abstract class User {


    protected int id;
    protected String name;
    protected String email;
    protected String passwordHash;



    public abstract boolean login(String password);

    public abstract void logOut();

    public abstract void updateProfile(String name, String email);





}
