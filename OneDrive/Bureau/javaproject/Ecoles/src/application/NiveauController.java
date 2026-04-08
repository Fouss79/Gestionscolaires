package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class NiveauController {

    @FXML
    private TextField txtNiveau;

    @FXML
    private TableView<Niveau> tableNiveaux;

    @FXML
    private TableColumn<Niveau, String> colNom;

    private ObservableList<Niveau> listeNiveaux = FXCollections.observableArrayList();

    // Connexion à la base
   
    @FXML
    public void initialize() {
        // Mapper la colonne
        colNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());

        // Charger les niveaux existants
        chargerNiveaux();
    }

    @FXML
    private void ajouterNiveau() {
        String nom = txtNiveau.getText().trim();
        if (nom.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Veuillez saisir le nom du niveau !").show();
            return;
        }

        String sql = "INSERT INTO niveau (nom) VALUES (?)";

        try (Connection conn = Database.connect();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            stmt.executeUpdate();

            Niveau n = new Niveau(nom);
            listeNiveaux.add(n);
            tableNiveaux.setItems(listeNiveaux);

            txtNiveau.clear();
            new Alert(Alert.AlertType.INFORMATION, "Niveau ajouté avec succès !").show();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l’ajout !").show();
        }
    }

    private void chargerNiveaux() {
        listeNiveaux.clear();
        String sql = "SELECT nom FROM niveau";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nom = rs.getString("nom");
                listeNiveaux.add(new Niveau(nom));
            }

            tableNiveaux.setItems(listeNiveaux);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
