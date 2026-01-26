package application;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Emargement {

    private IntegerProperty id;
    private StringProperty jour;
    private StringProperty enseignantId;
    private StringProperty enseignant;
    private StringProperty classe;
    private StringProperty matiere;
    private ObjectProperty<LocalDateTime> dateHeure;
    private IntegerProperty duree;   // ✅ Changer en IntegerProperty
    private BooleanProperty present;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Constructeur par défaut
    public Emargement() {
        this.id = new SimpleIntegerProperty();
        this.jour= new SimpleStringProperty();
        this.enseignantId = new SimpleStringProperty();
        this.enseignant = new SimpleStringProperty();
        this.classe = new SimpleStringProperty();
        this.matiere = new SimpleStringProperty();
        this.dateHeure = new SimpleObjectProperty<>();
        this.duree = new SimpleIntegerProperty();   // ✅ IntegerProperty
        this.present = new SimpleBooleanProperty();
    }

    // Constructeur complet
    public Emargement(int id,String jour, String enseignantId,String enseignant, String classe, String matiere,
                      LocalDateTime dateHeure, int duree, boolean present) {
        this.id = new SimpleIntegerProperty(id);
        this.jour=new SimpleStringProperty(jour);
        this.enseignantId = new SimpleStringProperty(enseignantId);
        this.enseignant = new SimpleStringProperty(enseignant);
        this.classe = new SimpleStringProperty(classe);
        this.matiere = new SimpleStringProperty(matiere);
        this.dateHeure = new SimpleObjectProperty<>(dateHeure);
        this.duree = new SimpleIntegerProperty(duree);
        this.present = new SimpleBooleanProperty(present);
    }

    // --- Getters / Setters classiques ---
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    
    
    public String getEnseignantId() { return enseignantId.get(); }
    public void setEnseignantId(String enseignantId) { this.enseignantId.set(enseignantId); }
    
    public String getJour() { return jour.get(); }
    public void setJour(String jour) { this.jour.set(jour); }

    
    public String getEnseignant() { return enseignant.get(); }
    public void setEnseignant(String enseignant) { this.enseignant.set(enseignant); }

    public String getClasse() { return classe.get(); }
    public void setClasse(String classe) { this.classe.set(classe); }

    public String getMatiere() { return matiere.get(); }
    public void setMatiere(String matiere) { this.matiere.set(matiere); }

    public LocalDateTime getDateHeure() { return dateHeure.get(); }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure.set(dateHeure); }

    public int getDuree() { return duree.get(); }   // ✅ int
    public void setDuree(int duree) { this.duree.set(duree); }

    public boolean isPresent() { return present.get(); }
    public void setPresent(boolean present) { this.present.set(present); }

    // --- Properties pour JavaFX TableView ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty enseignantIdProperty() { return enseignantId; }
    public StringProperty jourProperty() { return jour; }
    public StringProperty enseignantProperty() { return enseignant; }
    public StringProperty classeProperty() { return classe; }
    public StringProperty matiereProperty() { return matiere; }
    public ObjectProperty<LocalDateTime> dateHeureProperty() { return dateHeure; }
    public IntegerProperty dureeProperty() { return duree; }   // ✅ IntegerProperty
    public BooleanProperty presentProperty() { return present; }

    // Pour affichage formaté dans le tableau
    public StringProperty dateProperty() {
        return new SimpleStringProperty(dateHeure.get() != null ? dateHeure.get().format(formatter) : "");
    }

    // ✅ Conversion durée -> texte lisible
    public String getDureeLisible() {
        int totalMinutes = getDuree();
        int heures = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        if (heures > 0) {
            return heures + "h" + (minutes > 0 ? minutes : "");
        } else {
            return minutes + " min";
        }
    }
    
    public StringProperty dureeLisibleProperty() {
        return new ReadOnlyStringWrapper(getDureeLisible());
    }

    
    @Override
    public String toString() {
        return "Emargement{" +
                "id=" + getId() + "jour" +
                ", enseignantId='" + getEnseignantId() + '\'' +
                ", classe='" + getClasse() + '\'' +
                ", matiere='" + getMatiere() + '\'' +
                ", dateHeure=" + getDateHeure() +
                ", duree=" + getDuree() +
                ", present=" + isPresent() +
                '}';
    }
}
