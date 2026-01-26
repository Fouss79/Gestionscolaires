package application;

public class Enseignant {
    private Long id;
    private String nom;
    private String prenom;
    private String dateNaissance;
    private String adresse;
    private String telephone;
    private Long matiereId;   // Id de la matière
    private String matiereNom; // Nom de la matière (via jointure)

    public Enseignant() {}

    public Enseignant(String nom, String prenom, String dateNaissance,
                      String adresse, String telephone, Long matiereId, String matiereNom) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.telephone = telephone;
        this.matiereId = matiereId;
        this.matiereNom = matiereNom;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public Long getMatiereId() { return matiereId; }
    public void setMatiereId(Long matiereId) { this.matiereId = matiereId; }

    public String getMatiereNom() { return matiereNom; }
    public void setMatiereNom(String matiereNom) { this.matiereNom = matiereNom; }
    

    // 🔹 Méthode utilitaire
    @Override
    public String toString() {
        return "Enseignant{" +
                "matiere=" + matiereNom +
                ", nom='" + nom +
                ", prenom='" + prenom +
                '}';
    }
    
    
    
    
    
    
}
