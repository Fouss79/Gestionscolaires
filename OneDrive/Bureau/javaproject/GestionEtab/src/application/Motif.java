package application;

public class Motif {
    private int id;
    private String nom;
    private int typeId;
    private int statutId;
    private boolean mensuel; // indique si le motif nécessite un paiement mensue

    public Motif() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }

    public int getStatutId() { return statutId; }
    public void setStatutId(int statutId) { this.statutId = statutId; }

    public boolean isMensuel() { return mensuel; }
    public void setMensuel(boolean mensuel) { this.mensuel = mensuel; }
}
