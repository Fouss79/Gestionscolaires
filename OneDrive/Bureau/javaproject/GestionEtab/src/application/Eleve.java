package application;

import java.time.LocalDate;

public class Eleve {

    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String adresse;
    private String numeroMatricule;
    private String telephone;
    private String classeNom;
    private String sexe;
    private String statutId;
    private Long anneeId; // 🔹 colonne BDD
    private String anneeScolaire; // 🔹 pour l'affichage dans le tableau

    // 🔹 Constructeurs
    public Eleve() {}

    public Eleve(String nom, String prenom, LocalDate dateNaissance, String adresse,
                 String telephone, String classeNom, String sexe, String statutId, Long anneeId) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.telephone = telephone;
        this.classeNom = classeNom;
        this.sexe = sexe;
        this.statutId = statutId;


        // Déterminer l'année scolaire à afficher
     

        // Génération du matricule unique
        this.numeroMatricule = genererMatricule(nom, prenom, dateNaissance, sexe);
    }

    // 🔹 Méthode pour convertir l'id en année scolaire
   

    // 🔹 Génération du matricule
    private String genererMatricule(String nom, String prenom, LocalDate dateNaissance, String sexe) {
        String partNom = (nom != null && nom.length() >= 3) ? nom.substring(0, 3).toUpperCase() : (nom != null ? nom.toUpperCase() : "XXX");
        String partPrenom = (prenom != null && prenom.length() >= 3) ? prenom.substring(0, 3).toUpperCase() : (prenom != null ? prenom.toUpperCase() : "XXX");
        String anneeNaissance = (dateNaissance != null) ? String.valueOf(dateNaissance.getYear()) : "0000";
        String parsexe = sexe != null && !sexe.isEmpty() ? sexe.substring(0, 1).toUpperCase() : "X";
        int random = (int) (Math.random() * 9000 + 1000);
        return "ELV-" + partNom + partPrenom + anneeNaissance + "-" + random + parsexe;
    }

    // 🔹 Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getClasseNom() { return classeNom; }
    public void setClasseNom(String classeNom) { this.classeNom = classeNom; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public String getStatutId() { return statutId; }
    public void setStatutId(String statutId) { this.statutId = statutId; }

    public String getNumeroMatricule() { return numeroMatricule; }
    public void setNumeroMatricule(String numeroMatricule) { this.numeroMatricule = numeroMatricule; }

    public Long getAnneeId() { return anneeId; }
    public void setAnneeId(Long anneeId) { 
        this.anneeId = anneeId; 
      // mettre à jour l'affichage
    }

    public String getAnneeScolaire() { return anneeScolaire; }
    public void setAnneeScolaire(String anneeScolaire) { this.anneeScolaire = anneeScolaire; }

    @Override
    public String toString() {
        return "Eleve{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", adresse='" + adresse + '\'' +
                ", telephone='" + telephone + '\'' +
                ", classeNom='" + classeNom + '\'' +
                ", sexe='" + sexe + '\'' +
                ", numeroMatricule='" + numeroMatricule + '\'' +
                ", statutId='" + statutId + '\'' +
                ", anneeId=" + anneeId +
                ", anneeScolaire='" + anneeScolaire + '\'' +
                '}';
    }
}
