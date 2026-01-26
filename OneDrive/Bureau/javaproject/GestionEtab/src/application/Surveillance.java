package application;

import java.time.LocalDate;

public class Surveillance {
    private int id;
    private int enseignantId;
    private String nomEnseignant;
    private String prenomEnseignant;
    private LocalDate dateSurveillance;
    private String matiere;
    private String classe;
    private int nombreHeures;
    private double montantHeure;
    private double montantTotal;
    private String examen; // Ex: "Composition 1er trimestre"
    private String statut; // Ex: "Payé" ou "Non payé"

    public Surveillance() {}

    public Surveillance(int id, int enseignantId, String nomEnseignant, String prenomEnseignant,
                        LocalDate dateSurveillance, String matiere, String classe, int nombreHeures,
                        double montantHeure, double montantTotal, String examen, String statut) {
        this.id = id;
        this.enseignantId = enseignantId;
        this.nomEnseignant = nomEnseignant;
        this.prenomEnseignant = prenomEnseignant;
        this.dateSurveillance = dateSurveillance;
        this.matiere = matiere;
        this.classe = classe;
        this.nombreHeures = nombreHeures;
        this.montantHeure = montantHeure;
        this.montantTotal = montantTotal;
        this.examen = examen;
        this.statut = statut;
    }

    // --- Getters et Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEnseignantId() { return enseignantId; }
    public void setEnseignantId(int enseignantId) { this.enseignantId = enseignantId; }

    public String getNomEnseignant() { return nomEnseignant; }
    public void setNomEnseignant(String nomEnseignant) { this.nomEnseignant = nomEnseignant; }

    public String getPrenomEnseignant() { return prenomEnseignant; }
    public void setPrenomEnseignant(String prenomEnseignant) { this.prenomEnseignant = prenomEnseignant; }

    public LocalDate getDateSurveillance() { return dateSurveillance; }
    public void setDateSurveillance(LocalDate dateSurveillance) { this.dateSurveillance = dateSurveillance; }

    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }

    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    public int getNombreHeures() { return nombreHeures; }
    public void setNombreHeures(int nombreHeures) { this.nombreHeures = nombreHeures; }

    public double getMontantHeure() { return montantHeure; }
    public void setMontantHeure(double montantHeure) { this.montantHeure = montantHeure; }

    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }

    public String getExamen() { return examen; }
    public void setExamen(String examen) { this.examen = examen; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return nomEnseignant + " " + prenomEnseignant + " - " + matiere + " (" + classe + ")";
    }
}
