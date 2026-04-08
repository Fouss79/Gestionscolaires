package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.*;

public class RoleController {

    // 🔹 Champs liés au FXML
    @FXML
    private TextField txtNomRole;

    @FXML
    private Label lblMessageRole;

    // 🔹 Ajouter un rôle (appelé par le bouton)
    @FXML
    private void handleAjouterRole(){

        String nomRole = txtNomRole.getText().trim();

        if(nomRole.isEmpty()){
            lblMessageRole.setText("Veuillez saisir un rôle !");
            return;
        }

        String sql = "INSERT INTO role(nom) VALUES(?)";

        try(Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nomRole.toUpperCase());
            pstmt.executeUpdate();

            lblMessageRole.setText("Rôle ajouté avec succès !");
            txtNomRole.clear();

        } catch (SQLException e) {
            lblMessageRole.setText("Ce rôle existe déjà !");
        }
    }

    // 🔹 Charger tous les rôles (ComboBox ailleurs)
    public ObservableList<Role> getAllRoles(){

        ObservableList<Role> roles = FXCollections.observableArrayList();

        String sql = "SELECT * FROM role";

        try(Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while(rs.next()){
                roles.add(new Role(
                        rs.getInt("id"),
                        rs.getString("nom")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return roles;
    }

    // 🔹 Récupérer id du rôle
    public int getRoleIdByNom(String nom){

        String sql = "SELECT id FROM role WHERE nom = ?";
        int id = -1;

        try(Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                id = rs.getInt("id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }
}
