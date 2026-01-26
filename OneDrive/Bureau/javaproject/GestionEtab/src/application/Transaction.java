

package application;

import java.time.LocalDate;

public class Transaction{

    private int id;
    private int paiementId;
    private int enseignant_id;
    private String matricule;
    private double montant;
    private LocalDate date;
    private String modePaiement;
    private String commentaire;

    public Transaction() {
    }

    public Transaction(int id, int paiementId,int enseignant_id,String matricule, double montant, LocalDate date, String modePaiement, String commentaire) {
        this.id = id;
        this.paiementId = paiementId;
        this.montant = montant;
        this.date = date;
        this.modePaiement = modePaiement;
        this.commentaire = commentaire;
        this.enseignant_id = enseignant_id;
        this.matricule = matricule;
    }

    // Getters et setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPaiementId() {
        return paiementId;
    }

    public void setPaiementId(int paiementId) {
        this.paiementId = paiementId;
    }
    
    public int getEnseignant_id() {
        return enseignant_id;
    }

    public void setEnseignant_id(int enseignant_id) {
        this.enseignant_id = enseignant_id;
    }
  
    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule= matricule;
    }

    
    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    

    @Override
    public String toString() {
        return "Transaction{" +
                "montant=" + montant +
                ", date=" + date +
                '}';
 
    }}
