package application;

public class Motif {

    private int id;
    private String nom;
    private int typeId;
    private String typeNom;

    public Motif(int id, String nom, int typeId, String typeNom) {
        this.id = id;
        this.nom = nom;
        this.typeId = typeId;
        this.typeNom = typeNom;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public int getTypeId() { return typeId; }
    public String getTypeNom() { return typeNom; }
}