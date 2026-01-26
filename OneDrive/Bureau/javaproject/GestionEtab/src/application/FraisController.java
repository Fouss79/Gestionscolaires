package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FraisController {

    @FXML private ComboBox<String> cmbMotif;
    @FXML private ComboBox<String> cmbAnneeScolaire;
    @FXML private ComboBox<String> cmbNiveau;
    @FXML private TextField txtMontant;

    @FXML private TableView<Frais> tableFrais;
    @FXML private TableColumn<Frais, String> colMotif;
    @FXML private TableColumn<Frais, Double> colMontant;
    @FXML private TableColumn<Frais, String> colAnnee;
    @FXML private TableColumn<Frais, String> colNiveau;

    private ObservableList<Frais> listeFrais = FXCollections.observableArrayList();

    private final String URL = "jdbc:mysql://localhost:3306/gestionecole";
    private final String USER = "root";
    private final String PASSWORD = "";

    @FXML
    public void initialize() {

        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("anneeScolaire"));
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));

        chargerComboBox();
        chargerFrais();

        // Par défaut : désactiver Niveau
     

        // activer niveau seulement si motif = inscription ou mensualité
     
    }

    private void chargerComboBox() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            ResultSet rs = conn.createStatement().executeQuery("SELECT nom FROM motif");
            ObservableList<String> motifs = FXCollections.observableArrayList();
            while (rs.next()) motifs.add(rs.getString("nom"));
            cmbMotif.setItems(motifs);

            rs = conn.createStatement().executeQuery("SELECT libelle FROM anneeScolaire");
            ObservableList<String> annees = FXCollections.observableArrayList();
            while (rs.next()) annees.add(rs.getString("nom"));
            cmbAnneeScolaire.setItems(annees);

            rs = conn.createStatement().executeQuery("SELECT nom FROM niveau");
            ObservableList<String> niveau = FXCollections.observableArrayList();
            while (rs.next()) niveau.add(rs.getString("nom"));
            cmbNiveau.setItems(niveau);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerFrais() {
        listeFrais.clear();
        String sql = "SELECT motif, montant, anneeScolaire,niveau FROM frais";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             ResultSet rs = conn.createStatement().executeQuery(sql)) {

            while (rs.next()) {
                listeFrais.add(new Frais(
                        rs.getString("motif"),
                        rs.getDouble("montant"),
                        rs.getString("anneeScolaire"),
                        rs.getString("niveau")
                ));
            }
            tableFrais.setItems(listeFrais);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterFrais() {

        if (cmbMotif.getValue() == null || cmbAnneeScolaire.getValue()==null || txtMontant.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.").show();
            return;
        }

        // Vérifier niveau si nécessaire
        if ((cmbMotif.getValue().equalsIgnoreCase("inscription") || cmbMotif.getValue().equalsIgnoreCase("mensualité"))
                && cmbNiveau.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez choisir un niveau pour ce type de frais.").show();
            return;
        }

        double montant;

        try {
            montant = Double.valueOf(txtMontant.getText());
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Montant invalide !").show();
            return;
        }

        String sql = "INSERT INTO frais(motif, montant, anneeScolaire,niveau) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, cmbMotif.getValue());
            pst.setDouble(2, montant);
            pst.setString(3, cmbAnneeScolaire.getValue());
            pst.setString(4,cmbNiveau.getValue());

            pst.executeUpdate();

            new Alert(Alert.AlertType.INFORMATION, "Frais enregistrés avec succès !").show();

            chargerFrais();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l’enregistrement !").show();
        }
    }
}
