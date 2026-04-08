package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;


import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BarcodeQRCode;


public class PaiementEnseignantController {

  
    // FXML Components
    @FXML private ComboBox<String> comboMatiere;
    @FXML private ComboBox<String> comboEnseignant;
    @FXML private ComboBox<String> comboMotif;
    @FXML private ComboBox<String> comboMois;
    @FXML private ComboBox<Integer> comboAnnee;

    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtType;
    @FXML private TextField txtMontantTotal;
    @FXML private TextField txtMontantPaye;
    @FXML private TextField txtReste;
    @FXML private TextField txtTotalHeure;
    @FXML private TextField txtTarif;

    @FXML private DatePicker dpDebut;
    @FXML private DatePicker dpFin;

    @FXML private TableView<Transaction> tableTransactions;
    @FXML private TableColumn<Transaction, Integer> colId;
    @FXML private TableColumn<Transaction, Integer> colPaiementId;
    @FXML private TableColumn<Transaction, Integer> colEnseignant_id;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, Double> colMontant;
    @FXML private TableColumn<Transaction, String> colModepaiement;
    @FXML private TableColumn<Transaction, String> colCommentaire;
    
    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    
   
        @FXML
        private Button btnDeconnexion;

      
       

    

    
    

    // ---------------------- INITIALIZATION ---------------------- //
    @FXML
    public void initialize() {
        // Colonne ID
        colId.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());

        // Colonne Paiement ID
        colPaiementId.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getPaiementId()).asObject());
        
        colEnseignant_id.setCellValueFactory(data -> 
        new javafx.beans.property.SimpleIntegerProperty(data.getValue().getEnseignant_id()).asObject());

        // Colonne Date
        colDate.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getDate().toString()));

        // Colonne Montant
        colMontant.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleDoubleProperty(data.getValue().getMontant()).asObject());

        // Colonne Mode Paiement
        colModepaiement.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getModePaiement()));

        // Colonne Commentaire
        colCommentaire.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCommentaire() == null ? "" : data.getValue().getCommentaire()));

        // Lier la liste observable au TableView
        tableTransactions.setItems(transactions);

     
        comboMois.setItems(FXCollections.observableArrayList(
                "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        ));

        // Charger données initiales
        loadMatieres();
        loadMotifs();
        comboMotif.setOnAction(e -> chargerTarifSelonMotif());

       

        int anneeCourante = LocalDate.now().getYear();
        ObservableList<Integer> annees = FXCollections.observableArrayList();
        for (int i = anneeCourante - 5; i <= anneeCourante + 5; i++) annees.add(i);
        comboAnnee.setItems(annees);
        comboAnnee.setValue(anneeCourante);
    }

    // ---------------------- UTILITAIRES ---------------------- //
   

    

    private LocalDate[] getDebutEtFinMois() {
        String moisNom = comboMois.getValue();
        Integer annee = comboAnnee.getValue();

        if (moisNom == null || annee == null) {
            showAlert("Veuillez sélectionner un mois et une année !");
            return null;
        }

        int mois = comboMois.getItems().indexOf(moisNom) + 1;
        YearMonth ym = YearMonth.of(annee, mois);
        return new LocalDate[]{ym.atDay(1), ym.atEndOfMonth()};
    }

    private void showAlert(String msg) {
        new Alert(AlertType.ERROR, msg).show();
    }

    // ---------------------- NAVIGATION ---------------------- //
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
        
    
    @FXML
    void openEmargement(ActionEvent event) {
    	changerScene(event,"EmargementView.fxml", "Gestion des Emargements");

    } 
    
    
    @FXML private void retourAccueil() { System.out.println("Retour au Tableau de Bord"); }
    @FXML private void openClasse(ActionEvent event) { changerScene(event,"ClasseView.fxml", "Gestion des Classes"); }
    @FXML private void openEleve(ActionEvent event) { changerScene(event,"EleveForm.fxml", "Gestion des Élèves"); }
    @FXML private void openMatiere(ActionEvent event) { changerScene(event,"Matiere.fxml", "Gestion des Matières"); }
    @FXML private void openNote(ActionEvent event) { changerScene(event,"NoteView.fxml", "Gestion des Notes"); }
    @FXML private void openEnseignant(ActionEvent event) { changerScene(event,"EnseignantView.fxml", "Gestion des Enseignants"); }
    @FXML private void openTransaction(ActionEvent event) { changerScene(event,"PaiementEnseignant.fxml", "Gestion des Transactions"); }
    @FXML private void openBulletin(ActionEvent event) { changerScene(event,"BulletinView.fxml", "Gestion des Bulletins"); }
    @FXML private void openEmploidutemps(ActionEvent event) { changerScene(event,"EmploiDutempsView.fxml", "Gestion des Emplois du Temps"); }
    @FXML private void openComptabilite(ActionEvent event) { changerScene(event,"EnseignementView.fxml", "Gestion des Enseignements"); }

    
    
       @FXML private void openEnseignants(ActionEvent event) { changerScene(event,"PaiementEnseignant.fxml", "Gestion des Finances"); }
    @FXML private void openEleve1(ActionEvent event) {changerScene(event,"PaiementEleveForm.fxml", "Gestion des Finances"); }
    @FXML
    void openHabilitation(ActionEvent event) {
    	 changerScene(event,"HabilitationView.fxml", "Habilitation");
    }   

    
    // ---------------------- CHARGEMENT COMBOS ---------------------- //
    private void loadMatieres() {
        comboMatiere.getItems().clear();
        String sql = "SELECT id, nom FROM matiere";
        try (Connection conn = Database.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) comboMatiere.getItems().add(rs.getInt("id")+" - "+rs.getString("nom"));
        } catch (SQLException e) {
            showAlert("Erreur chargement matières : " + e.getMessage());
        }
    }

    private void loadMotifs() {
        comboMotif.getItems().clear();
        
       

        String sql = "SELECT nom FROM motif";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

   
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) 
           
        
            	
            	
            	comboMotif.getItems().add(rs.getString("nom"));}
        } catch (SQLException e) {
            showAlert("Erreur chargement motifs : " + e.getMessage());
        }
    }
    
    @FXML
    private void handleMotifSelected() {
        String motif = comboMotif.getValue();
        if (motif == null) return;

        String sql = """
            SELECT t.nom AS typeNom
            FROM motif m
            JOIN type t ON m.type_id = t.id
            WHERE m.nom = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, motif);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String typeNom = rs.getString("typeNom");
                txtType.setText(typeNom);
            } else {
                txtType.setText("");
            }
            if(motif.equals("SALAIRE")) {
          
            	 comboMois.setItems(FXCollections.observableArrayList(
                         "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                         "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
                 ));
            	 
            }
            else if(motif.equals("Reparation")) {
            	
            	 comboMois.setItems(FXCollections.observableArrayList(
                         "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                         "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
                 ));
            	txtMontantTotal.setEditable(true);

            	txtMontantTotal.setText("");
            	
            	txtTotalHeure.setText("");
            	txtTarif.setText("");
            }
            else {
            	 comboMois.getItems().clear();
             
            	txtMontantTotal.setEditable(true);

            	txtMontantTotal.setText("");
            	
            	txtTotalHeure.setText("");
            	txtTarif.setText("");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur chargement type : " + e.getMessage()).show();
        }
    }

 

  
    @FXML
    private void handleMoisSelected() {

        String mois = comboMois.getValue();
        String motif = comboMotif.getValue();
        String enseignant = comboEnseignant.getValue();

        if (mois == null || motif == null || enseignant == null) {
            return;
        }

        int id = Integer.parseInt(enseignant.split(" - ")[0].trim());

        if(motif.equals("SALAIRE") || motif.equals("Reparation")) {
            handleChargerPaiement();
            chargerReste(motif,id);
        }
    }
    
    @FXML
    private void handleMatiereSelected() {

        String matiere = comboMatiere.getValue();

        if (matiere == null || matiere.isEmpty()) {
            return;
        }

        comboEnseignant.getItems().clear();

        try (Connection conn = Database.connect()) {

            // ⚠ IMPORTANT : si ton comboMatiere contient "1 - Math"
            String[] parts = matiere.split(" - ");
            int matiereId = Integer.parseInt(parts[0].trim());

            String sql = """
                SELECT e.id, e.nom, e.prenom
                FROM enseignant e
                JOIN habilitation h ON e.id = h.enseignant_id
                WHERE h.matiere_id = ?
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, matiereId);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {

                found = true;

                String enseignant =
                        rs.getInt("id") + " - " +
                        rs.getString("nom") + " " +
                        rs.getString("prenom");

                System.out.println("Enseignants trouvés : " + comboEnseignant.getItems().size());

                comboEnseignant.getItems().add(enseignant);
                
            }

            if (!found) {
                System.out.println("Aucun enseignant trouvé pour cette matière !");
            }

        } catch (Exception e) {
            showAlert("Erreur chargement enseignants : " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleEnseignantSelected() {
        String enseignant = comboEnseignant.getValue();
        if (enseignant == null) return;

        int id = Integer.parseInt(enseignant.split(" - ")[0].trim());

        // Charger info enseignant
        String sql = "SELECT nom, prenom FROM enseignant WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNom.setText(rs.getString("nom"));
                txtPrenom.setText(rs.getString("prenom"));
                ;
            }
        } catch (SQLException e) {
            showAlert("Erreur chargement info enseignant : " + e.getMessage());
        }

        // Charger paiement et transactions
        comboMotif.setValue(null);
        transactions.clear();
        transactions.addAll(chargerTransactionsPourEnseignant(id));
       
    }

    // ---------------------- CHARGEMENT DES TRANSACTIONS ---------------------- //
    private ObservableList<Transaction> chargerTransactionsPourEnseignant(int idEnseignant) {

        ObservableList<Transaction> list = FXCollections.observableArrayList();

        String sql = """
            SELECT 
                t.id AS transaction_id,
                t.montant,
                t.date_transaction,
                t.paiement_id,
                t.mode_paiement,
                t.commentaire,
                p.enseignant_id
            FROM paiement p
            INNER JOIN transaction_enseignant t 
                ON t.paiement_id = p.id
            WHERE p.enseignant_id = ?
            ORDER BY t.date_transaction DESC
        """;

        try (Connection conn = Database.connect()) {

            System.out.println("=== DEBUG CONNEXION ===");
            System.out.println("AutoCommit: " + conn.getAutoCommit());

            // 🔎 1️⃣ Vérifier les tables visibles
            System.out.println("=== TABLES DISPONIBLES ===");
            try (Statement st = conn.createStatement();
                 ResultSet rsTables = st.executeQuery(
                         "SELECT name FROM sqlite_master WHERE type='table'")) {

                while (rsTables.next()) {
                    System.out.println("Table trouvée : " + rsTables.getString("name"));
                }
            }

            // 🔎 2️⃣ Exécution de la requête
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idEnseignant);
                ResultSet rs = ps.executeQuery();

                System.out.println("=== RESULTATS ===");

                while (rs.next()) {

                 
                    Double montant = rs.getDouble("montant");

                    LocalDate date = null;

                    long timestamp = rs.getLong("date_transaction");
                    if (!rs.wasNull()) {
                        date = new java.sql.Date(timestamp).toLocalDate();
                    }
                 
                    int id = rs.getInt("transaction_id");
                    int paiementId = rs.getInt("paiement_id");
                    String mode = rs.getString("mode_paiement");
                    String commentaire = rs.getString("commentaire");
                    int enseignantId = rs.getInt("enseignant_id");
                    

                    Transaction t = new Transaction();
                    t.setId(id);
                    t.setEnseignant_id(enseignantId);
                    t.setPaiementId(paiementId);
                    t.setMontant(montant);
                    t.setDate(date);
                    t.setModePaiement(mode);
                    t.setCommentaire(commentaire);

                    list.add(t);

                    System.out.println("Transaction chargée ID=" + id);
                }
            }

        } catch (SQLException e) {
            System.out.println("=== ERREUR SQL ===");
            e.printStackTrace();
            showAlert("Erreur chargement transactions : " + e.getMessage());
        }

        return list;
    }

    // ---------------------- CHARGER PAIEMENT ---------------------- //
    @FXML
    private void handleChargerPaiement() {

        String motif = comboMotif.getValue();
        if (motif == null) return;

        LocalDate[] periode = getDebutEtFinMois();
        if (periode == null) return;

        LocalDate debut = periode[0];
        LocalDate fin = periode[1];

        String enseignant = comboEnseignant.getValue();
        if (enseignant == null) return;

        int idEnseignant = Integer.parseInt(enseignant.split(" - ")[0].trim());

        int totalMinutes = 0;

        String sqlHeures = """
            SELECT SUM(duree) AS totalMinutes
            FROM emargement
            WHERE enseignant_id = ?
            AND DATE(date_heure) BETWEEN ? AND ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sqlHeures)) {

            ps.setInt(1, idEnseignant);
            ps.setString(2, debut.toString());
            ps.setString(3, fin.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalMinutes = rs.getInt("totalMinutes");
            }

        } catch (SQLException e) {
            showAlert("Erreur calcul heures : " + e.getMessage());
            return;
        }

        double totalHeures = totalMinutes / 60.0;
        txtTotalHeure.setText(String.format("%.2f", totalHeures));

        // 🔥 Récupérer tarif
        double tarif = getTarifByMotif(motif, ContexteApplication.getAnneeId());

        if (tarif == -1) {
            showAlert("Tarif non défini !");
            return;
        }

        txtTarif.setText(String.format("%.2f", tarif));
        txtTarif.setEditable(false);

        double montantTotal = totalHeures * tarif;

        txtMontantTotal.setText(String.format("%.2f", montantTotal));
        txtMontantTotal.setEditable(false);

        txtReste.setText(String.format("%.2f", montantTotal));
    }
    private double getTarifByMotif(String motifNom, int anneeId) {

        String sql = """
            SELECT t.montant
            FROM tarif t
            JOIN motif m ON t.motif_id = m.id
            WHERE m.nom = ?
            AND t.annee_id = ?
            LIMIT 1
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, motifNom);
            ps.setInt(2, anneeId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("montant");
            }

        } catch (SQLException e) {
            showAlert("Erreur récupération tarif : " + e.getMessage());
        }

        return -1; // tarif non trouvé
    }
    private void chargerTarifSelonMotif() {

        String motif = comboMotif.getValue();
        if (motif == null) return;

        int anneeId = ContexteApplication.getAnneeId();

        double tarif = getTarifByMotif(motif, anneeId);

        if (tarif == -1) {
            showAlert("Tarif non défini pour ce motif !");
            txtTarif.clear();
            return;
        }

        txtTarif.setText(String.format("%.2f", tarif));
        txtTarif.setEditable(false);
    }
    private void chargerReste(String motif, int id) {

        int anneeId = ContexteApplication.getAnneeId();

        String sql = """
            SELECT reste
            FROM paiement
            WHERE motif = ?
            AND enseignant_id = ?
            AND mois = ?
            AND annee_id = ?
            ORDER BY id DESC
            LIMIT 1
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, motif);
            ps.setInt(2, id);
            ps.setString(3, comboMois.getValue());
            ps.setInt(4, anneeId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtReste.setText(String.valueOf(rs.getDouble("reste")));
            }

        } catch (SQLException e) {
            showAlert("Erreur chargement reste : " + e.getMessage());
        }
    }
   

   

    @FXML
    private void handleSavePaiement() {

        int anneeId = ContexteApplication.getAnneeId();
        String motif = comboMotif.getValue();
        String enseignant = comboEnseignant.getValue();

        if (enseignant == null || motif == null) {
            showAlert("Veuillez sélectionner un enseignant et un motif !");
            return;
        }

        LocalDate[] periode = getDebutEtFinMois();
        if (periode == null) return;

        LocalDate debut = periode[0];
        LocalDate fin = periode[1];
        String mois = comboMois.getValue();

        int idEnseignant = Integer.parseInt(enseignant.split(" - ")[0].trim());

        try (Connection conn = Database.connect()) {

            conn.setAutoCommit(false);

            if (txtTotalHeure.getText().isEmpty()) {
                showAlert("Veuillez charger le paiement avant d'enregistrer !");
                return;
            }

            double totalHeures = Double.parseDouble(txtTotalHeure.getText().replace(",", "."));
            double montantPaye = txtMontantPaye.getText().isEmpty() ? 0 :
                    Double.parseDouble(txtMontantPaye.getText().replace(",", "."));

            // 🔥 Récupérer tarif proprement
            double tarif = getTarifByMotif(motif, anneeId);

            if (tarif == -1) {
                showAlert("Tarif non défini !");
                return;
            }

            double montantTotal = totalHeures * tarif;

            // Vérifier paiement existant
            PaiementEnseignant paie = chargerPaiementParMois(idEnseignant, mois, motif);

            if (paie == null) {

                String sqlInsert = """
                    INSERT INTO paiement
                    (nom, prenom, motif, type, enseignant_id,
                     date_debut, date_fin,
                     montant_total, montant_paye, reste,
                     total_minutes, date_creation,
                     mois, annee_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

                try (PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, txtNom.getText());
                    ps.setString(2, txtPrenom.getText());
                    ps.setString(3, motif);
                    ps.setString(4, txtType.getText());
                    ps.setInt(5, idEnseignant);
                    ps.setDate(6, Date.valueOf(debut));
                    ps.setDate(7, Date.valueOf(fin));
                    ps.setDouble(8, montantTotal);
                    ps.setDouble(9, montantPaye);
                    ps.setDouble(10, montantTotal - montantPaye);
                    ps.setDouble(11, totalHeures);
                    ps.setDate(12, Date.valueOf(LocalDate.now()));
                    ps.setString(13, mois);
                    ps.setInt(14, anneeId);

                    ps.executeUpdate();

                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next() && montantPaye > 0) {
                        int paiementId = rs.getInt(1);
                        ajouterTransaction(conn, paiementId, montantPaye, idEnseignant);
                    }
                }

            } else {

                if (montantPaye > paie.getReste()) {
                    showAlert("Le montant payé dépasse le reste !");
                    return;
                }

                double nouveauMontantPaye = paie.getMontantPaye() + montantPaye;
                double nouveauReste = montantTotal - nouveauMontantPaye;

                String sqlUpdate = """
                    UPDATE paiement
                    SET montant_total = ?, montant_paye = ?, reste = ?
                    WHERE id = ?
                """;

                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setDouble(1, montantTotal);
                    ps.setDouble(2, nouveauMontantPaye);
                    ps.setDouble(3, nouveauReste);
                    ps.setInt(4, paie.getId());
                    ps.executeUpdate();

                    if (montantPaye > 0) {
                        ajouterTransaction(conn, paie.getId(), montantPaye, idEnseignant);
                    }
                }
            }

            conn.commit();

            showAlert("Paiement enregistré avec succès !");

            transactions.clear();
            transactions.addAll(chargerTransactionsPourEnseignant(idEnseignant));
            chargerReste(motif, idEnseignant);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur : " + e.getMessage());
        }
    }
    
    private void ajouterTransaction(Connection conn, int paiementId, double montant, int idEnseignant) throws SQLException {

        String sqlTr = """
            INSERT INTO transaction_enseignant 
            (paiement_id, enseignant_id, montant, date_transaction) 
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sqlTr, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, paiementId);
            ps.setInt(2, idEnseignant);
            ps.setDouble(3, montant);
            ps.setDate(4, java.sql.Date.valueOf(LocalDate.now()));

            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {

                int transactionId = generatedKeys.getInt(1);

                // 🔥 Récupérer les infos mises à jour du paiement
                String sqlInfo = "SELECT montant_total, montant_paye FROM paiement WHERE id = ?";
                try (PreparedStatement psInfo = conn.prepareStatement(sqlInfo)) {
                    psInfo.setInt(1, paiementId);
                    ResultSet rs = psInfo.executeQuery();

                    if (rs.next()) {

                        double montantTotal = rs.getDouble("montant_total");
                        double montantDejaPaye = rs.getDouble("montant_paye");

                        String nomComplet = txtNom.getText() + " " + txtPrenom.getText();

                        genererRecu(
                                transactionId,
                                nomComplet,
                                montant,
                                LocalDate.now(),
                                montantTotal,
                                montantDejaPaye
                        );
                    }
                }
            }
        }
    }    
    private String genererNumeroRecu(int transactionId) {
        int annee = LocalDate.now().getYear();
        return String.format("REC-%d-%04d", annee, transactionId);
    }
  
    
    // ---------------------- GENERER RECU / PDF ---------------------- //
    
    private void genererRecu(
            int transactionId,
            String enseignantNom,
            double montant,
            LocalDate datePaiement,
            double montantTotal,
            double montantDejaPaye
    ) {
        try {
           
            String fileName = "Recu_REC-" + transactionId +comboAnnee.getValue() + ".pdf";

            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(fileName));
            
            document.open();
            
            // =========================
            // FONTS
            // =========================
            com.itextpdf.text.Font titreFont =
                    new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            18,
                            com.itextpdf.text.Font.BOLD
                    );

            com.itextpdf.text.Font headerFont =
                    new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            14,
                            com.itextpdf.text.Font.BOLD
                    );

            com.itextpdf.text.Font normalFont =
                    new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            12
                    );

            com.itextpdf.text.Font smallFont =
                    new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            10
                    );

            // =========================
            // EN-TÊTE ECOLE
            // =========================
            com.itextpdf.text.Paragraph ecole =
                    new com.itextpdf.text.Paragraph("LYCÉE PRIVÉ EL MOCTAR KONATÉ", titreFont);
            ecole.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(ecole);

            document.add(new com.itextpdf.text.Paragraph("Bamako - Mali", smallFont));
            document.add(new com.itextpdf.text.Paragraph("Téléphone : +223 79 70 70 10", smallFont));
            document.add(new com.itextpdf.text.Paragraph("Email : contact@elmoctar.com", smallFont));

            document.add(new com.itextpdf.text.Paragraph(" "));
            document.add(new com.itextpdf.text.Paragraph("------------------------------------------------------------"));
            document.add(new com.itextpdf.text.Paragraph(" "));

            // =========================
            // TITRE
            // =========================
            com.itextpdf.text.Paragraph titre =
                    new com.itextpdf.text.Paragraph("FACTURE", headerFont);
            titre.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(titre);

            document.add(new com.itextpdf.text.Paragraph(" "));
            document.add(new com.itextpdf.text.Paragraph("Référence : REC-" + LocalDate.now().getYear() + "-" + transactionId, normalFont));
            document.add(new com.itextpdf.text.Paragraph("Date : " + datePaiement, normalFont));
            document.add(new com.itextpdf.text.Paragraph("Mois concerné : " + comboMois.getValue(), normalFont));
            document.add(new com.itextpdf.text.Paragraph("Année scolaire : " + comboAnnee.getValue(), normalFont));
            document.add(new com.itextpdf.text.Paragraph(" "));

            // =========================
            // TABLEAU INFOS
            // =========================
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(2);
            table.setWidthPercentage(100);

            double montantRestant = Math.max(0, montantTotal - montantDejaPaye);
            String statut = montantRestant == 0 ? "SOLDÉ" : "EN COURS";

            table.addCell("Enseignant");
            table.addCell(enseignantNom);

            table.addCell("Montant total à payer");
            table.addCell(String.format("%,.0f FCFA", montantTotal));

            table.addCell("Montant déjà payé");
            table.addCell(String.format("%,.0f FCFA", montantDejaPaye));

            table.addCell("Paiement effectué aujourd'hui");
            table.addCell(String.format("%,.0f FCFA", montant));

            table.addCell("Montant restant");
            table.addCell(String.format("%,.0f FCFA", montantRestant));

            table.addCell("Statut");
            table.addCell(statut);

            table.addCell("Mode de paiement");
            table.addCell("Espèces / Virement / Mobile Money");

            table.addCell("Motif");
            table.addCell(comboMotif.getValue());

            document.add(table);

            document.add(new com.itextpdf.text.Paragraph(" "));
            document.add(new com.itextpdf.text.Paragraph(
                    "Arrêté le présent reçu à la somme de : "
                            + String.format("%,.0f", montant)
                            + " FCFA.", normalFont));

            document.add(new com.itextpdf.text.Paragraph(" "));
            document.add(new com.itextpdf.text.Paragraph("------------------------------------------------------------"));
            document.add(new com.itextpdf.text.Paragraph(" "));
            
            String numeroRecu = genererNumeroRecu(transactionId);

            BarcodeQRCode qrCode = new BarcodeQRCode(
                    "RECU:" + numeroRecu +
                    "|ENSEIGNANT:" + enseignantNom +
                    "|MONTANT:" + montant +
                    "|DATE:" + datePaiement,
                    150,
                    150,
                    null
            );

            Image qrImage = qrCode.getImage();
            qrImage.setAlignment(Element.ALIGN_CENTER);
            document.add(new Paragraph(" "));
            document.add(qrImage);

            // =========================
            // SIGNATURE
            // =========================
            document.add(new com.itextpdf.text.Paragraph("Signature du responsable :", normalFont));
            document.add(new com.itextpdf.text.Paragraph(" "));
            document.add(new com.itextpdf.text.Paragraph("_______________________________"));
            document.add(new com.itextpdf.text.Paragraph("Cachet officiel de l'établissement"));

            document.add(new com.itextpdf.text.Paragraph(" "));
            document.add(new com.itextpdf.text.Paragraph("Ce document tient lieu de justificatif officiel.", smallFont));

            document.close();

            new Alert(Alert.AlertType.INFORMATION,
                    "Reçu Premium généré avec succès : " + fileName).show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Erreur lors de la génération du reçu !").show();
        }
    }
    @FXML
    private void OpenPaiement(ActionEvent event) {

        loadScene("ComptableView.fxml", "Paiements");

    }
    
       

        @FXML
        void handleRapport() {loadScene("PaiementView.fxml", "Habilitation");

        }
        @FXML
        void handleRapportScolaire() {loadScene("PaiementEleveRapportView.fxml", "Habilitation");

        }

        @FXML
        void openScolarite() {loadScene("PaiementEleveView.fxml", "Scolarite");

        }
      

    
        private void loadScene(String fxml, String title) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
                Parent root = loader.load();
                Stage stage = (Stage) comboAnnee.getScene().getWindow();
                stage.setScene(new Scene(root, 1283, 657));
                stage.setTitle(title);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur ouverture page : " + e.getMessage());
            }
        }

      
                
      
        @FXML
        private void handleLogout() {

            try {

                // supprimer session
            	ContexteApplication.reset();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root,1283,657));
                stage.setTitle("Connexion");
                stage.show();

                // fermer dashboard actuel
                Stage current = (Stage) comboAnnee.getScene().getWindow();
                current.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
         

        
        
        @FXML private void handleGenererRecu() {}
    @FXML private void handleGenererPDF() {}

    // ---------------------- Charger paiement par mois ---------------------- //
    private PaiementEnseignant chargerPaiementParMois(int idEnseignant, String mois, String motif) {

        int anneeId = ContexteApplication.getAnneeId();

        String sql = """
            SELECT *
            FROM paiement
            WHERE enseignant_id = ?
            AND mois = ?
            AND motif = ?
            AND annee_id = ?
            ORDER BY id DESC
            LIMIT 1
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEnseignant);
            ps.setString(2, mois);
            ps.setString(3, motif);
            ps.setInt(4, anneeId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                PaiementEnseignant paiement = new PaiementEnseignant();
                paiement.setId(rs.getInt("id"));
                paiement.setMontantTotal(rs.getInt("montant_total"));
                paiement.setMontantPaye(rs.getInt("montant_paye"));
                paiement.setReste(rs.getDouble("reste"));
                return paiement;
            }

        } catch (SQLException e) {
            showAlert("Erreur lecture paiement : " + e.getMessage());
        }

        return null;
    }
    
    
}
