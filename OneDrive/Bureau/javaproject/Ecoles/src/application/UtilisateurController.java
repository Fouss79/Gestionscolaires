package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class UtilisateurController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private ComboBox<Role> comboRole;
    @FXML private Label lblMessage;

    @FXML private TableView<Utilisateur> tableUsers;
    @FXML private TableColumn<Utilisateur, Integer> colId;
    @FXML private TableColumn<Utilisateur, String> colUsername;
    @FXML private TableColumn<Utilisateur, String> colRole;

    private RoleController roleController = new RoleController();
    private ObservableList<Utilisateur> utilisateurs = FXCollections.observableArrayList();

    // 🔹 Initialisation
    @FXML
    private void initialize() {

        // Charger les rôles dans ComboBox
        comboRole.setItems(roleController.getAllRoles());

        // Configurer TableView
        colId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        colUsername.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUsername()));

        colRole.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole().getNom()));

        // Charger utilisateurs
        loadUtilisateurs();
    }

    // 🔹 Ajouter utilisateur
    @FXML
    private void handleAjouterUtilisateur() {

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        Role role = comboRole.getValue();

        if(username.isEmpty() || password.isEmpty() || role == null){
            lblMessage.setText("Veuillez remplir tous les champs !");
            return;
        }

        String sql = "INSERT INTO utilisateur(username,password,role_id) VALUES(?,?,?)";

        try(Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, role.getId());
            pstmt.executeUpdate();

            lblMessage.setText("Utilisateur ajouté !");
            txtUsername.clear();
            txtPassword.clear();
            comboRole.setValue(null);

            loadUtilisateurs();

        } catch (SQLException e) {
            lblMessage.setText("Nom utilisateur déjà existant !");
        }
    }

    // 🔹 Charger utilisateurs
    private void loadUtilisateurs() {

        utilisateurs.clear();

        String sql = """
            SELECT u.id, u.username, r.id AS roleId, r.nom AS roleNom
            FROM utilisateur u
            JOIN role r ON u.role_id = r.id
        """;

        try(Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while(rs.next()){

                Role role = new Role(
                        rs.getInt("roleId"),
                        rs.getString("roleNom")
                );

                Utilisateur user = new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("username"),
                        "",
                        role
                );

                utilisateurs.add(user);
            }

            tableUsers.setItems(utilisateurs);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    // ================= NAVIGATION =================
    
 // 🔹 Navigation entre les pages
    
    
    // 🔹 Navigation générique
    private void changerScene(String fxml, String titre, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 650));
            stage.setTitle(titre);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openEmargement(ActionEvent event) {
    	changerScene("EmargementView.fxml", "Gestion des Emargements",event);

    }
    @FXML private void retourAccueil(ActionEvent event) {changerScene("DashboardView.fxml", "Accueil",event);  }
    @FXML private void openClasse(ActionEvent event) { changerScene("ClasseView.fxml", "Gestion des Classes",event); }
    @FXML private void openEleve(ActionEvent event) { changerScene("ElevesView.fxml", "Gestion des Élèves",event); }
    @FXML private void openMatiere(ActionEvent event) { changerScene("Matiere.fxml", "Gestion des Matières",event); }
    @FXML private void openNote(ActionEvent event) { changerScene("NoteView.fxml", "Gestion des Notes",event); }
    @FXML private void openEnseignant(ActionEvent event) { changerScene("EnseignantView.fxml", "Gestion des Enseignants",event); }
    @FXML private void openTransaction(ActionEvent event) { changerScene("PaiementEnseignant.fxml", "Gestion des Transactions",event); }
    @FXML private void openBulletin(ActionEvent event) { changerScene("BulletinView.fxml", "Gestion des Bulletins",event); }
    @FXML private void openEmploidutemps(ActionEvent event) { changerScene("EmploiDutempsView.fxml", "Gestion des Emplois du Temps",event); }
    @FXML private void openComptabilite(ActionEvent event) { changerScene("EnseignementView.fxml", "Gestion des Cours",event); }


    @FXML private void openAccueil() { loadScene("DashboardView.fxml", "Tableau de Bord"); }
    @FXML private void openEmploiDuTemps() { loadScene("EmploiDutempsView.fxml", "Emploi du Temps"); }
    @FXML private void openPaiement() { loadScene("PaiementEnseignant.fxml", "Paiements"); }
    @FXML private void openParametre() { loadScene("ParametreView.fxml", "Paramètres"); }
    @FXML private void openAnneescolaire() { loadScene("AnneescolaireView.fxml", "Annee scolaire"); }
    @FXML private void openUtilisateur() { loadScene("UtilisateurView.fxml", "Utilisateurs"); }
    @FXML private void openHabilitation() { loadScene("HabilitationView.fxml", "Habilitation"); }

    private void loadScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) comboRole.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
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
            Stage current = (Stage) comboRole.getScene().getWindow();
            current.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    
    

    
    
    
    
}
