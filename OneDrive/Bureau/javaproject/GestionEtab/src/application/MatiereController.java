package application;

import java.io.IOException;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MatiereController {

    // 🔹 Configuration de la base de données
    private static final String URL = "jdbc:mysql://localhost:3306/GestionEcole";
    private static final String UTILISATEUR = "root";
    private static final String MOTDEPASSE = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, UTILISATEUR, MOTDEPASSE);
    }

    // 🔹 Composants FXML
    @FXML private TextField txtNom;
    @FXML private TableView<Matiere> tableMatieres;
    @FXML private TableColumn<Matiere, String> colId;
    @FXML private TableColumn<Matiere, String> colNom;

    private ObservableList<Matiere> matieres = FXCollections.observableArrayList();

    // ───────────────────────────────────────────────────────────────
    // 🔹 Initialisation
    // ───────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        try {
            chargerMatieres();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ───────────────────────────────────────────────────────────────
    // 🔹 Charger les matières
    // ───────────────────────────────────────────────────────────────
    private void chargerMatieres() throws SQLException {
        matieres.clear();
        String requete = "SELECT * FROM matiere";

        try (Connection connexion = getConnection();
             PreparedStatement stmt = connexion.prepareStatement(requete);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String nom = rs.getString("nom");
                matieres.add(new Matiere(id, nom));
            }
        }

        tableMatieres.setItems(matieres);
    }

    
    
    
    // ───────────────────────────────────────────────────────────────
    // 🔹 Ajouter une matière
    // ───────────────────────────────────────────────────────────────
    
   
    
    @FXML
    private void ajouterMatiere1() {
   ;
        String nom = txtNom.getText();

        if (nom.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Veuillez saisir le nom de la matière !").show();
            return;
        }

        String req = "INSERT INTO matiere (nom) VALUES (?)";

        try (Connection connexion = getConnection();
             PreparedStatement stmt = connexion.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nom);
            int n = stmt.executeUpdate();

            if (n > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                String id = null;
                if (keys.next()) id = keys.getString(1);

                matieres.add(new Matiere(id, nom));
                tableMatieres.refresh();
                new Alert(Alert.AlertType.INFORMATION, "Matière ajoutée avec succès !").show();
            }

            txtNom.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l’ajout de la matière !").show();
        }
    }

    // ───────────────────────────────────────────────────────────────
    // 🔹 Supprimer une matière
    // ───────────────────────────────────────────────────────────────
    @FXML
    private void supprimerMatiere(ActionEvent event) {
        Matiere selected = tableMatieres.getSelectionModel().getSelectedItem();

        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une matière à supprimer !").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer la matière " + selected.getNom() + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            String sql = "DELETE FROM matiere WHERE id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, selected.getId());
                stmt.executeUpdate();
                matieres.remove(selected);
                tableMatieres.refresh();

                new Alert(Alert.AlertType.INFORMATION, "Matière supprimée avec succès !").show();
            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression !").show();
            }
        }
    }

    // ───────────────────────────────────────────────────────────────
    // 🔹 Modifier une matière
    // ───────────────────────────────────────────────────────────────
    @FXML
    private void modifierMatiere(ActionEvent event) {
        Matiere selected = tableMatieres.getSelectionModel().getSelectedItem();

        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une matière à modifier !").show();
            return;
        }

        TextInputDialog dialogNom = new TextInputDialog(selected.getNom());
        dialogNom.setTitle("Modifier Matière");
        dialogNom.setHeaderText("Modifier la matière sélectionnée");
        dialogNom.setContentText("Nouveau nom :");
        String nouveauNom = dialogNom.showAndWait().orElse(null);

        if (nouveauNom != null && !nouveauNom.isBlank()) {
            String sql = "UPDATE matiere SET nom = ? WHERE id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, nouveauNom);
                stmt.setString(2, selected.getId());
                stmt.executeUpdate();

                selected.setNom(nouveauNom);
                tableMatieres.refresh();
                new Alert(Alert.AlertType.INFORMATION, "Matière modifiée avec succès !").show();

            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification !").show();
            }
        }
    }

    // ───────────────────────────────────────────────────────────────
    // 🔹 Navigation entre les pages
    // ───────────────────────────────────────────────────────────────
    private void changerScene(ActionEvent event, String fxml, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1283, 657));
            stage.setTitle(titre);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void openEmargement(ActionEvent event) {
    	changerScene(event,"EmargementView.fxml", "Gestion des Emargements");

    }
    @FXML private void retourAccueil() { System.out.println("Retour au Tableau de Bord"); }
    @FXML private void openClasse(ActionEvent event) { changerScene(event,"ClasseView.fxml", "Gestion des Classes"); }
    @FXML private void openEleve(ActionEvent event) { changerScene(event,"EleveForm.fxml", "Gestion des Élèves"); }
    @FXML private void openMatiere(ActionEvent event) { changerScene(event,"Matiere.fxml", "Gestion des Matières"); }
    @FXML private void openNote(ActionEvent event) { changerScene(event,"NoteView.fxml", "Gestion des Notes"); }
    @FXML private void openEnseignant(ActionEvent event) { changerScene(event,"EnseignantView.fxml", "Gestion des Enseignants"); }
    @FXML private void openTransaction(ActionEvent event) { changerScene(event,"PaiementEnseignant.fxml", "Gestion des Transactions"); }
    @FXML private void openBulletin(ActionEvent event) { changerScene(event,"BulletinView.fxml", "Gestion des Bulletins"); }
    @FXML private void openEmploidutemps(ActionEvent event) { changerScene(event,"EmploiDutempsView.fxml", "Gestion des Emplois du Temps"); }
    @FXML private void openComptabilite(ActionEvent event) { changerScene(event,"EnseignementView.fxml", "Gestion des Enseignements"); }

    // ───────────────────────────────────────────────────────────────
    // 🔹 Classe interne Matiere
    // ───────────────────────────────────────────────────────────────
    public static class Matiere {
        private String id;
        private String nom;

        public Matiere(String id, String nom) {
            this.id = id;
            this.nom = nom;
        }

        public String getId() { return id; }
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
    }
}
