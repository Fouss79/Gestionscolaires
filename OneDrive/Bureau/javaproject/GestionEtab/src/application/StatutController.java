package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class StatutController {

    @FXML
    private TextField txtStatut;

    @FXML
    private TableView<Niveau> tableStatut;

    @FXML
    private TableColumn<Niveau, String> colNom;

    private ObservableList<Niveau> listeStatuts = FXCollections.observableArrayList();

    // Connexion à la base
    private final String URL = "jdbc:mysql://localhost:3306/gestionEcole";
    private final String USER = "root";
    private final String PASSWORD = "";

    @FXML
    public void initialize() {
        // Mapper la colonne
        colNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());

        // Charger les niveaux existants
        chargerStatut();
    }

    @FXML
    private void ajouterNiveau() {
        String nom = txtStatut.getText().trim();
        if (nom.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Veuillez saisir le nom du niveau !").show();
            return;
        }

        String sql = "INSERT INTO statut (nom) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            stmt.executeUpdate();

            Niveau n = new Niveau(nom);
            listeStatuts.add(n);
            tableStatut.setItems(listeStatuts);

            txtStatut.clear();
            new Alert(Alert.AlertType.INFORMATION, "Statut ajouté avec succès !").show();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l’ajout !").show();
        }
    }

    private void chargerStatut() {
        listeStatuts.clear();
        String sql = "SELECT nom FROM statut";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nom = rs.getString("nom");
                listeStatuts.add(new Niveau(nom));
            }

            tableStatut.setItems(listeStatuts);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
