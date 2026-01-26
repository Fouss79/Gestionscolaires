
package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class NoteController {

    // TableView Notes
    @FXML private TableView<Note> tableNotes;
    @FXML private TableColumn<Note, Long> colId;
    @FXML private TableColumn<Note, String> colPeriode;
    @FXML private TableColumn<Note, String> colClasse;
    @FXML private TableColumn<Note, String> colElevNom;
    @FXML private TableColumn<Note, String> colMatiereNom;
    @FXML private TableColumn<Note, Double> colNClass;
    @FXML private TableColumn<Note, Double> colNExem;
    @FXML private TableColumn<Note, Double> colCoeff;

    // TableView Eleves
    @FXML private TableColumn<Note, Double> colMoyenne;
    @FXML private TableView<Eleve> tableEleves;
    @FXML private TableColumn<Eleve, Long> colEleveId;
    @FXML private TableColumn<Eleve, String> colEleveNom;
    @FXML private TableColumn<Eleve, String> colElevePrenom;
    @FXML private TableColumn<Eleve, String> colEleveMatricule;
    

    // ComboBox
    @FXML private ComboBox<String> comboAnnee;
    @FXML private ComboBox<String> cbClasse;
    @FXML private ComboBox<Eleve> cbEleve;
    @FXML private ComboBox<String> cbMatiere;
    @FXML private ComboBox<String> cbPeriode;

    // Champs de saisie
    @FXML private TextField txtNClass;
    @FXML private TextField txtNExem;
    @FXML private TextField txtCoeff;
    @FXML
    private Label lblMoyenne;

    @FXML
    private Label lblMoyenne1;

    @FXML
    private Label lblRang;

    @FXML
    private Label lblRang1;

    // Listes observables
    private ObservableList<Note> notes = FXCollections.observableArrayList();
    private ObservableList<Eleve> elevesCombo = FXCollections.observableArrayList();
    private ObservableList<Eleve> elevesTable = FXCollections.observableArrayList();
    private ObservableList<String> matieresCombo = FXCollections.observableArrayList();
    private ObservableList<String> classes = FXCollections.observableArrayList();
    private ObservableList<String> periodes = FXCollections.observableArrayList(
            "Trimestre 1", "Trimestre 2", "Trimestre 3", "Année"
    );

    // Connexion JDBC
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/gestionecole";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    @FXML
    public void initialize() {
        // Colonnes notes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPeriode.setCellValueFactory(new PropertyValueFactory<>("periode"));
        colClasse.setCellValueFactory(new PropertyValueFactory<>("classe"));
        colElevNom.setCellValueFactory(new PropertyValueFactory<>("eleveNom"));
        colMatiereNom.setCellValueFactory(new PropertyValueFactory<>("matiereNom"));
        colNClass.setCellValueFactory(new PropertyValueFactory<>("NClass"));
        colNExem.setCellValueFactory(new PropertyValueFactory<>("NExem"));
        colCoeff.setCellValueFactory(new PropertyValueFactory<>("coeff"));
        colMoyenne.setCellValueFactory(new PropertyValueFactory<>("moyenne"));

        tableNotes.setItems(notes);

        // Colonnes eleves
        colEleveId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEleveNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colElevePrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEleveMatricule.setCellValueFactory(new PropertyValueFactory<>("numeroMatricule"));
        tableEleves.setItems(elevesTable);

        // Périodes
        cbPeriode.setItems(periodes);
        cbPeriode.setDisable(false); // toujours activé

        // Charger années scolaires
        chargerAnnees();
        setColorOnNoteColumn(colNClass);
        setColorOnNoteColumn(colNExem);
        setColorOnNoteColumn(colMoyenne);


        // Actions
        
        cbPeriode.setOnAction(e ->{ loadNotesFromDB(cbClasse.getValue().split(" - ")[1].split("\\(")[0].trim(), cbEleve.getValue(), cbPeriode.getValue());
        afficherMoyenne();
        
        });

        comboAnnee.setOnAction(e -> handleAnneeSelected());
        cbClasse.setOnAction(e -> handleClasseSelected());
    
        


        cbEleve.setOnAction(e -> {
            String classe = cbClasse.getValue().split(" - ")[1].split("\\(")[0].trim();
            loadNotesFromDB(classe, cbEleve.getValue(), cbPeriode.getValue());
            updateCoeff();
            afficherMoyenne();
        });

       
       


        cbMatiere.setOnAction(e -> updateCoeff());

        tableEleves.setOnMouseClicked(e -> {
            Eleve selected = tableEleves.getSelectionModel().getSelectedItem();
            if (selected != null) cbEleve.setValue(selected);
        });
    }

    // --- Charger années scolaires ---
    private void chargerAnnees() {
        comboAnnee.getItems().clear();
        String sql = "SELECT id, libelle FROM anneescolaire ORDER BY libelle DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                comboAnnee.getItems().add(rs.getInt("id") + " - " + rs.getString("libelle"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Quand on sélectionne une année ---
    @FXML
    private void handleAnneeSelected() {
        String selected = comboAnnee.getValue();
        if (selected == null) return;

        String idAnnee = selected.split(" - ")[0];

        cbClasse.getItems().clear();
        elevesCombo.clear();

        String sql = """
            SELECT DISTINCT c.id, c.nom, c.niveau
            FROM classe c
            JOIN inscription i ON i.classe_id = c.id
            WHERE i.annee_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idAnnee);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cbClasse.getItems().add(
                        rs.getInt("id") + " - " + rs.getString("nom") + " (" + rs.getString("niveau") + ")"
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Quand on sélectionne une classe ---
    @FXML
    private void handleClasseSelected() {
        String selectedClasse = cbClasse.getValue();
        String selectedAnnee = comboAnnee.getValue();
        if (selectedClasse == null || selectedAnnee == null) return;

        elevesCombo.clear();

        String idClasse = selectedClasse.split(" - ")[0];
        String classeNom = selectedClasse.split(" - ")[1].split("\\(")[0].trim();
        String idAnnee = selectedAnnee.split(" - ")[0];

        // Charger élèves inscrits
        String sql = """
            SELECT e.id, e.numero_matricule, e.nom, e.prenom, c.nom AS classe_nom
            FROM eleve e
            JOIN inscription i ON i.eleve_id = e.id
            JOIN classe c ON c.id = i.classe_id
            WHERE i.classe_id = ? AND i.annee_id = ?
            ORDER BY e.nom, e.prenom
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idClasse);
            ps.setString(2, idAnnee);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Eleve e = new Eleve();
                e.setId(rs.getLong("id"));
                e.setNumeroMatricule(rs.getString("numero_matricule"));
                e.setNom(rs.getString("nom"));
                e.setPrenom(rs.getString("prenom"));
                e.setClasseNom(rs.getString("classe_nom"));
                elevesCombo.add(e);
            }
            cbEleve.setItems(elevesCombo);

            // Charger matières de cette classe
            loadMatieresByClasse(classeNom);

            cbPeriode.setDisable(false); // réactiver
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Charger matières selon classe ---
    private void loadMatieresByClasse(String classeNom) {
        matieresCombo.clear();
        String sql = "SELECT matiere, coeff FROM enseignement WHERE classe = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, classeNom);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                matieresCombo.add(rs.getString("matiere") + " - " + rs.getString("coeff"));
            }
            cbMatiere.setItems(matieresCombo);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadNotesFromDB(String classe, Eleve eleveFilter, String periodeFilter) {
        notes.clear();
        StringBuilder sql = new StringBuilder("""
            SELECT n.id, n.periode, n.classe, n.n_class, n.n_exem, 
                   e.nom AS eleveNom, m.matiere AS matiereNom, n.coeff
            FROM note n
            JOIN eleve e ON n.eleve_id = e.id
            JOIN enseignement m ON n.matiere_id = m.id
            WHERE n.classe = ?
        """);

        if (eleveFilter != null) sql.append(" AND n.eleve_id = ?");
        if (periodeFilter != null) sql.append(" AND n.periode = ?");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            ps.setString(index++, classe);
            if (eleveFilter != null) ps.setLong(index++, eleveFilter.getId());
            if (periodeFilter != null) ps.setString(index++, periodeFilter);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Note note = new Note(
                    rs.getLong("id"),
                    rs.getString("periode"),
                    rs.getString("classe"),
                    rs.getString("eleveNom"),
                    rs.getString("matiereNom"),
                    rs.getDouble("n_class"),
                    rs.getDouble("n_exem"),
                    rs.getDouble("coeff")
                );
                notes.add(note);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private int getEffectifClasse(long classeId, long anneeId) {

        String sql = "SELECT COUNT(*) FROM inscription WHERE classe_id = ? AND annee_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, classeId);
            ps.setLong(2, anneeId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    
    private Double calculerMoyenneElevePeriode(long eleveId, String periode, String classe) {

        String sql = """
            SELECT 
                SUM(((n.n_class + n.n_exem*2)/3)*n.coeff)/ SUM(n.coeff) AS moyenne
            FROM note n
            WHERE n.eleve_id = ? AND n.periode = ? AND n.classe = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, eleveId);
            ps.setString(2, periode);
            ps.setString(3, classe);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("moyenne");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void afficherMoyenne() {

        if (cbEleve.getValue() == null || cbPeriode.getValue() == null || cbClasse.getValue() == null)
            return;

        Eleve eleve = cbEleve.getValue();
        String periode = cbPeriode.getValue();
        String classeNom = cbClasse.getValue().split(" - ")[1].split("\\(")[0].trim();

        long classeId = Long.parseLong(cbClasse.getValue().split(" - ")[0]);
        long anneeId = Long.parseLong(comboAnnee.getValue().split(" - ")[0]);

        Double moyenne = calculerMoyenneElevePeriode(eleve.getId(), periode, classeNom);
        Integer rang = calculerRangEleve(eleve.getId(), classeNom, periode);
        int effectif = getEffectifClasse(classeId, anneeId);

        if (moyenne != null && rang != null) {

            lblMoyenne.setText(String.format("%.2f", moyenne));
            lblRang.setText(rang + " / " + effectif);
        }
    }
    
    private ResultSet getMoyennesClasse(String classe, String periode, Connection conn) throws SQLException {

    	String sql = """
    		    SELECT 
    		        n.eleve_id,
    		        SUM(((n.n_class + n.n_exem*2)/3) * n.coeff) / SUM(n.coeff) AS moyenne
    		    FROM note n
    		    WHERE n.classe = ? AND n.periode = ?
    		    GROUP BY n.eleve_id
    		    ORDER BY moyenne DESC
    		""";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, classe);
        ps.setString(2, periode);

        return ps.executeQuery();
    }

    private Integer calculerRangEleve(long eleveId, String classe, String periode) {

        try (Connection conn = getConnection()) {

            ResultSet rs = getMoyennesClasse(classe, periode, conn);
            
            
            

            int rang = 1;
            while (rs.next()) {
                if (rs.getLong("eleve_id") == eleveId) {
                    return rang;
                }
                rang++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    
    // --- Charger notes d’une classe ---
       // --- Mettre à jour le coeff ---
    private void updateCoeff() {
        if (cbClasse.getValue() == null || cbMatiere.getValue() == null) return;

        String classeNom = cbClasse.getValue().split(" - ")[1].split("\\(")[0].trim();
        String matiereNom = cbMatiere.getValue().split(" - ")[0];

        String sql = "SELECT coeff FROM enseignement WHERE classe = ? AND matiere = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, classeNom);
            ps.setString(2, matiereNom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) txtCoeff.setText(rs.getString("coeff"));
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    private void setColorOnNoteColumn(TableColumn<Note, Double> column) {

        column.setCellFactory(col -> new TableCell<>() {

            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f", value));

                    if (value < 10) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if (value < 12) {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    // --- Ajouter une note ---
    @FXML
    private void ajouterNote() {
        Eleve e = cbEleve.getValue();
        String matiereStr = cbMatiere.getValue();
        String periode = cbPeriode.getValue();
        if ("Année".equals(periode)) {
            showAlert("Erreur", "On ne saisit pas de notes pour la période Année.");
            return;
        }

        String classe = cbClasse.getValue().split(" - ")[1].split("\\(")[0].trim();

        if (e == null || matiereStr == null || periode == null || classe == null ||
                txtNClass.getText().isEmpty() || txtNExem.getText().isEmpty() || txtCoeff.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        try {
            double nClass = Double.parseDouble(txtNClass.getText());
            double nExem = Double.parseDouble(txtNExem.getText());
            double coeff = Double.parseDouble(txtCoeff.getText());

            if (nClass < 0 || nClass > 20 || nExem < 0 || nExem > 20) {
                showAlert("Erreur", "Les notes doivent être comprises entre 0 et 20 !");
                return;
            }

            try (Connection conn = getConnection()) {
                // Récupérer l'ID de la matière depuis la table enseignement
                String sqlMatiere = "SELECT id FROM enseignement WHERE matiere = ? AND classe = ?";
                long matiereId;
                try (PreparedStatement psM = conn.prepareStatement(sqlMatiere)) {
                    psM.setString(1, matiereStr.split(" - ")[0]);
                    psM.setString(2, classe);
                    ResultSet rsM = psM.executeQuery();
                    if (!rsM.next()) {
                        showAlert("Erreur", "Cette matière n'existe pas pour cette classe !");
                        return;
                    }
                    matiereId = rsM.getLong("id");
                }

                // Vérifier doublon
                String checkSql = "SELECT COUNT(*) FROM note WHERE eleve_id = ? AND matiere_id = ? AND periode = ? AND classe = ?";
                try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                    checkPs.setLong(1, e.getId());
                    checkPs.setLong(2, matiereId);
                    checkPs.setString(3, periode);
                    checkPs.setString(4, classe);

                    ResultSet rs = checkPs.executeQuery();
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        showAlert("Attention", "Une note existe déjà pour cet élève !");
                        return;
                    }
                }

                // Insertion
                String insertSql = """
                    INSERT INTO note(eleve_id, matiere_id, periode, classe, n_class, n_exem, coeff)
                    VALUES(?, ?, ?, ?, ?, ?, ?)
                """;
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setLong(1, e.getId());
                    ps.setLong(2, matiereId);
                    ps.setString(3, periode);
                    ps.setString(4, classe);
                    ps.setDouble(5, nClass);
                    ps.setDouble(6, nExem);
                    ps.setDouble(7, coeff);
                    ps.executeUpdate();
                }

                loadNotesFromDB(classe, cbEleve.getValue(), cbPeriode.getValue());
                afficherMoyenne();

                showAlert("Succès", "Note ajoutée avec succès !");
                txtNClass.clear();
                txtNExem.clear();
                txtCoeff.clear();

            }

        } catch (NumberFormatException ex) {
            showAlert("Erreur", "Les notes et le coefficient doivent être des nombres !");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Erreur", "Impossible d’enregistrer la note : " + ex.getMessage());
        }
    }
    @FXML
    void openEmargement() {
    	changerScene("EmargementView.fxml", "Gestion des Emargements");

    }
   
    @FXML private void openClasse() { changerScene("ClasseView.fxml", "Gestion des Classes"); }
    @FXML private void openEleve() { changerScene("EleveForm.fxml", "Gestion des Élèves"); }
    @FXML private void openMatiere() { changerScene("Matiere.fxml", "Gestion des Matières"); }
    @FXML private void openNote() { changerScene("NoteView.fxml", "Gestion des Notes"); }
    @FXML private void openEnseignant() { changerScene("EnseignantView.fxml", "Gestion des Enseignants"); }
    @FXML private void openTransaction() { changerScene("PaiementEnseignant.fxml", "Gestion des Transactions"); }
    @FXML private void openBulletin() { changerScene("BulletinView.fxml", "Gestion des Bulletins"); }
    @FXML private void openEmploidutemps() { changerScene("EmploiDutempsView.fxml", "Gestion des Emplois du Temps"); }
    @FXML private void openComptabilite() { changerScene("EnseignementView.fxml", "Gestion des Enseignements"); }
    @FXML
    private void retourAccueil() {
      
    }

    

    private void changerScene(String fxml, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) cbClasse.getScene().getWindow();
            stage.setScene(new Scene(root, 1283, 657));
            stage.setTitle(titre);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

       
    
    
        
    // --- Alert ---
    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}



    // --- Navigation entre pages ---
    
    
    