package application;


public class Utilisateur {

    private int id;
    private String username;
    private String password;
    private Role role;

    public Utilisateur(int id, String username, String password, Role role){
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId(){ return id; }
    public String getUsername(){ return username; }
    public String getPassword(){ return password; }
    public Role getRole(){ return role; }
}
