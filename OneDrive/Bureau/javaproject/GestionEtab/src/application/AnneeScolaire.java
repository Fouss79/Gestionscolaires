package application;

import java.time.LocalDate;

public class AnneeScolaire {

    private long id;
    private String libelle;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean active;

    public AnneeScolaire(long id, String libelle, LocalDate dateDebut, LocalDate dateFin, boolean active) {
        this.id = id;
        this.libelle = libelle;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.active = active;
    }

    public long getId() { return id; }
    public String getLibelle() { return libelle; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
