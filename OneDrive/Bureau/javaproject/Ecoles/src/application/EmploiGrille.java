package application;
public class EmploiGrille {

    private String heure;
    private String lundi;
    private String mardi;
    private String mercredi;
    private String jeudi;
    private String vendredi;
    private String samedi;
    private String classe;

    // ✅ CONSTRUCTEUR
    public EmploiGrille(String heure) {
        this.heure = heure;
    }

    // ✅ GETTERS (OBLIGATOIRE)
    
    
    

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }
    public String getHeure() {
        return heure;
    }

    public String getLundi() {
        return lundi;
    }

    public String getMardi() {
        return mardi;
    }

    public String getMercredi() {
        return mercredi;
    }

    public String getJeudi() {
        return jeudi;
    }

    public String getVendredi() {
        return vendredi;
    }

    public String getSamedi() {
        return samedi;
    }

    // ✅ SETTERS
    public void setLundi(String lundi) {
        this.lundi = lundi;
    }

    public void setMardi(String mardi) {
        this.mardi = mardi;
    }

    public void setMercredi(String mercredi) {
        this.mercredi = mercredi;
    }

    public void setJeudi(String jeudi) {
        this.jeudi = jeudi;
    }

    public void setVendredi(String vendredi) {
        this.vendredi = vendredi;
    }

    public void setSamedi(String samedi) {
        this.samedi = samedi;
    }
}