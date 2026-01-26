package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.*;

public class ClasseController {

    @FXML private TextField txtNom;
    @FXML private ComboBox<String> cbNiveau;
    @FXML
    private Button btnEnregistrer;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnEleve1;

    @FXML
    private Button btnClasse11;
    @FXML
    private Button btnMatiere11;
    
    @FXML
    private Button btnEleve11;



    

    @FXML private TableView<Classe> tableClasses;
    @FXML private TableColumn<Classe, String> colNom;
    @FXML private TableColumn<Classe, String> colNiveau;
    @FXML private TableColumn<Classe, Integer> colEffectif;
    @FXML private TableColumn<Classe, Integer> colFille;
    @FXML private TableColumn<Classe, Integer> colGarçon;

    @FXML private ComboBox<String> cbParams;
    private ObservableList<String> params = FXCollections.observableArrayList("Info","Niveau",
            "Classes", "Personnel","Type de Frais","Montant", "Année scolaire","Tarif horaire"
            ,"Remise/Penalité" );

    private ObservableList<Classe> listeClasses = FXCollections.observableArrayList();

    // Configuration DB
    private final String URL = "jdbc:mysql://localhost:3306/gestionEcole";
    private final String USER = "root";
    private final String PASSWORD = "";

    @FXML
    public void initialize() {
    	

       // cacher(btnEleve1);
      
        // Mapper les colonnes
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        colEffectif.setCellValueFactory(new PropertyValueFactory<>("effectif"));
        colFille.setCellValueFactory(new PropertyValueFactory<>("nbFille"));
        colGarçon.setCellValueFactory(new PropertyValueFactory<>("nbGarçon"));

        // Charger les classes existantes
        chargerClasses();

        // Charger les niveaux depuis la table "niveau"
        chargerNiveauxCombo();

        // Charger les paramètres
        cbParams.setItems(params);
        cbParams.setOnAction(e -> {
            String choix = cbParams.getValue();
            switch (choix) {
                case "Classes" -> changerScene("ClasseView.fxml", "Gestion des Classes");
                case "Frais" -> changerScene("PaiementView.fxml", "Gestion des Frais");
                case "Personnel" -> changerScene("EnseignantView.fxml", "Gestion du Personnel");
                case "Année scolaire" -> changerScene("AnneeScolaireView.fxml", "Année scolaire");
            }
        });
    }

    // 🔹 Charger les niveaux depuis la table "niveau"
    private void chargerNiveauxCombo() {
        ObservableList<String> niveaux = FXCollections.observableArrayList();
        String sql = "SELECT nom FROM niveau ORDER BY nom";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                niveaux.add(rs.getString("nom"));
            }

            cbNiveau.setItems(niveaux);

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible de charger les niveaux !").show();
        }
    }
    
    
 // 🔹 Navigation entre les pages
    @FXML
    void openEmargement(ActionEvent event) {
    	changerScene("EmargementView.fxml", "Gestion des Emargements");

    }
    @FXML private void retourAccueil() {changerScene("Accueil.fxml", "Accueil");  }
    @FXML private void openClasse(ActionEvent event) { changerScene("ClasseView.fxml", "Gestion des Classes"); }
    @FXML private void openEleve(ActionEvent event) { changerScene("ElevesView.fxml", "Gestion des Élèves"); }
    @FXML private void openMatiere(ActionEvent event) { changerScene("Matiere.fxml", "Gestion des Matières"); }
    @FXML private void openNote(ActionEvent event) { changerScene("NoteView.fxml", "Gestion des Notes"); }
    @FXML private void openEnseignant(ActionEvent event) { changerScene("EnseignantView.fxml", "Gestion des Enseignants"); }
    @FXML private void openTransaction(ActionEvent event) { changerScene("PaiementEnseignant.fxml", "Gestion des Transactions"); }
    @FXML private void openBulletin(ActionEvent event) { changerScene("BulletinView.fxml", "Gestion des Bulletins"); }
    @FXML private void openEmploidutemps(ActionEvent event) { changerScene("EmploiDutempsView.fxml", "Gestion des Emplois du Temps"); }
    @FXML private void openComptabilite(ActionEvent event) { changerScene("EnseignementView.fxml", "Gestion des Cours"); }

    
    private void cacher(Node node) {
        node.setVisible(false);
        node.setManaged(false);
    }


    
    
    // 🔹 Navigation générique
    private void changerScene(String fxml, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) tableClasses.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 650));
            stage.setTitle(titre);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Ajouter une classe
    @FXML
    private void ajouterClasse() {
        String nom = txtNom.getText();
        String niveau = cbNiveau.getValue();

        if (nom.isEmpty() || niveau == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.").show();
            return;
        }

        String sql = "INSERT INTO classe (nom, niveau, effectif) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            stmt.setString(2, niveau);
            stmt.setInt(3, 0);
            stmt.executeUpdate();

            Classe nouvelleClasse = new Classe(nom, niveau);
            nouvelleClasse.setEffectif(0);
            listeClasses.add(nouvelleClasse);
            tableClasses.setItems(listeClasses);

            txtNom.clear();
            cbNiveau.setValue(null);

            new Alert(Alert.AlertType.INFORMATION, "Classe enregistrée avec succès !").show();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l’enregistrement !").show();
        }
    }

    // 🔹 Charger les classes existantes
    private void chargerClasses() {
        listeClasses.clear();

        String sqlClasse = "SELECT id, nom, niveau FROM classe";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rsClasse = stmt.executeQuery(sqlClasse)) {

            while (rsClasse.next()) {
                int classeId = rsClasse.getInt("id");
                String nom = rsClasse.getString("nom");
                String niveau = rsClasse.getString("niveau");

                // Recalculer effectif, garçons et filles à partir de la table inscription
                String sqlStats = """
                    SELECT 
                        COUNT(*) AS effectif,
                        SUM(CASE WHEN e.sexe = 'Garçon' THEN 1 ELSE 0 END) AS nbGarcons,
                        SUM(CASE WHEN e.sexe = 'Fille' THEN 1 ELSE 0 END) AS nbFilles
                    FROM inscription i
                    JOIN eleve e ON i.eleve_id = e.id
                    WHERE i.classe_id = ?
                """;

                try (PreparedStatement psStats = conn.prepareStatement(sqlStats)) {
                    psStats.setInt(1, classeId);
                    ResultSet rsStats = psStats.executeQuery();

                    int effectif = 0, nbGarcons = 0, nbFilles = 0;
                    if (rsStats.next()) {
                        effectif = rsStats.getInt("effectif");
                        nbGarcons = rsStats.getInt("nbGarcons");
                        nbFilles = rsStats.getInt("nbFilles");
                    }

                    // Créer l'objet Classe avec les stats actualisées
                    Classe c = new Classe(nom, niveau);
                    c.setEffectif(effectif);
                    c.setNbGarçon(nbGarcons);
                    c.setNbFille(nbFilles);

                    listeClasses.add(c);
                }
            }

            tableClasses.setItems(listeClasses);

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des classes !").show();
        }
    }

    // 🔹 Modifier une classe
    @FXML
    void modifierClasse(ActionEvent event) {
        Classe selected = tableClasses.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une classe à modifier.").show();
            return;
        }

        // Modifier le nom
        TextInputDialog dialogNom = new TextInputDialog(selected.getNom());
        dialogNom.setTitle("Modifier Classe");
        dialogNom.setHeaderText("Modifier la classe sélectionnée");
        dialogNom.setContentText("Nom de la classe :");
        String nouveauNom = dialogNom.showAndWait().orElse(null);

        // Modifier le niveau via ChoiceDialog avec les niveaux de la table
        ObservableList<String> niveaux = cbNiveau.getItems(); // récupère les niveaux déjà chargés
        ChoiceDialog<String> dialogNiveau = new ChoiceDialog<>(selected.getNiveau(), niveaux);
        dialogNiveau.setTitle("Modifier Classe");
        dialogNiveau.setHeaderText("Modifier le niveau de la classe sélectionnée");
        dialogNiveau.setContentText("Niveau :");
        String nouveauNiveau = dialogNiveau.showAndWait().orElse(null);

        if (nouveauNom != null && nouveauNiveau != null && !nouveauNom.isBlank() && !nouveauNiveau.isBlank()) {
            String sql = "UPDATE classe SET nom = ?, niveau = ? WHERE nom = ? AND niveau = ?";

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, nouveauNom);
                stmt.setString(2, nouveauNiveau);
                stmt.setString(3, selected.getNom());
                stmt.setString(4, selected.getNiveau());
                stmt.executeUpdate();

                selected.setNom(nouveauNom);
                selected.setNiveau(nouveauNiveau);
                tableClasses.refresh();

                new Alert(Alert.AlertType.INFORMATION, "Classe modifiée avec succès !").show();

            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification !").show();
            }
        }
    }

    // 🔹 Supprimer une classe
    @FXML
    void supprimerClasse(ActionEvent event) {
        Classe selected = tableClasses.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une classe à supprimer.").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer la classe " + selected.getNom() + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            String sql = "DELETE FROM classe WHERE nom = ? AND niveau = ?";

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, selected.getNom());
                stmt.setString(2, selected.getNiveau());
                stmt.executeUpdate();

                listeClasses.remove(selected);
                tableClasses.refresh();

                new Alert(Alert.AlertType.INFORMATION, "Classe supprimée avec succès !").show();

            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression !").show();
            }
        }
    }
}






