package application;

public class Enseignement {
    private int id;
    private String enseignant;
    private String classe;
    private String matiere;
    private String coeff;
    private String annee;

    public Enseignement() {}

    public Enseignement(int id,String classe, String matiere, String coeff,String annee) {
        this.id = id;
      
        this.classe = classe;
        this.matiere = matiere;
        this.coeff = coeff;
        this.annee=annee;
    }

    // 🔹 Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEnseignant() { return enseignant; }
    public void setEnseignant(String enseignant) { this.enseignant = enseignant; }
    
    public String getAnnee() { return annee; }
    public void setAnnee(String annee) { this.annee = annee; }

    
    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }

    public String getCoeff() { return coeff; }
    public void setCoeff(String coeff) { this.coeff = coeff; }

    @Override
    public String toString() {
        return "Enseignement{" +
                "id=" + id +
                ", enseignant='" + enseignant + '\'' +
                ", classe='" + classe + '\'' +
                ", matiere='" + matiere + '\'' +
                ", coeff='" + coeff + '\'' +
                '}';
    }
}
