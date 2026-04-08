package application;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class MotifController {

    @FXML private TextField txtNom;
    @FXML private ComboBox<Type> cbType;
    @FXML private TableView<Motif> tableMotif;
    @FXML private TableColumn<Motif, Integer> colId;
    @FXML private TableColumn<Motif, String> colNom;
    @FXML private TableColumn<Motif, String> colType;

    private ObservableList<Motif> liste = FXCollections.observableArrayList();
    private ObservableList<Type> listeTypes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeNom"));

        chargerTypes();
        chargerMotifs();

        cbType.setItems(listeTypes);

        tableMotif.setOnMouseClicked(e -> {
            Motif m = tableMotif.getSelectionModel().getSelectedItem();
            if (m != null) {
                txtNom.setText(m.getNom());
                for (Type t : listeTypes) {
                    if (t.getId() == m.getTypeId()) {
                        cbType.setValue(t);
                        break;
                    }
                }
            }
        });
    }

    private void chargerTypes() {
        listeTypes.clear();
        try (Connection conn = Database.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM type")) {

            while (rs.next()) {
                listeTypes.add(new Type(
                        rs.getInt("id"),
                        rs.getString("nom")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chargerMotifs() {
        liste.clear();
        String sql = """
                SELECT m.id, m.nom, m.type_id, t.nom as type_nom
                FROM motif m
                JOIN type t ON m.type_id = t.id
                """;

        try (Connection conn = Database.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                liste.add(new Motif(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("type_id"),
                        rs.getString("type_nom")
                ));
            }

            tableMotif.setItems(liste);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterMotif() {

        if (txtNom.getText().isEmpty() || cbType.getValue() == null) {
            alert("Remplissez tous les champs");
            return;
        }

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO motif(nom, type_id) VALUES (?, ?)")) {

            ps.setString(1, txtNom.getText().toUpperCase());
            ps.setInt(2, cbType.getValue().getId());
            ps.executeUpdate();

            chargerMotifs();
            txtNom.clear();

        } catch (Exception e) {
            alert("Motif déjà existant !");
        }
    }

    @FXML
    private void modifierMotif() {

        Motif selection = tableMotif.getSelectionModel().getSelectedItem();

        if (selection == null) {
            alert("Sélectionnez un motif");
            return;
        }

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE motif SET nom=?, type_id=? WHERE id=?")) {

            ps.setString(1, txtNom.getText().toUpperCase());
            ps.setInt(2, cbType.getValue().getId());
            ps.setInt(3, selection.getId());
            ps.executeUpdate();

            chargerMotifs();
            txtNom.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerMotif() {

        Motif selection = tableMotif.getSelectionModel().getSelectedItem();

        if (selection == null) {
            alert("Sélectionnez un motif");
            return;
        }

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM motif WHERE id=?")) {

            ps.setInt(1, selection.getId());
            ps.executeUpdate();

            chargerMotifs();
            txtNom.clear();

        } catch (Exception e) {
            alert("Impossible de supprimer");
        }
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}