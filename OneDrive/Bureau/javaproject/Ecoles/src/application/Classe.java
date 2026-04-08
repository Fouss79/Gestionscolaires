package application;

public class Classe {

    private int id;
    private String niveau; // Nom du niveau
    private String serie;  // Nom de la série (peut être null)
    private String groupe; // Nom du groupe
    private int effectif;
    private int nbFille;
    private int nbGarçon;

    // 🔹 Constructeur complet
    public Classe(int id, String niveau, String serie, String groupe) {
        this.id = id;
        this.niveau = niveau;
        this.serie = serie;
        this.groupe = groupe;
        this.effectif = 0;
        this.nbFille = 0;
        this.nbGarçon = 0;
    }

    // 🔹 Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public String getGroupe() { return groupe; }
    public void setGroupe(String groupe) { this.groupe = groupe; }

    public int getEffectif() { return effectif; }
    public void setEffectif(int effectif) { this.effectif = effectif; }

    public int getNbFille() { return nbFille; }
    public void setNbFille(int nbFille) { this.nbFille = nbFille; }

    public int getNbGarcons() { return nbGarçon; }
    public void setNbGarçon(int nbGarçon) { this.nbGarçon = nbGarçon; }

    // 🔹 Pour l'affichage complet dans TableView
    public String getNom() {
        if (serie != null && !serie.isBlank()) {
            return niveau + " " + serie + " " + groupe;
        } else {
            return niveau + " " + groupe;
        }
    }
}