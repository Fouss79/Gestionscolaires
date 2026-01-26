package application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaiementEleve {
    private int id;                 // ID du paiement (clé primaire en BDD)
    private String nomEleve;        // Nom de l’élève
    private String prenomEleve;     // Prénom de l’élève
    private String matricule;       // Numéro matricule
    private String classe;          // Classe de l’élève
    private int montantTotal;       // Montant total à payer
    private int montantPaye;        // Montant déjà payé (cumul)
    private int reste;              // Montant restant
    private LocalDate dateCreation; // Date de création du paiement

    // Liste des transactions associées
    private List<TransactionEleve> transactions;

    // --- Constructeur ---
    public PaiementEleve(int id, String nomEleve, String prenomEleve, String matricule,
                         String classe, int montantTotal) {
        this.id = id;
        this.nomEleve = nomEleve;
        this.prenomEleve = prenomEleve;
        this.matricule = matricule;
        this.classe = classe;
        this.montantTotal = montantTotal;
        this.montantPaye = 0;
        this.reste = montantTotal;
        this.dateCreation = LocalDate.now();
        this.transactions = new ArrayList<>();
    }

    // --- Ajouter une transaction ---
    public void ajouterTransaction(TransactionEleve transaction) {
        transactions.add(transaction);
        recalculer(); // recalculer après chaque ajout
    }

    // --- Recalculer cumul et reste ---
    public void recalculer() {
        montantPaye = transactions.stream().mapToInt(TransactionEleve::getMontant).sum();
        reste = montantTotal - montantPaye;
    }

    // --- Getters & Setters ---
    public int getId() { return id; }
    public String getNomEleve() { return nomEleve; }
    public String getPrenomEleve() { return prenomEleve; }
    public String getMatricule() { return matricule; }
    public String getClasse() { return classe; }
    public int getMontantTotal() { return montantTotal; }
    public int getMontantPaye() { return montantPaye; }
    public int getReste() { return reste; }
    public LocalDate getDateCreation() { return dateCreation; }
    public List<TransactionEleve> getTransactions() { return transactions; }

    @Override
    public String toString() {
        return "PaiementEleve{" +
                "id=" + id +
                ", nom='" + nomEleve + '\'' +
                ", prenom='" + prenomEleve + '\'' +
                ", matricule='" + matricule + '\'' +
                ", classe='" + classe + '\'' +
                ", montantTotal=" + montantTotal +
                ", montantPaye=" + montantPaye +
                ", reste=" + reste +
                ", dateCreation=" + dateCreation +
                ", transactions=" + transactions +
                '}';
    }
}
