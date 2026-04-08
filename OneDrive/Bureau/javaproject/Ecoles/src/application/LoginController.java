package application;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;
    @FXML private Button btnLogin;

    // ================= INITIALIZE =================
    @FXML
    public void initialize() {

        // Animation fade-in
        FadeTransition fade = new FadeTransition(Duration.seconds(1));
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setNode(txtUsername.getParent());
        fade.play();

        // Vérifier si c'est la première utilisation après que la scène soit attachée
        Platform.runLater(() -> {
            if (isFirstRun()) {
                showFirstAdminSetup();
            }
        });
    }

    // ================= LOGIN =================
    @FXML
    private void handleLogin() {

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if(username.isEmpty() || password.isEmpty()){
            lblMessage.setText("Veuillez remplir tous les champs !");
            return;
        }

        String sql = """
                SELECT u.id, u.username,
                       r.id as roleId, r.nom as roleNom
                FROM utilisateur u
                JOIN role r ON u.role_id = r.id
                WHERE u.username = ? AND u.password = ?
                """;

        try(Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
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
                ContexteApplication.setUtilisateurConnecte(user);

             // 🔥 Charger année active depuis la base
                boolean anneeExiste = chargerAnneeActive();

                if (!anneeExiste) {
                    ouvrirSelectionAnnee();
                } else {
                    ouvrirVueSelonRole(user);
                }
               

            } else {
                lblMessage.setText("Nom utilisateur ou mot de passe incorrect !");
            }

        } catch(Exception e){
            e.printStackTrace();
            lblMessage.setText("Erreur lors de la connexion !");
        }
    }
    private void ouvrirVueSelonRole(Utilisateur user){

        try {

            String vue = "";

            if(user.getRole().getNom().equalsIgnoreCase("Admin")){
                vue = "DashboardView.fxml";
            }
            else if(user.getRole().getNom().equalsIgnoreCase("Comptable")){
                vue = "ComptableView.fxml";
            }
            else{
                vue = "DashboardView.fxml"; // sécurité
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(vue));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Application scolaire");
            stage.show();

            Stage current = (Stage) txtUsername.getScene().getWindow();
            current.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }
    private void ouvrirSelectionAnnee() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AnneescolaireView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sélection Année Scolaire");
            stage.show();

            Stage current = (Stage) txtUsername.getScene().getWindow();
            current.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ================= OUVRIR DASHBOARD =================
    private void ouvrirDashboard(Utilisateur user){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DashboardView.fxml"));
            Parent root = loader.load();

            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();

            // Fermer login
            Stage current = (Stage) txtUsername.getScene().getWindow();
            if(current != null) current.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }
    private boolean chargerAnneeActive() {

        // Réinitialiser le contexte
        ContexteApplication.setAnneeScolaire(null);

        String sql = "SELECT id, libelle FROM anneescolaire WHERE active = 1 LIMIT 1";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                ContexteApplication.setAnneeId(rs.getInt("id"));     // si tu as anneeId
                ContexteApplication.setAnneeScolaire(rs.getString("libelle"));
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ================= PREMIER ADMIN =================
    private boolean isFirstRun() {
        String sql = "SELECT COUNT(*) FROM utilisateur";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1) == 0; // aucun utilisateur => first run
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void showFirstAdminSetup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FirstAdminView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Création du premier administrateur");
            stage.show();

            // Fermer login uniquement si attaché
            Stage current = (Stage) txtUsername.getScene().getWindow();
            if(current != null) current.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= HOVER BOUTON =================
    @FXML
    private void hoverBtn(javafx.scene.input.MouseEvent e){
        btnLogin.setStyle(
                "-fx-background-color:#2980b9;" +
                "-fx-text-fill:white;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;");
    }

    @FXML
    private void leaveBtn(javafx.scene.input.MouseEvent e){
        btnLogin.setStyle(
                "-fx-background-color:#3498db;" +
                "-fx-text-fill:white;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;");
    }

    // ================= TOGGLE MOT DE PASSE =================
    @FXML
    private void togglePassword() {
        if(txtPassword.getPromptText().equals("Mot de passe")){
            txtPassword.setPromptText(txtPassword.getText());
            txtPassword.setText("");
        } else {
            txtPassword.setText(txtPassword.getPromptText());
            txtPassword.setPromptText("Mot de passe");
        }
    }
}