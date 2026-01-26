package application;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaiementEnseignant {

    private int id;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String nomEnseignant;
    private String prenomEnseignant;
    private String matiere;
    private String motif;
    private String type;
    private double montantTotal;
    private double montantPaye;
    private double reste;
    private int totalHeure;
    private LocalDate datePaiement;
    private String mois;

    // Liste des transactions associées
    private List<Transaction> transactions = new ArrayList<>();

    // === Constructeurs ===
    public PaiementEnseignant() {}

    public PaiementEnseignant(int id, LocalDate date, LocalDate date2, String nomEnseignant,
                              String prenomEnseignant, String matiere, String motif, String type,
                              double montantTotal2, double montantPaye2, double reste2, double d, LocalDate datePaiement, String mois) {
        this.id = id;
        this.dateDebut = date;
        this.dateFin = date2;
        this.nomEnseignant = nomEnseignant;
        this.prenomEnseignant = prenomEnseignant;
        this.matiere = matiere;
        this.motif = motif;
        this.type = type;
        this.montantTotal = montantTotal2;
        this.montantPaye = montantPaye2;
        this.reste = reste2;
        this.totalHeure=totalHeure;
        this.datePaiement = datePaiement;
        this.mois=mois;
    }

    public PaiementEnseignant(int int1, Date valueOf, Date valueOf2, String text, String text2, String value,
			String value2, String text3, double montantTotal2, double montantPaye2, double reste2, double double1,
			LocalDate now) {
		// TODO Auto-generated constructor stub
	}

	// === Getters et Setters ===
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate date) { this.dateDebut = date; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getNomEnseignant() { return nomEnseignant; }
    public void setNomEnseignant(String nomEnseignant) { this.nomEnseignant = nomEnseignant; }

    public String getPrenomEnseignant() { return prenomEnseignant; }
    public void setPrenomEnseignant(String prenomEnseignant) { this.prenomEnseignant = prenomEnseignant; }

    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(int montantTotal) { this.montantTotal = montantTotal; }

    public double getMontantPaye() { return montantPaye; }
    public void setMontantPaye(int montantPaye) { this.montantPaye = montantPaye; }

    public double getReste() { return reste; }
    public void setReste(double reste2) { this.reste = reste2; }
    
    public int getTotalHeure() { return totalHeure; }
    public void setTotalHeure(int TotalHeure) { this.totalHeure = totalHeure; }


    public LocalDate getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; }
    
    public String getMois() { return mois; }
    public void setMois(String mois) { this.mois = mois; }


    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    // === Méthodes utilitaires ===
    public void calculerReste() {
        this.reste = this.montantTotal - this.montantPaye;
    }

    public void ajouterTransaction(Transaction t) {
        if (t != null) {
            transactions.add(t);
            this.montantPaye += t.getMontant();
            calculerReste();
        }
    }

    @Override
    public String toString() {
        return "PaiementEnseignant{" +
                "id=" + id +
                ", nom='" + nomEnseignant + '\'' +
                ", prenom='" + prenomEnseignant + '\'' +
                ", matiere='" + matiere + '\'' +
                ", motif='" + motif + '\'' +
                ", type='" + type + '\'' +
                ", montantTotal=" + montantTotal +
                ", montantPaye=" + montantPaye +
                ", reste=" + reste +
                ", datePaiement=" + datePaiement +
                ", transactions=" + transactions +
                '}';
    }
}
