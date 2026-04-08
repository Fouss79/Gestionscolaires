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
    @FXML private ComboBox<String> cbSerie;
    @FXML private ComboBox<String> cbGroupe;
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
    @FXML private TableColumn<Classe, Integer> colGarcon; // plus d'accent
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
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        colEffectif.setCellValueFactory(new PropertyValueFactory<>("effectif"));
        colFille.setCellValueFactory(new PropertyValueFactory<>("nbFille"));
        colGarcon.setCellValueFactory(new PropertyValueFactory<>("nbGarcons"));

        chargerClasses();
        chargerNiveauxCombo();
        chargerSeriesCombo();
        chargerGroupesCombo();

      
    }    
    private void chargerSeriesCombo() {
        ObservableList<String> series = FXCollections.observableArrayList();
        String sql = "SELECT nom FROM serie ORDER BY nom";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                series.add(rs.getString("nom"));
            }
            cbSerie.setItems(series);

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible de charger les séries !").show();
        }
    }

    private void chargerGroupesCombo() {
        ObservableList<String> groupes = FXCollections.observableArrayList();
        String sql = "SELECT nom FROM groupe ORDER BY nom";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                groupes.add(rs.getString("nom"));
            }
            cbGroupe.setItems(groupes);

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible de charger les groupes !").show();
        }
    }

    // 🔹 Charger les niveaux depuis la table "niveau"
    private void chargerNiveauxCombo() {
        ObservableList<String> niveaux = FXCollections.observableArrayList();
        String sql = "SELECT nom FROM niveau ORDER BY nom";

        try (Connection conn = Database.connect();
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
    private void deconnexion() {
        // par ex : retourner à l'écran de login
    }
    @FXML
    void openEmargement(ActionEvent event) {
    	changerScene("EmargementView.fxml", "Gestion des Emargements");

    }
    @FXML private void retourAccueil() {changerScene("DashboardView.fxml", "Accueil");  }
    @FXML private void openClasse(ActionEvent event) { changerScene("ClasseView.fxml", "Gestion des Classes"); }
    @FXML private void openEleve(ActionEvent event) { changerScene("ElevesView.fxml", "Gestion des Élèves"); }
    @FXML private void openMatiere(ActionEvent event) { changerScene("Matiere.fxml", "Gestion des Matières"); }
    @FXML private void openNote(ActionEvent event) { changerScene("NoteView.fxml", "Gestion des Notes"); }
    @FXML private void openEnseignant(ActionEvent event) { changerScene("EnseignantView.fxml", "Gestion des Enseignants"); }
    @FXML private void openTransaction(ActionEvent event) { changerScene("PaiementEnseignant.fxml", "Gestion des Transactions"); }
    @FXML private void openBulletin(ActionEvent event) { changerScene("BulletinView.fxml", "Gestion des Bulletins"); }
    @FXML private void openEmploidutemps(ActionEvent event) { changerScene("EmploiDutempsView.fxml", "Gestion des Emplois du Temps"); }
    @FXML private void openComptabilite(ActionEvent event) { changerScene("EnseignementView.fxml", "Gestion des Cours"); }
    @FXML
    void openHabilitation(ActionEvent event) {
    	 changerScene("HabilitationView.fxml", "Habilitation");
    }
    
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
            stage.setScene(new Scene(root, 1283, 657));
            stage.setTitle(titre);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Ajouter une classe
    @FXML
    private void ajouterClasse() {
        String niveau = cbNiveau.getValue();
        String serie = cbSerie.getValue();
        String groupe = cbGroupe.getValue();

        if (niveau == null || groupe == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner le niveau et le groupe.").show();
            return;
        }

        // 🔹 Récupérer les IDs depuis la base
        int niveauId = getIdFromTable("niveau", niveau);
        Integer serieId = (serie != null) ? getIdFromTable("serie", serie) : null;
        int groupeId = getIdFromTable("groupe", groupe);

        String sql = "INSERT INTO classe (niveau_id, serie_id, groupe_id) VALUES (?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, niveauId);
            if (serieId != null) stmt.setInt(2, serieId); else stmt.setNull(2, Types.INTEGER);
            stmt.setInt(3, groupeId);

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int id = 0;
            if (generatedKeys.next()) id = generatedKeys.getInt(1);

            Classe nouvelleClasse = new Classe(id, niveau,serie, groupe);
            listeClasses.add(nouvelleClasse);
            tableClasses.setItems(listeClasses);

            cbNiveau.setValue(null);
            cbSerie.setValue(null);
            cbGroupe.setValue(null);

            new Alert(Alert.AlertType.INFORMATION, "Classe enregistrée avec succès !").show();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l’enregistrement !").show();
        }
    }

    // 🔹 Méthode utilitaire pour récupérer l'ID d'une table
    private int getIdFromTable(String table, String nom) {
        String sql = "SELECT id FROM " + table + " WHERE nom = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    // 🔹 Charger les classes existantes
    private void chargerClasses() {
        listeClasses.clear();

        String sql = """
            SELECT 
                c.id,
                n.nom AS niveau,
                s.nom AS serie,
                g.nom AS groupe,
                COUNT(i.id) AS effectif,
                COALESCE(SUM(CASE WHEN e.sexe = 'Garçon' THEN 1 ELSE 0 END), 0) AS nbGarcons,
                COALESCE(SUM(CASE WHEN e.sexe = 'Fille' THEN 1 ELSE 0 END), 0) AS nbFilles
            FROM classe c
            JOIN niveau n ON c.niveau_id = n.id
            LEFT JOIN serie s ON c.serie_id = s.id
            JOIN groupe g ON c.groupe_id = g.id
            LEFT JOIN inscription i 
                ON i.classe_id = c.id 
                AND i.annee_id = (SELECT id FROM anneescolaire WHERE active = 1 LIMIT 1)
            LEFT JOIN eleve e ON i.eleve_id = e.id
            GROUP BY c.id, n.nom, s.nom, g.nom
            ORDER BY n.nom, s.nom, g.nom
        """;

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Classe c = new Classe(
                        rs.getInt("id"),
                        rs.getString("niveau"),
                        rs.getString("serie"),
                        rs.getString("groupe")
                );
                c.setEffectif(rs.getInt("effectif"));
                c.setNbGarçon(rs.getInt("nbGarcons"));
                c.setNbFille(rs.getInt("nbFilles"));

                listeClasses.add(c);
            }

            tableClasses.setItems(listeClasses);

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des classes !").show();
        }
    }    // 🔹 Modifier une classe
    @FXML
    void modifierClasse(ActionEvent event) {
        Classe selected = tableClasses.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une classe à modifier.").show();
            return;
        }

        // 🔹 Modifier le niveau via ChoiceDialog
        ObservableList<String> niveaux = cbNiveau.getItems();
        ChoiceDialog<String> dialogNiveau = new ChoiceDialog<>(selected.getNiveau(), niveaux);
        dialogNiveau.setTitle("Modifier Classe");
        dialogNiveau.setHeaderText("Modifier le niveau de la classe sélectionnée");
        dialogNiveau.setContentText("Niveau :");
        String nouveauNiveau = dialogNiveau.showAndWait().orElse(null);

        // 🔹 Modifier la série via ChoiceDialog
        ObservableList<String> series = cbSerie.getItems();
        ChoiceDialog<String> dialogSerie = new ChoiceDialog<>(selected.getSerie(), series);
        dialogSerie.setTitle("Modifier Classe");
        dialogSerie.setHeaderText("Modifier la série de la classe sélectionnée");
        dialogSerie.setContentText("Série :");
        String nouvelleSerie = dialogSerie.showAndWait().orElse(null);

        // 🔹 Modifier le groupe via ChoiceDialog
        ObservableList<String> groupes = cbGroupe.getItems();
        ChoiceDialog<String> dialogGroupe = new ChoiceDialog<>(selected.getGroupe(), groupes);
        dialogGroupe.setTitle("Modifier Classe");
        dialogGroupe.setHeaderText("Modifier le groupe de la classe sélectionnée");
        dialogGroupe.setContentText("Groupe :");
        String nouveauGroupe = dialogGroupe.showAndWait().orElse(null);

        if (nouveauNiveau != null && nouvelleSerie != null && nouveauGroupe != null &&
            !nouveauNiveau.isBlank() && !nouvelleSerie.isBlank() && !nouveauGroupe.isBlank()) {

            // 🔹 Récupérer les IDs depuis la base
            int niveauId = getIdFromTable("niveau", nouveauNiveau);
            Integer serieId = getIdFromTable("serie", nouvelleSerie);
            int groupeId = getIdFromTable("groupe", nouveauGroupe);

            String sql = """
                UPDATE classe 
                SET niveau_id = ?, serie_id = ?, groupe_id = ? 
                WHERE id = ?
            """;

            try (Connection conn = Database.connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, niveauId);
                if (serieId != null) stmt.setInt(2, serieId); else stmt.setNull(2, Types.INTEGER);
                stmt.setInt(3, groupeId);
                stmt.setInt(4, selected.getId());

                stmt.executeUpdate();

                // 🔹 Mettre à jour l'objet local
                selected.setNiveau(nouveauNiveau);
                selected.setSerie(nouvelleSerie);
                selected.setGroupe(nouveauGroupe);
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
            new Alert(Alert.AlertType.WARNING,
                    "Veuillez sélectionner une classe à supprimer.")
                    .show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer la classe " + selected.getNom() + " ?",
                ButtonType.YES, ButtonType.NO);

        confirm.showAndWait();

        if (confirm.getResult() != ButtonType.YES) {
            return;
        }

        try (Connection conn = Database.connect()) {

            // 🔹 Vérifier si la classe est utilisée
            String checkSql = "SELECT COUNT(*) FROM inscription WHERE classe_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, selected.getId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                new Alert(Alert.AlertType.WARNING,
                        "Impossible de supprimer cette classe.\nElle contient des inscriptions.")
                        .show();
                return;
            }

            // 🔹 Supprimer par ID
            String deleteSql = "DELETE FROM classe WHERE id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, selected.getId());
            deleteStmt.executeUpdate();

            listeClasses.remove(selected);
            tableClasses.refresh();

            new Alert(Alert.AlertType.INFORMATION,
                    "Classe supprimée avec succès !")
                    .show();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Erreur lors de la suppression !")
                    .show();
        }
    }}






