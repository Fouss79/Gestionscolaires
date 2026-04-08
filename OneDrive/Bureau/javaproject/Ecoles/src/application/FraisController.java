package application;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class FraisController {

    @FXML private ComboBox<String> cbMotif;
    @FXML private ComboBox<String> cbNiveau;
    @FXML private TextField txtMontant;

    @FXML private TableView<Frais> tableTarifs;
    @FXML private TableColumn<Frais, String> colMotif;
    @FXML private TableColumn<Frais, String> colAnnee;
    @FXML private TableColumn<Frais, Double> colMontant;
    @FXML private TableColumn<Frais, String> colNiveau;

    private ObservableList<Frais> listeFrais = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));

        chargerMotifs();
        chargerNiveau();
        chargerFrais();
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
    private void chargerNiveau() {

        String sql = "SELECT nom FROM niveau ORDER BY nom";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cbNiveau.getItems().add(rs.getString("nom"));
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
    private void ajouterFrais() {

        String motifNom = cbMotif.getValue();
        String montantStr = txtMontant.getText();
        String niveau = cbNiveau.getValue();
        

        if (motifNom == null || niveau == null || montantStr.isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                    "Veuillez remplir tous les champs.")
                    .show();
            return;
        }

        double montant = Double.parseDouble(montantStr);

        String sqlMotif = "SELECT id FROM motif WHERE nom = ?";
        String sqlNiveau = "SELECT id FROM niveau WHERE nom = ?";
        
        String sqlInsert = "INSERT INTO frais (motif_id, annee_id, montant,niveau_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.connect()) {

            // récupérer id motif
            PreparedStatement psMotif = conn.prepareStatement(sqlMotif);
            psMotif.setString(1, motifNom);
            ResultSet rs = psMotif.executeQuery();

            if (!rs.next()) return;

         // récupérer id Niveau
            PreparedStatement psNiveau = conn.prepareStatement(sqlNiveau);
            psNiveau.setString(1, niveau);
            ResultSet rss = psNiveau.executeQuery();

            if (!rss.next()) return;
            

            int motifId = rs.getInt("id");
            int NiveauId = rss.getInt("id");
            int anneeId = getAnneeActiveId();
            
            

            PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
            psInsert.setInt(1, motifId);
            psInsert.setInt(2, anneeId);
            psInsert.setDouble(3, montant);
            psInsert.setInt(4, NiveauId);
            psInsert.executeUpdate();

            new Alert(Alert.AlertType.INFORMATION,
                    "Frais enregistré avec succès.")
                    .show();

            txtMontant.clear();
            chargerFrais();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Charger tarifs
    private void chargerFrais() {

        listeFrais.clear();

        String sql = """
            SELECT f.id, m.nom AS motif, n.nom AS niveau, a.libelle AS annee, f.montant
            FROM frais f
            JOIN motif m ON f.motif_id = m.id
            JOIN niveau n ON f.niveau_id = n.id
            JOIN anneescolaire a ON f.annee_id = a.id
            WHERE a.active = 1
        """;

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Frais frais = new Frais(
                        rs.getInt("id"),
                        rs.getString("motif"),
                        rs.getString("annee"),
                        rs.getDouble("montant"),
                        rs.getString("niveau")
                );

                listeFrais.add(frais);
            }

            tableTarifs.setItems(listeFrais);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
