package application;
public class TransactionEleve {

    private int id;
    private String eleve;
    private double montant;
    private String mode;
    private String date;

    public TransactionEleve(int id,String eleve,double montant,String mode,String date){
        this.id=id;
        this.eleve=eleve;
        this.montant=montant;
        this.mode=mode;
        this.date=date;
    }

    public int getId(){return id;}
    public String getEleve(){return eleve;}
    public double getMontant(){return montant;}
    public String getMode(){return mode;}
    public String getDate(){return date;}
}