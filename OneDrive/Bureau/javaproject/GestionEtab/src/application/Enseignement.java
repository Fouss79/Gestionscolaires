package application;

public class Enseignement {
    private int id;
    private String enseignant;
    private String classe;
    private String matiere;
    private String coeff;
    private int annee_id;

    public Enseignement() {}

    public Enseignement(int id, String enseignant, String classe, String matiere, String coeff,int annee_id) {
        this.id = id;
        this.enseignant = enseignant;
        this.classe = classe;
        this.matiere = matiere;
        this.coeff = coeff;
        this.annee_id=annee_id;
    }

    // 🔹 Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEnseignant() { return enseignant; }
    public void setEnseignant(String enseignant) { this.enseignant = enseignant; }
    
    public int getAnnee_id() { return annee_id; }
    public void setAnnee_id(int annee_id) { this.annee_id = annee_id; }

    
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
