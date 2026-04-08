package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;

public class FirstAdminController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnCreate;
    @FXML private Label lblMessage;

    @FXML
    private void handleCreateAdmin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if(username.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Veuillez remplir tous les champs !");
            return;
        }

        String sqlUser = "INSERT INTO utilisateur (username, password, role_id) VALUES (?, ?, ?)";

        try(Connection conn = Database.connect()) {

            // 1️⃣ créer rôle ADMIN si pas déjà fait
            PreparedStatement psRole = conn.prepareStatement(
                    "INSERT OR IGNORE INTO role(id, nom) VALUES (1, 'ADMIN')"
            );
            psRole.executeUpdate();

            // 2️⃣ créer utilisateur admin
            PreparedStatement psUser = conn.prepareStatement(sqlUser);
            psUser.setString(1, username);
            psUser.setString(2, password); // plus tard tu peux hasher le mot de passe
            psUser.setInt(3, 1); // role_id = 1 (ADMIN)
            psUser.executeUpdate();

            // 3️⃣ définir l'utilisateur connecté dans ContexteApplication
            Utilisateur admin = new Utilisateur(1, username, "", new Role(1, "ADMIN"));
            ContexteApplication.setUtilisateurConnecte(admin);

            // 4️⃣ ouvrir Dashboard
            ouvrirDashboard();

        } catch (SQLException e) {
            e.printStackTrace();
            lblMessage.setText("Erreur : " + e.getMessage());
        }
    }
    private void ouvrirDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AnneeScolaireView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de Bord");
            stage.show();

            // fermer fenêtre actuelle
            Stage current = (Stage) txtUsername.getScene().getWindow();
            current.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= Hover bouton =================
    @FXML
    private void hoverBtn(javafx.scene.input.MouseEvent e){
        btnCreate.setStyle(
                "-fx-background-color:#2980b9;" +
                "-fx-text-fill:white;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;");
    }

    @FXML
    private void leaveBtn(javafx.scene.input.MouseEvent e){
        btnCreate.setStyle(
                "-fx-background-color:#3498db;" +
                "-fx-text-fill:white;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;");
    }
}