package application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Paiement {

    private int id;
    private String matricule;
    private String nom;
    private String prenom;
    private String classe;
    private double montantTotal;
    private double montantPaye;
    private double reste;
    private LocalDate dateCreation;
    private String motif; 
    private String type;    
    private List<Transaction> transactions;

    // Constructeur pour un nouveau paiement
    public Paiement(int id ,String matricule, String nom, String prenom, String classe,
            double montantTotal,double montantPaye, double reste,LocalDate dateCreation, String motif, String type) {
this.id = id;
this.matricule = matricule;
this.nom = nom;
this.prenom = prenom;
this.classe = classe;
this.montantTotal = montantTotal;
this.montantPaye = montantPaye;
this.reste = reste;
this.dateCreation = dateCreation;
this.motif = motif;
this.type = type;
this.transactions = new ArrayList<>();
}


    // Constructeur complet (souvent utilisé pour reconstruire depuis la BDD)
  
    // --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(int montantTotal) { this.montantTotal = montantTotal; }

    public double getMontantPaye() { return montantPaye; }
    public void setMontantPaye(int montantPaye) { this.montantPaye = montantPaye; }

    public double getReste() { return reste; }
    public void setReste(int reste) { this.reste = reste; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    // --- Ajouter une transaction ---
    public void ajouterTransaction(Transaction transaction) {
        transactions.add(transaction);
        montantPaye += transaction.getMontant();
        reste = montantTotal - montantPaye;
    }

    @Override
    public String toString() {
        return "Paiement{" +
                "id=" + id +
                ", matricule='" + matricule + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", classe='" + classe + '\'' +
                ", montantTotal=" + montantTotal +
                ", montantPaye=" + montantPaye +
                ", reste=" + reste +
                ", dateCreation=" + dateCreation +
                ", motif=" + motif +
                ", type=" + type +
                ", transactions=" + (transactions != null ? transactions.size() : 0) +
                '}';
    }
}
