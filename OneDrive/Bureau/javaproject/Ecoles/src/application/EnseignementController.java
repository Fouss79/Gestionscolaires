package application;

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

import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class EnseignementController {

  
    
    @FXML
    private Button btnClasse;

    @FXML
    private Button btnComptabilite;

    @FXML
    private Button btnEleve;

    @FXML
    private Button btnEmargement;

    @FXML
    private Button btnEmploidutemps;

    @FXML
    private Button btnEnseignant;

    @FXML
    private Button btnMatiere;

    @FXML
    private Button btnNote;
      

    @FXML
    private Button btnTransaction;

    @FXML private ComboBox<String> cmbEnseignant;
    @FXML private ComboBox<String> cmbClasse;
    @FXML private ComboBox<String> cmbMatiere;
    @FXML private ComboBox<String> cmbCoeff;
    @FXML private ComboBox<String> cbAnnee;

    @FXML private TableView<Enseignement> tableEnseignement;
    @FXML private TableColumn<Enseignement, Integer> colId;
    @FXML private TableColumn<Enseignement, String> colEnseignant;
    @FXML private TableColumn<Enseignement, String> colClasse;
    @FXML private TableColumn<Enseignement, String> colMatiere;
    @FXML private TableColumn<Enseignement, String> colCoeff;
    @FXML private TableColumn<Enseignement, Integer> colAnnee;


    @FXML private Button btnAjouter;
    @FXML private Button btnSupprimer;
    
    

    // ✅ Méthode générique pour changer de page
    private void changerScene(String fxml, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) cmbMatiere.getScene().getWindow();
            stage.setScene(new Scene(root, 1283, 657));
            stage.setTitle(titre);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void openHabilitation(ActionEvent event) {
    	 changerScene("HabilitationView.fxml", "Habilitation");
    }
    @FXML
    private void retourAccueil() {
        
    }
    @FXML
    void openEmargement(ActionEvent event) {
    	changerScene("EmargementView.fxml", "Gestion des Emargements");
    }
    
    
    @FXML private void openClasse(ActionEvent event) {  changerScene("ClasseView.fxml", "Gestion des Classes");}
    @FXML private void openEleve(ActionEvent event) {changerScene("ElevesView.fxml", "Gestion des Élèves"); }
    @FXML private void openMatiere(ActionEvent event) {changerScene("Matiere.fxml", "Gestion des Matieres"); }
    @FXML private void openNote(ActionEvent event) { changerScene("NoteView.fxml", "Gestion des Matieres");}
    @FXML private void openEnseignant(ActionEvent event) { changerScene("EnseignantView.fxml", "Gestion des Enseignants");}
    @FXML private void openTransaction(ActionEvent event) { changerScene("PaiementEnseignant.fxml", "Gestion des Transactions");}
    @FXML private void openBulletin(ActionEvent event) { changerScene("ClasseView.fxml", "Gestion des Bulletins");}
    @FXML private void openEmploidutemps(ActionEvent event) { changerScene("EmploiDutempsView.fxml", "Gestion des Emplois du temps");}
    @FXML private void openComptabilite(ActionEvent event) { changerScene("ClasseView.fxml", "Gestion des Finances"); }


    private ObservableList<Enseignement> data = FXCollections.observableArrayList();

    // 🔹 Initialisation du controller
    @FXML
    public void initialize() {
        // Initialisation des colonnes du tableau
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colClasse.setCellValueFactory(new PropertyValueFactory<>("classe"));
        colMatiere.setCellValueFactory(new PropertyValueFactory<>("matiere"));
        colCoeff.setCellValueFactory(new PropertyValueFactory<>("coeff"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));

        tableEnseignement.setItems(data);

        // Charger les ComboBox et le tableau
        chargerComboBox();
        chargerDonnees();
    }
   
  
    // 🔹 Charger les données dans le tableau
    private void chargerDonnees() {

        data.clear();
        String sql = """
        	       SELECT e.id,
       n.nom AS niveau,
       s.nom AS serie,
       g.nom AS groupe,
       m.nom AS matiere,
       e.coeff,
       a.libelle AS annee
FROM enseignement e
JOIN classe c ON e.classe_id = c.id
LEFT JOIN niveau n ON c.niveau_id = n.id
LEFT JOIN serie s ON c.serie_id = s.id
LEFT JOIN groupe g ON c.groupe_id = g.id
JOIN matiere m ON e.matiere_id = m.id
JOIN anneescolaire a ON e.annee_id = a.id
WHERE a.active = 1;
        	""";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

        	while (rs.next()) {
        	    String classeFull = rs.getString("niveau") + " " 
        	                      + rs.getString("serie") + " " 
        	                      + rs.getString("groupe");

        	    data.add(new Enseignement(
        	        rs.getInt("id"),
        	        classeFull,
        	        rs.getString("matiere"),
        	        String.valueOf(rs.getInt("coeff")),
        	        rs.getString("annee")
        	    ));
        	}

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   
    // 🔹 Charger les ComboBox depuis la base
    private void chargerComboBox() {

        try (Connection conn = Database.connect()) {

            // 🔹 Classes (id - niveau serie groupe)
            ObservableList<String> classes = FXCollections.observableArrayList();
            String sqlClasse = """
                SELECT c.id,
                       n.nom AS niveau,
                       s.nom AS serie,
                       g.nom AS groupe
                FROM classe c
                LEFT JOIN niveau n ON c.niveau_id = n.id
                LEFT JOIN serie s ON c.serie_id = s.id
                LEFT JOIN groupe g ON c.groupe_id = g.id
            """;

            ResultSet rsClasse = conn.createStatement().executeQuery(sqlClasse);

            while (rsClasse.next()) {
                String classeFull = rsClasse.getString("niveau") + " " 
                                   + rsClasse.getString("serie") + " " 
                                   + rsClasse.getString("groupe");
                classes.add(rsClasse.getInt("id") + " - " + classeFull);
            }
            cmbClasse.setItems(classes);

            // 🔹 Matières (id - nom)
            ObservableList<String> matieres = FXCollections.observableArrayList();
            ResultSet rsMatiere = conn.createStatement()
                    .executeQuery("SELECT id, nom FROM matiere");

            while (rsMatiere.next()) {
                matieres.add(rsMatiere.getInt("id") + " - " + rsMatiere.getString("nom"));
            }
            cmbMatiere.setItems(matieres);

            // 🔹 Années scolaires
            ObservableList<String> annees = FXCollections.observableArrayList();
            ResultSet rsAnnee = conn.createStatement()
                    .executeQuery("SELECT id, libelle FROM anneescolaire ORDER BY libelle DESC");

            while (rsAnnee.next()) {
                annees.add(rsAnnee.getInt("id") + " - " + rsAnnee.getString("libelle"));
            }
            cbAnnee.setItems(annees);

            cmbCoeff.setItems(FXCollections.observableArrayList("1","2","3","4","5"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 🔹 Ajouter un enseignement
    @FXML
    void ajouterEnseignement(ActionEvent event) {

        String classeSelected = cmbClasse.getValue();
        String matiereSelected = cmbMatiere.getValue();
        String coeffStr = cmbCoeff.getValue();
        String anneeSelected = cbAnnee.getValue();

        // ✅ Vérification NULL AVANT split
        if (classeSelected == null || matiereSelected == null ||
            coeffStr == null || anneeSelected == null) {

            new Alert(Alert.AlertType.WARNING,
                    "Veuillez remplir tous les champs !").show();
            return;
        }

        // ✅ Extraction des ID
        int classeId = Integer.parseInt(classeSelected.split(" - ")[0]);
        int matiereId = Integer.parseInt(matiereSelected.split(" - ")[0]);
        int anneeId = Integer.parseInt(anneeSelected.split(" - ")[0]);
        int coeff = Integer.parseInt(coeffStr);

        try (Connection conn = Database.connect()) {

            // ✅ Vérification doublon
            String checkSql = """
                SELECT COUNT(*) FROM enseignement
                WHERE classe_id = ? AND matiere_id = ? AND annee_id = ?
            """;

            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, classeId);
                checkPs.setInt(2, matiereId);
                checkPs.setInt(3, anneeId);

                ResultSet rs = checkPs.executeQuery();
                rs.next();

                if (rs.getInt(1) > 0) {
                    new Alert(Alert.AlertType.WARNING,
                            "Cet enseignement existe déjà !").show();
                    return;
                }
            }

            // ✅ Insertion correcte (INTEGER = setInt)
            String insertSql = """
                INSERT INTO enseignement (classe_id, matiere_id, coeff, annee_id)
                VALUES (?, ?, ?, ?)
            """;

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, classeId);
                ps.setInt(2, matiereId);
                ps.setInt(3, coeff);
                ps.setInt(4, anneeId);
                ps.executeUpdate();
            }

            new Alert(Alert.AlertType.INFORMATION,
                    "Enseignement enregistré avec succès !").show();

            chargerDonnees();

            cmbClasse.setValue(null);
            cmbMatiere.setValue(null);
            cmbCoeff.setValue(null);

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR,
                    "Coefficient invalide !").show();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Erreur SQL : " + e.getMessage()).show();
        }
    }

    // 🔹 Supprimer un enseignement sélectionné
    @FXML
    void supprimerEnseignement(ActionEvent event) {
        Enseignement selection = tableEnseignement.getSelectionModel().getSelectedItem();

        if (selection == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un élément à supprimer !");
            alert.show();
            return;
        }

        try (Connection conn = Database.connect()) {
            String sql = "DELETE FROM enseignement WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, selection.getId());
            ps.executeUpdate();
            ps.close();

            data.remove(selection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
