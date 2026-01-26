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

public class EnseignementController {

    // 🔹 Configuration de la base de données
    private final String URL = "jdbc:mysql://localhost:3306/gestionecole";
    private final String USER = "root";
    private final String PASSWORD = "";

    
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
    private void retourAccueil() {
        
    }
    @FXML
    void openEmargement(ActionEvent event) {
    	changerScene("EmargementView.fxml", "Gestion des Emargements");
    }
    
    
    @FXML private void openClasse(ActionEvent event) {  changerScene("ClasseView.fxml", "Gestion des Classes");}
    @FXML private void openEleve(ActionEvent event) {changerScene("EleveForm.fxml", "Gestion des Eleves"); }
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
        colEnseignant.setCellValueFactory(new PropertyValueFactory<>("enseignant"));
        colClasse.setCellValueFactory(new PropertyValueFactory<>("classe"));
        colMatiere.setCellValueFactory(new PropertyValueFactory<>("matiere"));
        colCoeff.setCellValueFactory(new PropertyValueFactory<>("coeff"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee_id"));

        tableEnseignement.setItems(data);

        // Charger les ComboBox et le tableau
        chargerComboBox();
        chargerDonnees();
    }
   


    // 🔹 Charger les données dans le tableau
    private void chargerDonnees() {
        data.clear();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, enseignant, classe, matiere, coeff,annee_id FROM enseignement")) {

            while (rs.next()) {
                data.add(new Enseignement(
                        rs.getInt("id"),
                        rs.getString("enseignant"),
                        rs.getString("classe"),
                        rs.getString("matiere"),
                        rs.getString("coeff"),
                        rs.getInt("annee_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
     
   
    // 🔹 Charger les ComboBox depuis la base
    private void chargerComboBox() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            // 🔹 Enseignants
            ResultSet rs = conn.createStatement().executeQuery("SELECT e.id, e.nom, e.prenom, e.date_naissance, e.adresse, e.telephone, e.matiere_id, m.nom AS matiereNom " +
                    "FROM enseignants e LEFT JOIN matiere m ON e.matiere_id = m.id");
            ObservableList<String> enseignants = FXCollections.observableArrayList();
            while (rs.next()) {
                
                enseignants.add(rs.getInt("id") + " - " + rs.getString("nom") + " " + rs.getString("prenom")+" - "+rs.getString("matiereNom")
); // e.toString() doit retourner "Nom Prénom"
            }
            cmbEnseignant.setItems(enseignants);

            // 🔹 Classes
            rs = conn.createStatement().executeQuery("SELECT nom FROM classe");
            ObservableList<String> classes = FXCollections.observableArrayList();
            while (rs.next()) classes.add(rs.getString("nom"));
            cmbClasse.setItems(classes);

            // 🔹 Matières
            rs = conn.createStatement().executeQuery("SELECT nom FROM matiere");
            ObservableList<String> matieres = FXCollections.observableArrayList();
            while (rs.next()) matieres.add(rs.getString("nom"));
            cmbMatiere.setItems(matieres);
            

            // 🔹 Annees
            rs = conn.createStatement().executeQuery("SELECT id, libelle FROM anneescolaire ORDER BY libelle DESC");
            while (rs.next()) cbAnnee.getItems().add(rs.getInt("id") + " - " + rs.getString("libelle"));
            
            
         
            
           

            // 🔹 Coeff (exemple 1 à 5)
            cmbCoeff.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Ajouter un enseignement
        @FXML
        void ajouterEnseignement(ActionEvent event) {
        	String enseignant = cmbEnseignant.getValue();
            String classe = cmbClasse.getValue();
            String matiere = cmbMatiere.getValue();
            String coeff = cmbCoeff.getValue();
            String selected = cbAnnee.getValue();
            if (selected == null) return;

            String annee_id = selected.split(" - ")[0];


            // Vérifier que tous les champs sont remplis
            if (enseignant == null || classe == null || matiere == null || coeff == null) {
                new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !").show();
                return;
            }

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

                // Vérifier s'il existe déjà un enseignement identique
                String checkSql = """
                    SELECT COUNT(*) 
                    FROM enseignement 
                    WHERE enseignant = ? 
                      AND classe = ? 
                      AND matiere = ?
                      AND annee_id =?
                """;
                try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                    checkPs.setString(1, enseignant);
                    checkPs.setString(2, classe);
                    checkPs.setString(3, matiere);
                    checkPs.setString(4, annee_id);

                    ResultSet rs = checkPs.executeQuery();
                    rs.next();

                    if (rs.getInt(1) > 0) {
                        new Alert(Alert.AlertType.WARNING,
                            "Cet enseignement existe déjà pour ce professeur, cette classe et cette matière !"
                        ).show();
                        return;
                    }
                }

                // Si pas de doublon → insertion
                String insertSql = "INSERT INTO enseignement (enseignant, classe, matiere, coeff,annee_id) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, enseignant);
                    ps.setString(2, classe);
                    ps.setString(3, matiere);
                    ps.setString(4, coeff);
                    ps.setString(5,annee_id);
                    ps.executeUpdate();
                }

                new Alert(Alert.AlertType.INFORMATION, "Enseignement enregistré avec succès !").show();

                // Rafraîchir les données dans la table
                chargerDonnees();

                // Réinitialiser les champs
                cmbEnseignant.setValue(null);
                cmbClasse.setValue(null);
                cmbMatiere.setValue(null);
                cmbCoeff.setValue(null);

            } catch (SQLException e) {
                e.printStackTrace();
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

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
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
