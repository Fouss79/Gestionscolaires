package application;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AnneeScolaireController implements Initializable {

    @FXML private TextField txtLibelle;
    @FXML private DatePicker dpDebut;
    @FXML private DatePicker dpFin;

    @FXML private TableView<AnneeScolaire> table;
    @FXML private TableColumn<AnneeScolaire, String> colLibelle;
    @FXML private TableColumn<AnneeScolaire, LocalDate> colDebut;
    @FXML private TableColumn<AnneeScolaire, LocalDate> colFin;
    @FXML private TableColumn<AnneeScolaire, Boolean> colActive;

    private ObservableList<AnneeScolaire> annees = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colLibelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        colDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colActive.setCellValueFactory(new PropertyValueFactory<>("active"));

        table.setItems(annees);

        // Charger les années depuis MySQL
        chargerAnnees();
    }

    // ================= CHARGER ANNEES =================
    private void chargerAnnees() {
        annees.clear();

        String sql = "SELECT * FROM anneescolaire";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String libelle = rs.getString("libelle");
                LocalDate debut = rs.getDate("date_debut").toLocalDate();
                LocalDate fin = rs.getDate("date_fin").toLocalDate();
                boolean active = rs.getBoolean("active");

                annees.add(new AnneeScolaire(id, libelle, debut, fin, active));
            }

        } catch (Exception e) {
            e.printStackTrace();
            alert("Erreur lors du chargement des années depuis MySQL");
        }
    }

    // ================= AJOUTER ANNEE =================
    @FXML
    private void ajouterAnnee() {

        String libelle = txtLibelle.getText();
        LocalDate debut = dpDebut.getValue();
        LocalDate fin = dpFin.getValue();

        if (libelle.isEmpty() || debut == null || fin == null) {
            alert("Veuillez remplir tous les champs");
            return;
        }

        String sql = "INSERT INTO anneescolaire(libelle, date_debut, date_fin, active) VALUES(?,?,?,0)";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, libelle);
            ps.setDate(2, Date.valueOf(debut));
            ps.setDate(3, Date.valueOf(fin));
            ps.executeUpdate();

            chargerAnnees();

            txtLibelle.clear();
            dpDebut.setValue(null);
            dpFin.setValue(null);

        } catch (Exception e) {
            e.printStackTrace();
            alert("Erreur lors de l'ajout de l'année");
        }
    }

    // ================= ACTIVER ANNEE =================
    @FXML
    private void activerAnnee() {

        AnneeScolaire selection = table.getSelectionModel().getSelectedItem();

        if (selection == null) {
            alert("Sélectionnez une année à activer");
            return;
        }

        try (Connection c = DB.getConnection()) {

            c.setAutoCommit(false);

            // Désactiver toutes les années
            try (PreparedStatement ps1 = c.prepareStatement("UPDATE annee_scolaire SET active = 0")) {
                ps1.executeUpdate();
            }

            // Activer la sélection
            try (PreparedStatement ps2 = c.prepareStatement(
                    "UPDATE annee_scolaire SET active = 1 WHERE id = ?")) {
                ps2.setLong(1, selection.getId());
                ps2.executeUpdate();
            }

            c.commit();

            chargerAnnees();

        } catch (Exception e) {
            e.printStackTrace();
            alert("Erreur lors de l'activation de l'année");
        }
    }

    // ================= UTILITAIRES =================
    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // ================= ANNEE ACTIVE =================
    public AnneeScolaire getAnneeActive() {
        return annees.stream()
                .filter(AnneeScolaire::isActive)
                .findFirst()
                .orElse(null);
    }
}
