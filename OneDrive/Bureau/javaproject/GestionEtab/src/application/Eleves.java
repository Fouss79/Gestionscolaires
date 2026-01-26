package application;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Eleves {

    private final StringProperty numeroMatricule = new SimpleStringProperty();
    private final StringProperty nom = new SimpleStringProperty();
    private final StringProperty prenom = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dateNaissance = new SimpleObjectProperty<>();

    private final StringProperty adresse = new SimpleStringProperty();
    private final StringProperty telephone = new SimpleStringProperty();
    private final StringProperty sexe = new SimpleStringProperty();
    private final StringProperty classe = new SimpleStringProperty();
    private final StringProperty annee = new SimpleStringProperty();
    private final StringProperty statut = new SimpleStringProperty();

    // ✅ Constructeur
    public Eleves(String nom, String prenom, LocalDate dateNaissance,
                 String adresse, String telephone, String sexe) {

        this.nom.set(nom);
        this.prenom.set(prenom);
        this.dateNaissance.set(dateNaissance);
        this.adresse.set(adresse);
        this.telephone.set(telephone);
        this.sexe.set(sexe);
    }

    // ===== getters & setters normaux =====
    public String getNumeroMatricule() { return numeroMatricule.get(); }
    public void setNumeroMatricule(String value) { numeroMatricule.set(value); }

    public String getNom() { return nom.get(); }
    public void setNom(String value) { nom.set(value); }

    public String getPrenom() { return prenom.get(); }
    public void setPrenom(String value) { prenom.set(value); }
    
    public LocalDate getDateNaissance() { return dateNaissance.get(); }
    public void setDateNaissance(LocalDate value) { dateNaissance.set(value); }
    public ObjectProperty<LocalDate> dateNaissanceProperty() { return dateNaissance; }

    
    public String getAdresse() { return adresse.get(); }
    public void setAdresse(String value) { adresse.set(value); }
    
    public String getTelephone() { return telephone.get(); }
    public void setTelephone(String value) { telephone.set(value); }

    public String getSexe() { return sexe.get(); }
    public void setSexe(String value) { sexe.set(value); }


    public String getClasse() { return classe.get(); }
    public void setClasse(String value) { classe.set(value); }

    public String getAnnee() { return annee.get(); }
    public void setAnnee(String value) { annee.set(value); }

    public String getStatut() { return statut.get(); }
    public void setStatut(String value) { statut.set(value); }

    // ===== methods Property() pour TableView =====
    public StringProperty numeroMatriculeProperty() { return numeroMatricule; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty prenomProperty() { return prenom; }
    public StringProperty classeProperty() { return classe; }
    public StringProperty anneeProperty() { return annee; }
    public StringProperty statutProperty() { return statut; }
}
