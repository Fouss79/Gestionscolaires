package application;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class TarifController {

    @FXML private ComboBox<String> cbMotif;
    @FXML private TextField txtMontant;

    @FXML private TableView<Tarif> tableTarifs;
    @FXML private TableColumn<Tarif, String> colMotif;
    @FXML private TableColumn<Tarif, String> colAnnee;
    @FXML private TableColumn<Tarif, Double> colMontant;

    private ObservableList<Tarif> listeTarifs = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));

        chargerMotifs();
        chargerTarifs();
    }

    // 🔹 Charger motifs dans ComboBox
    private void chargerMotifs() {

        String sql = "SELECT nom FROM motif ORDER BY nom";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cbMotif.getItems().add(rs.getString("nom"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Récupérer ID année active
    private int getAnneeActiveId() {

        String sql = "SELECT id FROM anneescolaire WHERE active = 1 LIMIT 1";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // 🔹 Ajouter tarif
    @FXML
    private void ajouterTarif() {

        String motifNom = cbMotif.getValue();
        String montantStr = txtMontant.getText();

        if (motifNom == null || montantStr.isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                    "Veuillez remplir tous les champs.")
                    .show();
            return;
        }

        double montant = Double.parseDouble(montantStr);

        String sqlMotif = "SELECT id FROM motif WHERE nom = ?";
        String sqlInsert = "INSERT INTO tarif (motif_id, annee_id, montant) VALUES (?, ?, ?)";

        try (Connection conn = Database.connect()) {

            // récupérer id motif
            PreparedStatement psMotif = conn.prepareStatement(sqlMotif);
            psMotif.setString(1, motifNom);
            ResultSet rs = psMotif.executeQuery();

            if (!rs.next()) return;

            int motifId = rs.getInt("id");
            int anneeId = getAnneeActiveId();

            PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
            psInsert.setInt(1, motifId);
            psInsert.setInt(2, anneeId);
            psInsert.setDouble(3, montant);
            psInsert.executeUpdate();

            new Alert(Alert.AlertType.INFORMATION,
                    "Tarif enregistré avec succès.")
                    .show();

            txtMontant.clear();
            chargerTarifs();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Charger tarifs
    private void chargerTarifs() {

        listeTarifs.clear();

        String sql = """
            SELECT t.id, m.nom AS motif, a.libelle AS annee, t.montant
            FROM tarif t
            JOIN motif m ON t.motif_id = m.id
            JOIN anneescolaire a ON t.annee_id = a.id
            WHERE a.active = 1
        """;

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Tarif tarif = new Tarif(
                        rs.getInt("id"),
                        rs.getString("motif"),
                        rs.getString("annee"),
                        rs.getDouble("montant")
                );

                listeTarifs.add(tarif);
            }

            tableTarifs.setItems(listeTarifs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}