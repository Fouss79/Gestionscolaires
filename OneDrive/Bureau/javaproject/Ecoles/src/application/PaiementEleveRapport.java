package application;

public class PaiementEleveRapport {

    private String nom;
    private String prenom;
    private String frais;
    private double total;
    private double paye;
    private double reste;
    private String statut;
    private String classe;

    public PaiementEleveRapport(String nom,String prenom,String classe,String frais,double total,double paye,double reste,String statut){
        this.nom=nom;
        this.prenom=prenom;
        this.frais=frais;
        this.total=total;
        this.paye=paye;
        this.reste=reste;
        this.statut=statut;
        this.classe=classe;
    }

    public String getNom(){return nom;}
    public String getPrenom(){return prenom;}
    public String getClasse(){return classe;}
    public String getFrais(){return frais;}
    public double getTotal(){return total;}
    public double getPaye(){return paye;}
    public double getReste(){return reste;}
    public String getStatut(){return statut;}
}