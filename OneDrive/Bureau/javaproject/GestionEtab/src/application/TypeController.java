package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class TypeController {

    @FXML private TableView<Type> tableTypes;
    @FXML private TableColumn<Type, Integer> colId;
    @FXML private TableColumn<Type, String> colNom;

    @FXML private TextField txtNom;

    private ObservableList<Type> types = FXCollections.observableArrayList();

    // ⚡ Paramètres JDBC
    private final String URL = "jdbc:mysql://localhost:3306/GestionEcole";
    private final String USER = "root";
    private final String PASSWORD = "";

    @FXML
    public void initialize() {
        // Associer colonnes aux propriétés
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        colNom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));

        tableTypes.setItems(types);

        chargerTypes();
    }

    // Charger la liste des types
    private void chargerTypes() {
        types.clear();
        String sql = "SELECT * FROM type";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Type t = new Type();
                t.setId(rs.getInt("id"));
                t.setNom(rs.getString("nom"));
                types.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ajouter un type
    @FXML
    private void handleAjouter() {
        String nom = txtNom.getText();
        if (nom.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Veuillez saisir un nom !").show();
            return;
        }

        String sql = "INSERT INTO type (nom) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.executeUpdate();
            new Alert(Alert.AlertType.INFORMATION, "Type ajouté avec succès !").show();
            chargerTypes();
            txtNom.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage()).show();
        }
    }

    // Supprimer le type sélectionné
    @FXML
    private void handleSupprimer() {
        Type selected = tableTypes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez un type à supprimer !").show();
            return;
        }

        String sql = "DELETE FROM type WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, selected.getId());
            ps.executeUpdate();
            new Alert(Alert.AlertType.INFORMATION, "Type supprimé avec succès !").show();
            chargerTypes();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage()).show();
        }
    }
}
