package application;



public class AnneeScolaires {
    private int id;
    private String libelle;

    public AnneeScolaires(int id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public int getId() { return id; }
    public String getLibelle() { return libelle; }

    @Override
    public String toString() {
        return libelle; // affiché dans ComboBox
    }
}

