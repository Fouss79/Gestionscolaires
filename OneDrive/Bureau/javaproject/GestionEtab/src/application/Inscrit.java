package application;
public class Inscrit {
    private String matricule;
    private String nom;
    private String prenom;
    private String sexe;
    private String classe;
    private String annee; // <-- nouveau

    public Inscrit(String matricule, String nom, String prenom, String sexe, String classe, String annee) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.classe = classe;
        this.annee = annee; // <-- nouveau
    }

    // getters
    public String getMatricule() { return matricule; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getSexe() { return sexe; }
    public String getClasse() { return classe; }
    public String getAnnee() { return annee; } // <-- nouveau
}
