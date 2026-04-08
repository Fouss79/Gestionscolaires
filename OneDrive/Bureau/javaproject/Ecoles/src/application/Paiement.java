package application;

public class Paiement {

    private int id;
    private String nom;
    private String prenom;
    private String motif;
    private String type;
    private String mois;
    private String annee;
   

    private double montantTotal;
    private double montantPaye;
    private double reste;

    public Paiement(int id, String nom, String prenom, String motif, String type, String mois, String annee,
            double montantTotal, double montantPaye, double reste) {

this.id = id;
this.nom = nom;
this.prenom = prenom;
this.motif = motif;
this.type = type;
this.mois = mois;
this.annee = annee;  // <-- initialisation ajoutée

this.montantTotal = montantTotal;
this.montantPaye = montantPaye;
this.reste = reste;
}

    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getMotif() { return motif; }
    public String getType() { return type; }
    public String getMois() { return mois; }
    public String getAnnee() { return annee; }

    public double getMontantTotal() { return montantTotal; }
    public double getMontantPaye() { return montantPaye; }
    public double getReste() { return reste; }

    public String getStatut() {
        return reste == 0 ? "Soldé" : "Non soldé";
    }
}