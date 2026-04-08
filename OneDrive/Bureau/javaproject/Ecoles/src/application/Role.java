package application;

public class Role {

    private int id;
    private String nom;

    public Role(int id, String nom){
        this.id = id;
        this.nom = nom;
    }

    public int getId(){
        return id;
    }

    public String getNom(){
        return nom;
    }

    @Override
    public String toString(){
        return nom; // pour affichage dans ComboBox
    }
}
