package application;

import java.time.DayOfWeek;

public class EmploiDutemps {

    private int id;
    private DayOfWeek jour;
    private String heureDebut;
    private String heureFin;
    private String matiere;
    private String professeur;
    private String classe;

    // ✅ Année scolaire
    private int anneeId;
    private String anneeLibelle; // optionnel pour affichage

    public EmploiDutemps(int id, DayOfWeek jour, String heureDebut, String heureFin,
                         String matiere, String professeur, String classe,
                         int anneeId, String anneeLibelle) {

        this.id = id;
        this.jour = jour;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.matiere = matiere;
        this.professeur = professeur;
        this.classe = classe;
        this.anneeId = anneeId;
        this.anneeLibelle = anneeLibelle;
    }

    
    
    public String getJourFrancais() {
        switch (jour) {
            case MONDAY: return "Lundi";
            case TUESDAY: return "Mardi";
            case WEDNESDAY: return "Mercredi";
            case THURSDAY: return "Jeudi";
            case FRIDAY: return "Vendredi";
            case SATURDAY: return "Samedi";
            case SUNDAY: return "Dimanche";
            default: return "";
        }
    }
    // ===== Getters & Setters =====

    public int getId() { return id; }

    public DayOfWeek getJour() { return jour; }
    public void setJour(DayOfWeek jour) { this.jour = jour; }

    public String getHeureDebut() { return heureDebut; }
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }

    public String getHeureFin() { return heureFin; }
    public void setHeureFin(String heureFin) { this.heureFin = heureFin; }

    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }

    public String getProfesseur() { return professeur; }
    public void setProfesseur(String professeur) { this.professeur = professeur; }

    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    // ✅ Année

    public int getAnneeId() { return anneeId; }
    public void setAnneeId(int anneeId) { this.anneeId = anneeId; }

    public String getAnneeLibelle() { return anneeLibelle; }
    public void setAnneeLibelle(String anneeLibelle) { this.anneeLibelle = anneeLibelle; }
}
