package application;

public class Statut {
    private int id;
    private String nom;

    public Statut(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Statut() {
		// TODO Auto-generated constructor stub
	}

	public int getId() { return id; }
    public String getNom() { return nom; }

    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }

    @Override
    public String toString() {
        return nom;
    }
}
