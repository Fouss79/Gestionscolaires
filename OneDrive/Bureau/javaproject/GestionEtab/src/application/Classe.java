package application;

public class Classe {
    private String nom;
    private String niveau;
    private int effectif;
    private int nbGarçon;
    private int nbFille;

    // Constructeur
    public Classe(String nom, String niveau) {
        this.nom = nom;
        this.niveau = niveau;
        this.effectif = 0;
        this.nbGarçon= 0;
        this.nbFille = 0;
    }

    // Getters & Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getNiveau() { return niveau; }
    public void setNiveau(String serie) { this.niveau = niveau; }

    public int getEffectif() { return effectif; }
    public void setEffectif(int effectif) { this.effectif = effectif; }

    public int getNbGarçon() { return nbGarçon; }
    public void setNbGarçon(int nbGarçon) { this.nbGarçon = nbGarçon; }
    

    public int getNbFille() { return nbFille; }
    public void setNbFille(int nbFille) { this.nbFille = nbFille; }

}

