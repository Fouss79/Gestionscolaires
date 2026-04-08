package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class EnseignantController {

    // ─── Composants FXML ─────────────────────────
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private DatePicker dpNaissance;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtTelephone;
    @FXML private TextField txtSpecialite;
    @FXML private TableView<Enseignant> tableEnseignants;
    @FXML private TableColumn<Enseignant, Long> colId;
    @FXML private TableColumn<Enseignant, String> colNom;
    @FXML private TableColumn<Enseignant, String> colPrenom;
    @FXML private TableColumn<Enseignant, String> colDateNaissance;
    @FXML private TableColumn<Enseignant, String> colAdresse;
    @FXML private TableColumn<Enseignant, String> colTelephone;
    @FXML private TableColumn<Enseignant, String> colMatiere;
    @FXML private TableColumn<Enseignant, String> colSpecialite;

    private ObservableList<Enseignant> enseignants = FXCollections.observableArrayList();
    

   
    // ─── Initialisation ─────────────────────────
    @FXML
    public void initialize() {
        try {
            // Colonnes TableView
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            colDateNaissance.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
            colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
            colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
            colSpecialite.setCellValueFactory(new PropertyValueFactory<>("specialite"));

            // Charger enseignants
            enseignants.setAll(getAllEnseignants());
            tableEnseignants.setItems(enseignants);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 
    
    @FXML
    void openEmargement(ActionEvent event) {
    	changerScene("EmargementView.fxml", "Gestion des Emargements");

    }
    
    @FXML private void openClasse(ActionEvent event) {  changerScene("ClasseView.fxml", "Gestion des Classes");}
    @FXML private void openEleve(ActionEvent event) {changerScene("ElevesView.fxml", "Gestion des Élèves"); }
    @FXML private void openMatiere(ActionEvent event) {changerScene("Matiere.fxml", "Gestion des Matieres"); }
    @FXML private void openNote(ActionEvent event) { changerScene("NoteView.fxml", "Gestion des Matieres");}
    @FXML private void openEnseignant(ActionEvent event) { changerScene("EnseignantView.fxml", "Gestion des Enseignants");}
    @FXML private void openTransaction(ActionEvent event) { changerScene("PaiementEnseignant.fxml", "Gestion des Transactions");}
    @FXML private void openBulletin(ActionEvent event) { changerScene("ClasseView.fxml", "Gestion des Bulletins");}
    @FXML private void openEmploidutemps(ActionEvent event) { changerScene("EmploiDutempsView.fxml", "Gestion des Emplois du temps");}
    @FXML private void openComptabilite(ActionEvent event) { changerScene("EnseignementView.fxml", "Gestion des Cors"); }


    // ─── Récupérer tous les enseignants avec jointure ──────────
    private ObservableList<Enseignant> getAllEnseignants() throws SQLException {
        ObservableList<Enseignant> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM enseignant";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Enseignant(
                		rs.getLong("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("date_naissance"),
                        rs.getString("adresse"),
                        rs.getString("telephone"),
                        rs.getString("specialite")

                        
                ));
            }
        }
        return list;
    }
    
    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Accueil.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1283, 657);

            // Utiliser un composant existant comme référence pour récupérer la fenêtre
            Stage stage = (Stage) txtNom.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Accueil");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  
   



    // ✅ Méthode générique pour changer de page
    private void changerScene(String fxml, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) txtSpecialite.getScene().getWindow();
            stage.setScene(new Scene(root, 1283, 657));
            stage.setTitle(titre);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openHabilitation(ActionEvent event) {
    	 changerScene("HabilitationView.fxml", "Habilitation");
    }

    
    




    // ─── Ajouter un enseignant ──────────
    @FXML
    private void ajouterEnseignant() {
        String nom = txtNom.getText();
        String prenom = txtPrenom.getText();
        String dateNaissance = (dpNaissance.getValue() != null) ? dpNaissance.getValue().toString() : "";
        String adresse = txtAdresse.getText();
        String telephone = txtTelephone.getText();
        
       String specialite = txtSpecialite.getText();

        if (nom.isEmpty() || prenom.isEmpty() || specialite.isEmpty()) return;

        String sql = "INSERT INTO enseignant (nom, prenom, date_naissance, adresse, telephone, specialite) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, dateNaissance);
            stmt.setString(4, adresse);
            stmt.setString(5, telephone);
            stmt.setString(6, specialite);
            stmt.executeUpdate();

            // Recharger les enseignants
            enseignants.setAll(getAllEnseignants());

            // Nettoyer les champs
            txtNom.clear();
            txtPrenom.clear();
            dpNaissance.setValue(null);
            txtAdresse.clear();
            txtTelephone.clear();
            txtSpecialite.clear();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
    