package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HabilitationController {

    @FXML
    private ComboBox<String> comboProf;

    @FXML
    private ComboBox<String> comboMatiere;

    @FXML
    private ComboBox<String> cbAnnee;

    @FXML
    private Button btnHabiliter;

    @FXML
    private TableView<HabilitationItem> tableHabilitations;

    @FXML
    private TableColumn<HabilitationItem, String> colEnseignant;

    @FXML
    private TableColumn<HabilitationItem, String> colMatiere;

    @FXML
    private TableColumn<HabilitationItem, String> colAnnee;

 
    // Liste pour TableView
    private final ObservableList<HabilitationItem> habilitations = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        chargerComboBox();
        initialiserTableView();
        chargerHabilitations();
    }
    
    private void chargerEnseignantsParAnnee() {

        if (cbAnnee.getValue() == null) return;

        int anneeId = Integer.parseInt(cbAnnee.getValue().split(" - ")[0]);

        ObservableList<String> enseignants = FXCollections.observableArrayList();

        String sql = """
            SELECT id, nom, prenom, specialite
            FROM enseignant
            WHERE annee_id = ?
            ORDER BY nom
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, anneeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                enseignants.add(
                    rs.getInt("id") + " - " +
                    rs.getString("nom") + " " +
                    rs.getString("prenom") + " " +
                    rs.getString("specialite")
                );
            }

            comboProf.setItems(enseignants);

        } catch (SQLException e) {
        	afficherAlerte("Erreur", "Impossible de charger les données.");     }
    }

    // ✅ Méthode générique pour changer de page
    private void changerScene(String fxml, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) comboProf.getScene().getWindow();
            stage.setScene(new Scene(root, 1283, 657));
            stage.setTitle(titre);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void retourAccueil() {
        
    }
    
    @FXML
    void openEmargement() {
    	changerScene("EmargementView.fxml", "Gestion des Emargements");
    }
    
    
    @FXML private void openClasse() {  changerScene("ClasseView.fxml", "Gestion des Classes");}
    @FXML private void openEleve() {changerScene("ElevesView.fxml", "Gestion des Élèves"); }
    @FXML private void openMatiere() {changerScene("Matiere.fxml", "Gestion des Matieres"); }
    @FXML private void openNote() { changerScene("NoteView.fxml", "Gestion des Matieres");}
    @FXML private void openEnseignant() { changerScene("EnseignantView.fxml", "Gestion des Enseignants");}
    @FXML private void openTransaction() { changerScene("PaiementEnseignant.fxml", "Gestion des Transactions");}
    @FXML private void openBulletin() { changerScene("ClasseView.fxml", "Gestion des Bulletins");}
    @FXML private void openEmploidutemps() { changerScene("EmploiDutempsView.fxml", "Gestion des Emplois du temps");}
    @FXML private void openComptabilite() { changerScene("EnseignementView.fxml", "Gestion des Cours"); }


    @FXML
    void openHabilitation() {
    	 changerScene("HabilitationView.fxml", "Habilitation");
    }

    
    // 🔹 Charger les ComboBox depuis la base
    private void chargerComboBox() {
    	
        try (Connection conn = Database.connect()) {

            // Enseignants
            ObservableList<String> enseignants = FXCollections.observableArrayList();
            ResultSet rsProf = conn.createStatement().executeQuery(
                    "SELECT * FROM enseignant"
            );
            while (rsProf.next()) {
                enseignants.add(rsProf.getInt("id") + " - " +
                        rsProf.getString("nom") + " " +
                        rsProf.getString("prenom")+ " "+
                        rsProf.getString("specialite"));
            }
            comboProf.setItems(enseignants);

            // Matières
            ObservableList<String> matieres = FXCollections.observableArrayList();
            ResultSet rsMat = conn.createStatement().executeQuery("SELECT id, nom FROM matiere");
            while (rsMat.next()) {
                matieres.add(rsMat.getInt("id") + " - " + rsMat.getString("nom"));
            }
            comboMatiere.setItems(matieres);

            // Années scolaires
            ObservableList<String> annees = FXCollections.observableArrayList();
            ResultSet rsAnnee = conn.createStatement().executeQuery(
                    "SELECT id, libelle FROM anneescolaire ORDER BY libelle DESC"
            );
            while (rsAnnee.next()) {
                annees.add(rsAnnee.getInt("id") + " - " + rsAnnee.getString("libelle"));
            }
            cbAnnee.setItems(annees);

        } catch (SQLException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger les données.");
        }
    }

    // 🔹 Initialiser TableView
    private void initialiserTableView() {
        colEnseignant.setCellValueFactory(new PropertyValueFactory<>("enseignant"));
        colMatiere.setCellValueFactory(new PropertyValueFactory<>("matiere"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));

        tableHabilitations.setItems(habilitations);
    }

    // 🔹 Charger toutes les habilitations depuis la base
    private void chargerHabilitations() {
        habilitations.clear();
        try (Connection conn = Database.connect()) {
            String sql = "SELECT h.id, e.nom AS nomProf, e.prenom AS prenomProf, m.nom AS nomMatiere, a.libelle AS annee " +
                    "FROM habilitation h " +
                    "JOIN enseignant e ON h.enseignant_id = e.id " +
                    "JOIN matiere m ON h.matiere_id = m.id " +
                    "JOIN anneescolaire a ON h.annee_id = a.id " +
                    "ORDER BY h.id DESC";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                String enseignant = rs.getString("nomProf") + " " + rs.getString("prenomProf");
                String matiere = rs.getString("nomMatiere");
                String annee = rs.getString("annee");
                habilitations.add(new HabilitationItem(enseignant, matiere, annee));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void AjouterHabilitation() {

        if (comboProf.getValue() == null ||
            comboMatiere.getValue() == null ||
            cbAnnee.getValue() == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner l'enseignant, la matière et l'année.");
            return;
        }

        int enseignantId = Integer.parseInt(comboProf.getValue().split(" - ")[0]);
        int matiereId = Integer.parseInt(comboMatiere.getValue().split(" - ")[0]);
        int anneeId = Integer.parseInt(cbAnnee.getValue().split(" - ")[0]);

        try (Connection conn = Database.connect();) {

            PreparedStatement psCheck = conn.prepareStatement(
                    "SELECT * FROM habilitation WHERE enseignant_id=? AND matiere_id=? AND annee_id=?"
            );
            psCheck.setInt(1, enseignantId);
            psCheck.setInt(2, matiereId);
            psCheck.setInt(3, anneeId);

            ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next()) {
                afficherAlerte("Information", "Cette habilitation existe déjà.");
                return;
            }

            PreparedStatement psInsert = conn.prepareStatement(
                    "INSERT INTO habilitation (enseignant_id, matiere_id, annee_id) VALUES (?, ?, ?)"
            );
            psInsert.setInt(1, enseignantId);
            psInsert.setInt(2, matiereId);
            psInsert.setInt(3, anneeId);
            psInsert.executeUpdate();

            afficherAlerte("Succès", "Habilitation ajoutée avec succès.");

            // 🔹 Rafraîchir le tableau
            chargerHabilitations();

        } catch (SQLException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Erreur lors de l'ajout de l'habilitation.");
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // 🔹 Classe interne pour TableView
    public static class HabilitationItem {
        private final String enseignant;
        private final String matiere;
        private final String annee;

        public HabilitationItem(String enseignant, String matiere, String annee) {
            this.enseignant = enseignant;
            this.matiere = matiere;
            this.annee = annee;
        }

        public String getEnseignant() { return enseignant; }
        public String getMatiere() { return matiere; }
        public String getAnnee() { return annee; }
    }
}
