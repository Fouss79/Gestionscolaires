package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class TypeController {

    @FXML private TextField txtNom;
    @FXML private TableView<Type> tableType;
    @FXML private TableColumn<Type, Integer> colId;
    @FXML private TableColumn<Type, String> colNom;

    private ObservableList<Type> liste = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        chargerTypes();

        tableType.setOnMouseClicked(e -> {
            Type t = tableType.getSelectionModel().getSelectedItem();
            if (t != null) {
                txtNom.setText(t.getNom());
            }
        });
    }

    private void chargerTypes() {
        liste.clear();

        try (Connection conn = Database.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM type")) {

            while (rs.next()) {
                liste.add(new Type(
                        rs.getInt("id"),
                        rs.getString("nom")
                ));
            }

            tableType.setItems(liste);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterType() {

        String nom = txtNom.getText();

        if (nom.isEmpty()) {
            alert("Entrez un nom !");
            return;
        }

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO type(nom) VALUES (?)")) {

            ps.setString(1, nom.toUpperCase());
            ps.executeUpdate();

            chargerTypes();
            txtNom.clear();

        } catch (Exception e) {
            alert("Ce type existe déjà !");
        }
    }

    @FXML
    private void modifierType() {

        Type selection = tableType.getSelectionModel().getSelectedItem();

        if (selection == null) {
            alert("Sélectionnez un type");
            return;
        }

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE type SET nom = ? WHERE id = ?")) {

            ps.setString(1, txtNom.getText().toUpperCase());
            ps.setInt(2, selection.getId());
            ps.executeUpdate();

            chargerTypes();
            txtNom.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerType() {

        Type selection = tableType.getSelectionModel().getSelectedItem();

        if (selection == null) {
            alert("Sélectionnez un type");
            return;
        }

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM type WHERE id = ?")) {

            ps.setInt(1, selection.getId());
            ps.executeUpdate();

            chargerTypes();
            txtNom.clear();

        } catch (Exception e) {
            alert("Impossible de supprimer (type utilisé dans motif)");
        }
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}