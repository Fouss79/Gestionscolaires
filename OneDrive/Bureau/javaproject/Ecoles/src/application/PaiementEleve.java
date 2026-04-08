package application;



public class PaiementEleve {

    private String eleve;
    private String frais;
    private double montant;
    private String date;

    public PaiementEleve(String eleve,String frais,double montant,String date){
        this.eleve=eleve;
        this.frais=frais;
        this.montant=montant;
        this.date=date;
    }

    public String getEleve(){ return eleve; }

    public String getFrais(){ return frais; }

    public double getMontant(){ return montant; }

    public String getDate(){ return date; }
}