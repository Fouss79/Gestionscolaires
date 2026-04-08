package application;

public class Tarif {

    private int id;
    private String motif;
    private String annee;
    private double montant;

    public Tarif(int id, String motif, String annee, double montant) {
        this.id = id;
        this.motif = motif;
        this.annee = annee;
        this.montant = montant;
    }

    public int getId() { return id; }
    public String getMotif() { return motif; }
    public String getAnnee() { return annee; }
    public double getMontant() { return montant; }
}