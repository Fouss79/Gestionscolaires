package application;

public class Frais {

    private int id;
    private String motif;
    private String annee;
    private double montant;
    private String niveau;

    public Frais(int id, String motif, String annee, double montant,String niveau) {
        this.id = id;
        this.motif = motif;
        this.annee = annee;
        this.montant = montant;
        this.niveau=niveau;
    }

    public int getId() { return id; }
    public String getMotif() { return motif; }
    public String getAnnee() { return annee; }
    public double getMontant() { return montant; }
    public String getNiveau() { return niveau; }
    

}