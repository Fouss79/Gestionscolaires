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

public class PaiementEnseignantController {

    // JDBC
    private final String URL = "jdbc:mysql://localhost:3306/GestionEcole";
    private final String USER = "root";
    private final String PASSWORD = "";

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
   

    
    // ---------------------- CHARGEMENT COMBOS ---------------------- //
    private void loadMatieres() {
        comboMatiere.getItems().clear();
        String sql = "SELECT nom FROM matiere";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) comboMatiere.getItems().add(rs.getString("nom"));
        } catch (SQLException e) {
            showAlert("Erreur chargement matières : " + e.getMessage());
        }
    }

    private void loadMotifs() {
        comboMotif.getItems().clear();
        
       

        String sql = "SELECT nom FROM motif WHERE statut_id = 2";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
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

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, motif);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String typeNom = rs.getString("typeNom");
                txtType.setText(typeNom);
            } else {
                txtType.setText("");
            }
            if(motif.equals("Salaire")) {
          
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
    private void  handleMoisSelected() {
    	String mois = comboMois.getValue(); 
    	String motif = comboMotif.getValue();
    	 int id = Integer.parseInt(comboEnseignant.getValue().split(" - ")[0].trim());
    	
    	if(motif.equals("Salaire") || motif.equals("Reparation")) {  handleChargerPaiement();
    		chargerReste(motif,id);}
    	
    }
    
    @FXML
    private void handleMatiereSelected() {
        String matiere = comboMatiere.getValue();
        if (matiere == null) return;

        String sql = "SELECT e.Enseignant FROM enseignement e JOIN matiere m ON e.matiere = m.nom WHERE m.nom = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matiere);
            ResultSet rs = ps.executeQuery();

            comboEnseignant.getItems().clear();
            while (rs.next()) {
                String enseignant = rs.getString("Enseignant");
                comboEnseignant.getItems().add(enseignant);
            }
        } catch (SQLException e) {
            showAlert("Erreur chargement enseignants : " + e.getMessage());
        }
    }

    @FXML
    private void handleEnseignantSelected() {
        String enseignant = comboEnseignant.getValue();
        if (enseignant == null) return;

        int id = Integer.parseInt(enseignant.split(" - ")[0].trim());

        // Charger info enseignant
        String sql = "SELECT nom, prenom FROM enseignants WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
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
            SELECT t.id, t.montant, t.date_transaction, t.paiement_id, t.mode_paiement, t.commentaire,p.enseignant_id
            FROM paiement p
            JOIN transaction_enseignant t ON t.paiement_id = p.id
            WHERE p.enseignant_id = ?
            ORDER BY t.date_transaction DESC
        """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEnseignant);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Double montant = rs.getDouble("montant");
                LocalDate date = rs.getDate("date_transaction").toLocalDate();
                int id = rs.getInt("id");
                int paiementId = rs.getInt("paiement_id");
                String mode_paiement= rs.getString("mode_paiement");
                String commentaire = rs.getString("commentaire");
                int enseignant_id= rs.getInt("enseignant_id");
                Transaction t = new Transaction();
                t.setId(id);
                t.setEnseignant_id(enseignant_id);
                t.setPaiementId(paiementId);
                t.setMontant(montant);
                t.setDate(date);
                t.setModePaiement(mode_paiement);
                t.setCommentaire(commentaire);
                list.add(t);
                
              for(Transaction p : list) {
            	  System.out.println(p.getEnseignant_id());
              }
            }
        } catch (SQLException e) {
            showAlert("Erreur chargement transactions : " + e.getMessage());
        }
        return list;
    }

    // ---------------------- CHARGER PAIEMENT ---------------------- //
    @FXML
    private void handleChargerPaiement() {
    	String mois = comboMois.getValue();
    	 String motif = comboMotif.getValue();
        LocalDate[] periode = getDebutEtFinMois();
        if (periode == null) return;
        if(!motif.equals("Salaire")) return ;

        LocalDate debut = periode[0];
        LocalDate fin = periode[1];

        String enseignant = comboEnseignant.getValue();
        if (enseignant == null) return;

        int id = Integer.parseInt(enseignant.split(" - ")[0].trim());
        
        

        String sql = "SELECT SUM(duree) AS totalMinutes FROM emargement WHERE enseignant_id = ? AND DATE(date_heure) BETWEEN ? AND ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setDate(2, Date.valueOf(debut));
            ps.setDate(3, Date.valueOf(fin));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int totalMinutes = rs.getInt("totalMinutes");
                double totalHeures = totalMinutes / 60.0;
                txtTotalHeure.setText(String.format("%.2f", totalHeures));
                txtTarif.setText("2000");

                double tarifParHeure = Double.parseDouble(txtTarif.getText());
                double montantTotal = totalHeures * tarifParHeure;
                txtMontantTotal.setText(String.format("%.2f", montantTotal));
                txtMontantTotal.setEditable(false);
            }

        } catch (SQLException e) {
            showAlert("Erreur chargement paiement : " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Veuillez entrer un tarif valide !");
        }
    }

    private void chargerReste( String motif ,int id) {
    	
    	motif = comboMotif.getValue();
    	 
        String sql = "SELECT reste FROM paiement WHERE motif=? AND enseignant_id = ? AND mois = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
        	ps.setString(1,motif);
            ps.setInt(2, id);
            ps.setString(3, comboMois.getValue());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) txtReste.setText(String.valueOf(rs.getDouble("reste")));
        } catch (SQLException e) {
            showAlert("Erreur chargement reste : " + e.getMessage());
        }
    }

   
    @FXML
    private void handleSavePaiement() {
    	  String motif = comboMotif.getValue();
        String enseignant = comboEnseignant.getValue();
        if (enseignant == null) {
            showAlert("Veuillez sélectionner un enseignant !");
            return;
        }

        LocalDate[] periode = getDebutEtFinMois();
        if (periode == null) return;

        LocalDate debut = periode[0];
        LocalDate fin = periode[1];
        String mois = comboMois.getValue();

        try {
            // Vérification des champs numériques
            if (txtMontantTotal.getText().isEmpty()) {
                showAlert("Veuillez calculer le paiement avant d'enregistrer !");
                return;
            }

            // Conversion sécurisée
            double montantTotal = txtMontantTotal.getText().isEmpty() ? 0 :
                Double.parseDouble(txtMontantTotal.getText().replace(",", "."));

            double montantPaye = txtMontantPaye.getText().isEmpty() ? 0 :
                    Double.parseDouble(txtMontantPaye.getText().replace(",", "."));
            Double totalHeures = txtTotalHeure.getText().isEmpty() ? 0 :
                Double.parseDouble(txtTotalHeure.getText().replace(",", "."));

            double reste = montantTotal - montantPaye;

            int id = Integer.parseInt(enseignant.split(" - ")[0].trim());
            PaiementEnseignant paie = chargerPaiementParMois(id, mois,motif);

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                if (paie == null) {
                    // Nouveau paiement
                    String sql = """
                        INSERT INTO paiement (nom, prenom, motif, type, enseignant_id, date_debut, date_fin, 
                                              montant_total, montant_paye, reste, total_minutes, date_creation, mois)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
                    PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                    ps.setString(1, txtNom.getText());
                    ps.setString(2, txtPrenom.getText());
                    ps.setString(3, comboMotif.getValue());
                    ps.setString(4, txtType.getText());
                    ps.setInt(5, id);
                    ps.setDate(6, Date.valueOf(debut));
                    ps.setDate(7, Date.valueOf(fin));
                    ps.setDouble(8, montantTotal);
                    ps.setDouble(9, montantPaye);
                    ps.setDouble(10, reste);
                    ps.setDouble(11, totalHeures);
                    ps.setDate(12, Date.valueOf(LocalDate.now()));
                    ps.setString(13, mois);
                    ps.executeUpdate();

                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next() && montantPaye > 0) {
                        int newId = rs.getInt(1);
                        ajouterTransaction(conn, newId, montantPaye,id);
                    }
                } else {
                    // Paiement existant
                	//if(montantPaye > paie.getReste()) {
                    //    showAlert("Le montant payé dépasse le reste dû !");
                      //  return;
                    //}
                        
                 
                    double rest = txtReste.getText().isEmpty() ? 0 :
                    	Double.parseDouble(txtReste.getText().replace(",", "."));
                    
                    double nouveauReste = rest - montantPaye;
                   
                    String sql = """
                        UPDATE paiement 
                        SET montant_total = ?, montant_paye = montant_paye + ?, reste = montant_total-montant_paye 
                        WHERE id = ?
                    """;
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setDouble(1, montantTotal);
                    ps.setDouble(2, montantPaye);
                 
                    ps.setInt(3, paie.getId());
                    ps.executeUpdate();

                    if (montantPaye > 0) ajouterTransaction(conn, paie.getId(), montantPaye,id);
                }
            }
             
            showAlert("Paiement enregistré avec succès !");
            transactions.clear();
            transactions.addAll(chargerTransactionsPourEnseignant(id));

            // Rafraîchir le reste à l’écran
            chargerReste(motif,id); // ← affiche le nouveau reste calculé

            
           
        } catch (SQLException e) {
            showAlert("Erreur SQL : " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Erreur de format : veuillez entrer des nombres valides !");
        }
    }

    
    private void ajouterTransaction(Connection conn, int paiementId, double montant,int id) throws SQLException {
        String sqlTr = "INSERT INTO transaction_enseignant (paiement_id,enseignant_id, montant, date_transaction) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlTr)) {
            ps.setInt(1, paiementId);
            ps.setInt(2,id);
            ps.setDouble(3, montant);
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
        }
    }

    // ---------------------- GENERER RECU / PDF ---------------------- //
    @FXML private void handleGenererRecu() {}
    @FXML private void handleGenererPDF() {}

    // ---------------------- Charger paiement par mois ---------------------- //
    private PaiementEnseignant chargerPaiementParMois(int idEnseignant, String mois,String motif) {
        String sql = "SELECT * FROM paiement WHERE enseignant_id = ? AND mois = ? AND motif= ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEnseignant);
            ps.setString(2, mois);
            ps.setString(3,motif);
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
