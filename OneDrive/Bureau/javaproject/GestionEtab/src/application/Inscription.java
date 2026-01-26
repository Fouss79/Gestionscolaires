package application;

import java.time.LocalDate;

public class Inscription {

    private long id;
    private long eleveId;
    private int classeId;
    private int anneeId;
    private LocalDate dateInscription;

    public Inscription(long eleveId, int classeId, int anneeId, LocalDate dateInscription) {
        this.eleveId = eleveId;
        this.classeId = classeId;
        this.anneeId = anneeId;
        this.dateInscription = dateInscription;
    }

    public Inscription(long id, long eleveId, int classeId, int anneeId, LocalDate dateInscription) {
        this.id = id;
        this.eleveId = eleveId;
        this.classeId = classeId;
        this.anneeId = anneeId;
        this.dateInscription = dateInscription;
    }

    // getters & setters
    public long getId() { return id; }
    public long getEleveId() { return eleveId; }
    public int getClasseId() { return classeId; }
    public int getAnneeId() { return anneeId; }
    public LocalDate getDateInscription() { return dateInscription; }

    public void setId(long id) { this.id = id; }
    public void setEleveId(long eleveId) { this.eleveId = eleveId; }
    public void setClasseId(int classeId) { this.classeId = classeId; }
    public void setAnneeId(int anneeId) { this.anneeId = anneeId; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
}
