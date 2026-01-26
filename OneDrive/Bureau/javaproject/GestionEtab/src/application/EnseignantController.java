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
    @FXML private ComboBox<Matiere> cbMatiere;
    @FXML private TableView<Enseignant> tableEnseignants;
    @FXML private TableColumn<Enseignant, Long> colId;
    @FXML private TableColumn<Enseignant, String> colNom;
    @FXML private TableColumn<Enseignant, String> colPrenom;
    @FXML private TableColumn<Enseignant, String> colDateNaissance;
    @FXML private TableColumn<Enseignant, String> colAdresse;
    @FXML private TableColumn<Enseignant, String> colTelephone;
    @FXML private TableColumn<Enseignant, String> colMatiere;

    private ObservableList<Enseignant> enseignants = FXCollections.observableArrayList();
    private ObservableList<Matiere> matieres = FXCollections.observableArrayList();

    // ─── Connexion JDBC ─────────────────────────
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/GestionEcole";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

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
            colMatiere.setCellValueFactory(new PropertyValueFactory<>("matiereNom"));

            // Charger matières
            matieres.setAll(getAllMatieres());
            cbMatiere.setItems(matieres);

            // Charger enseignants
            enseignants.setAll(getAllEnseignants());
            tableEnseignants.setItems(enseignants);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ─── Récupérer toutes les matières ──────────
    private ObservableList<Matiere> getAllMatieres() throws SQLException {
        ObservableList<Matiere> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM matiere";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Matiere(rs.getString("id"), rs.getString("nom")));
            }
        }
        return list;
    }
    
    @FXML
    void openEmargement(ActionEvent event) {
    	changerScene("EmargementView.fxml", "Gestion des Emargements");

    }
    
    @FXML private void openClasse(ActionEvent event) {  changerScene("ClasseView.fxml", "Gestion des Classes");}
    @FXML private void openEleve(ActionEvent event) {changerScene("EleveForm.fxml", "Gestion des Eleves"); }
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
        String sql = "SELECT e.id, e.nom, e.prenom, e.date_naissance, e.adresse, e.telephone, e.matiere_id, m.nom AS matiereNom " +
                     "FROM enseignants e LEFT JOIN matiere m ON e.matiere_id = m.id";
        try (Connection conn = getConnection();
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
                        rs.getLong("matiere_id"),
                        rs.getString("matiereNom")
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
            Stage stage = (Stage) cbMatiere.getScene().getWindow();
            stage.setScene(new Scene(root, 1283, 657));
            stage.setTitle(titre);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 

    
    




    // ─── Ajouter un enseignant ──────────
    @FXML
    private void ajouterEnseignant() {
        String nom = txtNom.getText();
        String prenom = txtPrenom.getText();
        String dateNaissance = (dpNaissance.getValue() != null) ? dpNaissance.getValue().toString() : "";
        String adresse = txtAdresse.getText();
        String telephone = txtTelephone.getText();
        Matiere matiere = cbMatiere.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || matiere == null) return;

        String sql = "INSERT INTO enseignants (nom, prenom, date_naissance, adresse, telephone, matiere_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, dateNaissance);
            stmt.setString(4, adresse);
            stmt.setString(5, telephone);
            stmt.setString(6, matiere.getId());
            stmt.executeUpdate();

            // Recharger les enseignants
            enseignants.setAll(getAllEnseignants());

            // Nettoyer les champs
            txtNom.clear();
            txtPrenom.clear();
            dpNaissance.setValue(null);
            txtAdresse.clear();
            txtTelephone.clear();
            cbMatiere.setValue(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ─── Classes internes pour modèle ──────────
    public static class Enseignant {
        private Long id;
        private String nom;
        private String prenom;
        private String dateNaissance;
        private String adresse;
        private String telephone;
        private Long matiereId;
        private String matiereNom;

        public Enseignant(Long id, String nom, String prenom, String dateNaissance,
                          String adresse, String telephone, Long matiereId, String matiereNom) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.dateNaissance = dateNaissance;
            this.adresse = adresse;
            this.telephone = telephone;
            this.matiereId = matiereId;
            this.matiereNom = matiereNom;
        }

        public Long getId() { return id; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getDateNaissance() { return dateNaissance; }
        public String getAdresse() { return adresse; }
        public String getTelephone() { return telephone; }
        public Long getMatiereId() { return matiereId; }
        public String getMatiereNom() { return matiereNom; }
    }

    
}
