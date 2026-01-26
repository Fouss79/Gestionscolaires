
package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class PaiementEleveController {

    @FXML private TextField txtMatricule;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtMontantTotal;
    @FXML private TextField txtMontantPaye;
    @FXML private TextField txtReste;
    @FXML private TextField txtType;
    @FXML private ComboBox<String> comboClasse;
    @FXML private ComboBox<String> comboEleve;
    @FXML private ComboBox<String> comboMotif;
    @FXML private ComboBox<String> cbMois;
    @FXML private ComboBox<String> comboAnnee;


    @FXML private TableView<Transaction> tableTransactions;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, Integer> colMontant;

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private ObservableList<String> mois = FXCollections.observableArrayList(
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    );

    // Connexion JDBC
    private final String URL = "jdbc:mysql://localhost:3306/GestionEcole";
    private final String USER = "root";
    private final String PASSWORD = "";

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate().toString()));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        tableTransactions.setItems(transactions);

        txtMontantPaye.textProperty().addListener((obs, oldVal, newVal) -> calculerReste());
        txtMontantTotal.textProperty().addListener((obs, oldVal, newVal) -> calculerReste());

        cbMois.setDisable(true); // Désactivé par défaut
        chargerClasses();
        chargerAnnees();
        // ⚡ Charger les motifs uniquement après sélection d'un élève
        comboEleve.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                chargerMotif();
            }
        });
      

    }


    // ✅ Méthode générique pour changer de page
    private void changerScene(ActionEvent event, String fxml, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1283, 657));
            stage.setTitle(titre);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Navigation
    @FXML private void retourAccueil() { System.out.println("Retour au Tableau de Bord"); }
    @FXML private void openClasse(ActionEvent event) { changerScene(event, "ClasseView.fxml", "Gestion des Classes"); }
    @FXML private void openEleve(ActionEvent event) { changerScene(event, "EleveView.fxml", "Gestion des Élèves"); }
    @FXML private void openMatiere(ActionEvent event) { changerScene(event, "MatiereView.fxml", "Gestion des Matières"); }
    @FXML private void openNote(ActionEvent event) { changerScene(event, "NoteView.fxml", "Gestion des Notes"); }
    @FXML private void openEnseignant(ActionEvent event) { changerScene(event, "EnseignantView.fxml", "Gestion des Enseignants"); }
    @FXML private void openTransaction(ActionEvent event) { changerScene(event, "TransactionView.fxml", "Gestion des Transactions"); }
    @FXML private void openBulletin(ActionEvent event) { changerScene(event, "BulletinView.fxml", "Gestion des Bulletins"); }
    @FXML private void openEmploidutemps(ActionEvent event) { changerScene(event, "EmploiTempsView.fxml", "Gestion des Emplois du Temps"); }
    @FXML private void openComptabilite(ActionEvent event) { changerScene(event, "ComptabiliteView.fxml", "Gestion des Finances"); }
    @FXML private void openEnseignants(ActionEvent event) { changerScene(event, "PaiementEnseignant.fxml", "Paiement Enseignants"); }
    @FXML private void openEleve1(ActionEvent event) { changerScene(event, "PaiementEleveForm.fxml", "Paiement Élèves"); }

    // 🔹 Calcul du reste
    private void calculerReste() {
        try {
            int total = Integer.parseInt(txtMontantTotal.getText());
            int paye = Integer.parseInt(txtMontantPaye.getText());
            txtReste.setText(String.valueOf(total - paye));
        } catch (NumberFormatException e) {
            txtReste.setText("");
        }
    }
    // Methode pour charger les anneescolaires depuis la table anneescolaire
    private void chargerAnnees() {
        comboAnnee.getItems().clear();

        String sql = "SELECT id, libelle FROM anneescolaire ORDER BY libelle DESC";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                comboAnnee.getItems().add(
                    rs.getInt("id") + " - " + rs.getString("libelle")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 🔹 Charger les classes depuis la BDD
    private void chargerClasses() {
        String sql = "SELECT id, nom,niveau FROM classe";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                comboClasse.getItems().add(rs.getInt("id") + " - " + rs.getString("nom")+"-"+rs.getString("niveau"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerMotif() {
        comboMotif.getItems().clear();
        String valeurEleve = comboEleve.getValue();
        if (valeurEleve == null) return; // sécurité
        

        String statutID = valeurEleve.split(" - ")[2].trim();

        String sql = "SELECT nom , mensuel FROM motif WHERE statut_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statutID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                	String nom = rs.getString("nom");
                	boolean mensuel = rs.getBoolean("mensuel");
                	
                	String statutMensuel = mensuel ? "Mensuel" : "Non mensuel";
                	
                    comboMotif.getItems().add(nom + " - " + statutMensuel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void handleMoisSelected(ActionEvent event) {

    }
    @FXML
    private void handleClasseSelected() {

        String selectedClasse = comboClasse.getValue();
        String selectedAnnee = comboAnnee.getValue();

        if (selectedClasse == null || selectedAnnee == null) return;

        comboEleve.getItems().clear();

        String idClasse = selectedClasse.split(" - ")[0];
        String idAnnee = selectedAnnee.split(" - ")[0];

        String sql = """
            SELECT e.numero_matricule, e.nom, e.prenom, e.statutId
            FROM eleve e
            JOIN inscription i ON i.eleve_id = e.id
            WHERE i.classe_id = ? AND i.annee_id = ?
        """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idClasse);
            ps.setString(2, idAnnee);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                comboEleve.getItems().add(
                    rs.getString("numero_matricule") + " - " +
                    rs.getString("nom") + " " +
                    rs.getString("prenom") + " - " +
                    rs.getString("statutId")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnneeSelected() {

        String selected = comboAnnee.getValue();
        if (selected == null) return;

        String idAnnee = selected.split(" - ")[0];

        comboClasse.getItems().clear();

        String sql = """
            SELECT DISTINCT c.id, c.nom, c.niveau
            FROM classe c
            JOIN inscription i ON i.classe_id = c.id
            WHERE i.annee_id = ?
        """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idAnnee);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                comboClasse.getItems().add(
                    rs.getInt("id") + " - " +
                    rs.getString("nom") + " (" + rs.getString("niveau") + ")"
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Quand on choisit un motif
    @FXML
    private void handleMotifSelected() {

        String selected = comboMotif.getValue();
        if (selected == null) return;

        // Motif = partie avant " - "
        String motif = selected.split(" - ")[0];

        boolean estMensuel = selected.endsWith("Mensuel");

        String sql = """
            SELECT t.nom AS typeNom
            FROM motif m
            JOIN type t ON m.type_id = t.id
            WHERE m.nom = ?
        """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, motif);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String typeNom = rs.getString("typeNom");
                txtType.setText(typeNom);

                if (estMensuel) {
                    cbMois.setItems(mois);
                    cbMois.setDisable(false);
                } else {
                    cbMois.getItems().clear();
                    cbMois.setDisable(true);
                }

            } else {
                txtType.clear();
                cbMois.getItems().clear();
                cbMois.setDisable(true);
            }

            // 🔥 Charger montant après type
            String niveau = extraireNiveauDepuisClasse();
            chargerMontantAutomatique(motif, niveau);

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur chargement type : " + e.getMessage()).show();
        }
    }
    
    
    private String extraireNiveauDepuisClasse() {
        String classe = comboClasse.getValue();
        if (classe == null) return null;

        // Exemple: "3 - TleA-Scientifique"
        if (classe.contains("-")) {
            String[] parts = classe.split("-");
            if (parts.length >= 3) {
                return parts[2].trim();
            }
        }

        // Exemple: "3 - TleA (Scientifique)"
        if (classe.contains("(") && classe.contains(")")) {
            return classe.substring(classe.indexOf("(") + 1, classe.indexOf(")"));
        }

        return null;
    }

    
    @FXML
    private void handleEleveSelected() {
        String selected = comboEleve.getValue();
        if (selected == null) return;

        String matricule = selected.split(" - ")[0];
        txtMatricule.setText(matricule);

        String sql = "SELECT nom, prenom FROM eleve WHERE numero_matricule = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricule);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNom.setText(rs.getString("nom"));
                txtPrenom.setText(rs.getString("prenom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void chargerMontantAutomatique(String motif,String Niveau) {

        if (motif == null ) return;

        String sql;
        if (motif.equalsIgnoreCase("mensualité")) {
            if (Niveau == null) return;
            sql = "SELECT montant FROM frais WHERE motif=? AND niveau=?";
        } else {
            sql = "SELECT montant FROM frais WHERE motif=? AND niveau IS NULL";
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, motif);
            if (sql.contains("niveau=?")) pst.setString(2, Niveau);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    double montant = rs.getDouble("montant");
                    txtMontantTotal.setText(String.valueOf(montant));
                } else {
                    txtMontantTotal.clear();
                    showAlert("Tarif introuvable pour ce motif dans la table FRAIS !");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors de la récupération du montant !");
        }
    }

    

    // 🔹 Charger paiement existant
 // 🔹 Charger paiement existant
    private Paiement chargerPaiement(String matricule, String mois, String motif) {
        String sql = (mois == null)
                ? "SELECT * FROM paiement WHERE matricule = ? AND motif = ? AND mois IS NULL ORDER BY id DESC LIMIT 1"
                : "SELECT * FROM paiement WHERE matricule = ? AND motif = ? AND mois = ? ORDER BY id DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricule);
            ps.setString(2, motif);
            if (mois != null) ps.setString(3, mois);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Paiement paiement = new Paiement(
                            rs.getInt("id"),
                            rs.getString("matricule"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("classe"),
                            rs.getDouble("montant_total"),
                            rs.getDouble("montant_paye"),
                            rs.getDouble("reste"),
                            rs.getDate("date_creation").toLocalDate(),
                            rs.getString("motif"),
                            rs.getString("type")
                    );

                    // Charger les transactions liées à ce paiement
                    String sqlTrans = "SELECT * FROM transaction_enseignant WHERE paiement_id = ? ORDER BY date_transaction DESC";
                    try (PreparedStatement psTr = conn.prepareStatement(sqlTrans)) {
                        psTr.setInt(1, paiement.getId());
                        try (ResultSet rsTr = psTr.executeQuery()) {
                            while (rsTr.next()) {
                                Transaction t = new Transaction();
                                t.setId(rsTr.getInt("id"));
                                t.setPaiementId(rsTr.getInt("paiement_id"));
                                t.setMontant(rsTr.getDouble("montant"));
                                t.setDate(rsTr.getDate("date_transaction").toLocalDate());
                                t.setModePaiement(rsTr.getString("mode_paiement"));
                                t.setCommentaire(rsTr.getString("commentaire"));
                                t.setMatricule(rsTr.getString("matricule"));
                                paiement.ajouterTransaction(t);
                            }
                        }
                    }

                    return paiement;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement du paiement : " + e.getMessage());
        }
        return null;
    }
    // 🔹 Sauvegarder le paiement
    @FXML
    private void handleSavePaiement() {
        try {
            String matricule = txtMatricule.getText();
            String nom = txtNom.getText();
            String prenom = txtPrenom.getText();
            String classe = comboClasse.getValue();
            String motif = comboMotif.getValue().split(" - ")[0];
            String mois = cbMois.isDisabled() ? null : cbMois.getValue();
            double montantTotal = Double.parseDouble(txtMontantTotal.getText());
            double montantPaye = Double.parseDouble(txtMontantPaye.getText());
            String type = txtType.getText();

            if (matricule.isEmpty() || motif == null || type.isEmpty()) {
                showAlert("Veuillez remplir tous les champs obligatoires !");
                return;
            }

            Paiement paiement = null;

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

                // 🔹 Vérifier si le paiement existe
                paiement = chargerPaiement(matricule, mois, motif);

                if (paiement == null) {
                    // INSERT paiement
                    String insertSql = """
                            INSERT INTO paiement (matricule, nom, prenom, classe, montant_total, montant_paye, reste, date_creation, motif, type, mois)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                            """;
                    try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, matricule);
                        ps.setString(2, nom);
                        ps.setString(3, prenom);
                        ps.setString(4, classe);
                        ps.setDouble(5, montantTotal);
                        ps.setDouble(6, montantPaye);
                        ps.setDouble(7, montantTotal - montantPaye);
                        ps.setDate(8, Date.valueOf(LocalDate.now()));
                        ps.setString(9, motif);
                        ps.setString(10, type);
                        ps.setString(11, mois);
                        ps.executeUpdate();

                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (rs.next()) {
                                int idPaiement = rs.getInt(1);
                                paiement = new Paiement(idPaiement, matricule, nom, prenom, classe,
                                        montantTotal, montantPaye, montantTotal - montantPaye,
                                        LocalDate.now(), motif, type);
                            }
                        }
                    }
                } else {
                    // UPDATE paiement
                    String updateSql = "UPDATE paiement SET montant_paye = montant_paye + ?, reste = reste - ? WHERE id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                        ps.setDouble(1, montantPaye);
                        ps.setDouble(2, montantPaye);
                        ps.setInt(3, paiement.getId());
                        ps.executeUpdate();
                    }
                }

                // 🔹 Ajouter la transaction si le montant payé > 0
                if (montantPaye > 0 && paiement != null) {
                    String sqlTr = """
                            INSERT INTO transaction_enseignant (paiement_id, matricule, montant, date_transaction, motif, type)
                            VALUES (?, ?, ?, ?, ?, ?)
                            """;
                    try (PreparedStatement ps = conn.prepareStatement(sqlTr)) {
                        ps.setInt(1, paiement.getId());
                        ps.setString(2, matricule);
                        ps.setDouble(3, montantPaye);
                        ps.setDate(4, Date.valueOf(LocalDate.now()));
                        ps.setString(5, motif);
                        ps.setString(6, type);
                        ps.executeUpdate();
                    }
                }

                // 🔹 Recharger le paiement avec les transactions
                paiement = chargerPaiement(matricule, mois, motif);
                if (paiement != null) {
                    transactions.clear();
                    transactions.addAll(paiement.getTransactions());
                    txtMontantTotal.setText(String.valueOf(paiement.getMontantTotal()));
                    txtMontantPaye.setText("0");
                    txtReste.setText(String.valueOf(paiement.getReste()));
                }

                new Alert(Alert.AlertType.INFORMATION, "Paiement enregistré / mis à jour avec succès !").show();

            }

        } catch (NumberFormatException nfe) {
            showAlert("Veuillez entrer un montant valide !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur : " + e.getMessage());
        }
    }
    // 🔹 Charger les paiements d’un élève
       private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }


       
       @FXML
       private void handleChargerPaiement() {
           String matricule = txtMatricule.getText();
           String moiis = cbMois.isDisabled() ? null : cbMois.getValue();
           String motif = comboMotif.getValue().split(" - ")[0];
           Paiement paiement = chargerPaiement(matricule,moiis,motif);

           if (paiement == null) {
               new Alert(Alert.AlertType.WARNING, "Aucun paiement trouvé pour cet élève").show();
               txtMontantTotal.setDisable(false);
               return;
           }
         


           transactions.clear();
           transactions.addAll(paiement.getTransactions());
           txtMontantTotal.setText(String.valueOf(paiement.getMontantTotal()));
           txtMontantPaye.setText("0");
           txtReste.setText(String.valueOf(paiement.getReste()));
           txtMontantTotal.setDisable(true);
       }

@FXML
    private void handleGenererPDF() {
        String matricule = txtMatricule.getText();
        String mois = cbMois.isDisabled() ? null : cbMois.getValue();
        String motif = comboMotif.getValue();
        Paiement paiement = chargerPaiement(matricule,mois,motif);

        if (paiement == null) {
            new Alert(Alert.AlertType.WARNING, "Aucun paiement trouvé pour cet élève").show();
            return;
        }

        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contenu = new PDPageContentStream(document, page)) {

                // Titre
                contenu.beginText();
                contenu.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contenu.newLineAtOffset(150, 750);
                contenu.showText("FACTURE DE PAIEMENT");
                contenu.endText();

                // Infos élève
                contenu.beginText();
                contenu.setFont(PDType1Font.HELVETICA, 12);
                contenu.newLineAtOffset(50, 700);
                contenu.showText("Élève : " + paiement.getPrenom() + " " + paiement.getNom());
                contenu.newLineAtOffset(0, -15);
                contenu.showText("Date : " + paiement.getDateCreation());
                contenu.newLineAtOffset(0, -15);
                contenu.showText("Montant total : " + paiement.getMontantTotal() + " FCFA");
                contenu.newLineAtOffset(0, -15);
                contenu.showText("Montant payé : " + paiement.getMontantPaye() + " FCFA");
                contenu.newLineAtOffset(0, -15);
                contenu.showText("Reste à payer : " + paiement.getReste() + " FCFA");
                contenu.endText();

                // Transactions
                float yPosition = 600;
                contenu.beginText();
                contenu.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contenu.newLineAtOffset(50, yPosition);
                contenu.showText("Historique des transactions :");
                contenu.endText();

                yPosition -= 20;
                for (Transaction t : paiement.getTransactions()) {
                    contenu.beginText();
                    contenu.setFont(PDType1Font.HELVETICA, 12);
                    contenu.newLineAtOffset(50, yPosition);
                    contenu.showText(t.getDate() + " - Montant payé : " + t.getMontant() + " FCFA");
                    contenu.endText();
                    yPosition -= 15;
                }

            }

            String chemin = "facture_" + matricule + ".pdf";
            document.save(chemin);
            document.close();

            // Ouvrir automatiquement le PDF
            File fichier = new File(chemin);
            if (fichier.exists()) {
                Desktop.getDesktop().open(fichier);
            }

            new Alert(Alert.AlertType.INFORMATION, "PDF généré avec succès !").show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la génération du PDF : " + e.getMessage()).show();
        }
    }
    @FXML
    private void handleGenererRecu() {
        String matricule = txtMatricule.getText();
        Paiement paiement = chargerPaiement(matricule);

        if (paiement == null) {
            new Alert(Alert.AlertType.WARNING, "Aucun paiement trouvé pour cet élève").show();
            return;
        }

        // Nom de fichier unique pour éviter les conflits
        String chemin = "recu_" + matricule + "_" + System.currentTimeMillis() + ".pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contenu = new PDPageContentStream(document, page)) {
                // --- En-tête ---
                contenu.beginText();
                contenu.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contenu.newLineAtOffset(200, 750);
                contenu.showText("REÇU DE PAIEMENT");
                contenu.endText();

                // Infos établissement
                contenu.beginText();
                contenu.setFont(PDType1Font.HELVETICA, 10);
                contenu.newLineAtOffset(50, 720);
                contenu.showText("École Supérieure de Gestion");
                contenu.newLineAtOffset(0, -12);
                contenu.showText("Bamako - Mali");
                contenu.newLineAtOffset(0, -12);
                contenu.showText("Tel: +223 70 00 00 00");
                contenu.endText();

                // Ligne séparation
                contenu.moveTo(50, 700);
                contenu.lineTo(550, 700);
                contenu.stroke();

                // Infos élève
                contenu.beginText();
                contenu.setFont(PDType1Font.HELVETICA, 12);
                contenu.newLineAtOffset(50, 670);
                contenu.showText("Matricule : " + paiement.getMatricule());
                contenu.newLineAtOffset(0, -15);
                contenu.showText("Nom : " + paiement.getNom());
                contenu.newLineAtOffset(0, -15);
                contenu.showText("Prénom : " + paiement.getPrenom());
                contenu.newLineAtOffset(0, -15);
                contenu.showText("Classe : " + paiement.getClasse());
                contenu.endText();

                // Montant payé
                int dernierPaiement = paiement.getTransactions().isEmpty()
                        ? 0
                        : paiement.getTransactions().get(paiement.getTransactions().size() - 1).getMontant();

                contenu.beginText();
                contenu.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contenu.newLineAtOffset(50, 600);
                contenu.showText("Montant payé : " + dernierPaiement + " FCFA");
                contenu.newLineAtOffset(0, -20);
                contenu.setFont(PDType1Font.HELVETICA, 12);
                contenu.showText("Montant total : " + paiement.getMontantTotal() + " FCFA");
                contenu.newLineAtOffset(0, -15);
                contenu.showText("Reste à payer : " + paiement.getReste() + " FCFA");
                contenu.endText();

                // Historique des transactions
                float y = 530;
                contenu.beginText();
                contenu.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contenu.newLineAtOffset(50, y);
                contenu.showText("Historique des paiements :");
                contenu.endText();

                y -= 20;
                for (Transaction t : paiement.getTransactions()) {
                    contenu.beginText();
                    contenu.setFont(PDType1Font.HELVETICA, 10);
                    contenu.newLineAtOffset(50, y);
                    contenu.showText("- " + t.getDate() + " : " + t.getMontant() + " FCFA");
                    contenu.endText();
                    y -= 15;
                }

                // Signature
                contenu.beginText();
                contenu.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                contenu.newLineAtOffset(400, 150);
                contenu.showText("Signature du caissier");
                contenu.endText();

                contenu.moveTo(400, 140);
                contenu.lineTo(550, 140);
                contenu.stroke();
            }

            // Sauvegarde du PDF
            document.save(chemin);

            // Ouvrir le PDF
            File fichier = new File(chemin);
            if (fichier.exists()) {
                try {
                    Desktop.getDesktop().open(fichier);
                } catch (IOException e) {
                    new Alert(Alert.AlertType.WARNING, "Impossible d'ouvrir le PDF, mais il a été généré").show();
                }
            }

            new Alert(Alert.AlertType.INFORMATION, "Reçu généré avec succès !").show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la génération du reçu : " + e.getMessage()).show();
        }
    }
   
}
