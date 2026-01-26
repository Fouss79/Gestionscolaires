package application;

public class OperationFinanciere {
    private int id;
    private String type; // B = dépense, L = recette
    private String libelle;
    private double montant;
    private String dateOperation;

    private Integer idEleve;       // null si non concerné
    private Integer idEnseignant;  // null si non concerné
    private Integer idPaiement;    // null si non concerné

    public OperationFinanciere() {}

    public OperationFinanciere(int id, String type, String libelle, double montant, String dateOperation,
                               Integer idEleve, Integer idEnseignant, Integer idPaiement) {
        this.id = id;
        this.type = type;
        this.libelle = libelle;
        this.montant = montant;
        this.dateOperation = dateOperation;
        this.idEleve = idEleve;
        this.idEnseignant = idEnseignant;
        this.idPaiement = idPaiement;
    }

    // getters et setters...
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(String dateOperation) {
        this.dateOperation = dateOperation;
    }

    public Integer getIdEleve() {
        return idEleve;
    }

    public void setIdEleve(Integer idEleve) {
        this.idEleve = idEleve;
    }

    public Integer getIdEnseignant() {
        return idEnseignant;
    }

    public void setIdEnseignant(Integer idEnseignant) {
        this.idEnseignant = idEnseignant;
    }

    public Integer getIdPaiement() {
        return idPaiement;
    }

    public void setIdPaiement(Integer idPaiement) {
        this.idPaiement = idPaiement;
    }

}
