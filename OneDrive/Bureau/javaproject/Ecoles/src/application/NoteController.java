
package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;


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
    @FXML 
    private TableColumn<Note, Double> colMoyenne;
    @FXML
    private TableColumn<Note, String> colMention;

    

    // ComboBox
    @FXML private ComboBox<String> comboAnnee;
    @FXML private ComboBox<String> cbClasse;
    @FXML private ComboBox<Eleve> cbEleve;
    @FXML private ComboBox<String> cbMatiere;
    @FXML private ComboBox<String> cbPeriode;
    @FXML
    private Label lblMention;
    


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
        colMention.setCellValueFactory(new PropertyValueFactory<>("mention"));
        colMention.setCellFactory(column -> new TableCell<Note, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);

                    switch (item) {
                        case "Insuffisant" -> setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        case "Passable", "Assez Bien" -> setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                        default -> setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    }
                }
            }
        });



        tableNotes.setItems(notes);

        

        // Périodes
        cbPeriode.setItems(periodes);
        cbPeriode.setDisable(false); // toujours activé

        // Charger années scolaires
        chargerAnnees();
        setColorOnNoteColumn(colNClass);
        setColorOnNoteColumn(colNExem);
        setColorOnNoteColumn(colMoyenne);


        // Actions
        
        cbPeriode.setOnAction(e -> {

            if (cbClasse.getValue() == null) return;

            long classeId = Long.parseLong(cbClasse.getValue().split(" - ")[0]);

            loadNotesFromDB(classeId, cbEleve.getValue(), cbPeriode.getValue());
           
            afficherMoyenne();
        });


        comboAnnee.setOnAction(e -> handleAnneeSelected());
        cbClasse.setOnAction(e -> handleClasseSelected());
    
        


        cbEleve.setOnAction(e -> {

            if (cbClasse.getValue() == null) return;
            long classeId = Long.parseLong(cbClasse.getValue().split(" - ")[0]);

            loadNotesFromDB(classeId, cbEleve.getValue(), cbPeriode.getValue());updateCoeff();
            afficherMoyenne();
        });
       
       


        cbMatiere.setOnAction(e -> updateCoeff());

           }
    
    

    // --- Charger années scolaires ---
    private void chargerAnnees() {

        comboAnnee.getItems().clear();

        String sql = "SELECT id, libelle, active FROM anneescolaire ORDER BY libelle DESC";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            String anneeActive = null;

            while (rs.next()) {

                String item = rs.getInt("id") + " - " + rs.getString("libelle");
                comboAnnee.getItems().add(item);

                if (rs.getInt("active") == 1) {
                    anneeActive = item;
                }
            }

            // 🔥 Sélection automatique de l'année active
            if (anneeActive != null) {
                comboAnnee.setValue(anneeActive);
                handleAnneeSelected(); // recharge les classes automatiquement
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    // --- Quand on sélectionne une année ---
    @FXML
    private void handleAnneeSelected() {

        String selected = comboAnnee.getValue();
        if (selected == null) return;

        int idAnnee = Integer.parseInt(selected.split(" - ")[0]);

        cbClasse.getItems().clear();
        elevesCombo.clear();

        String sql = """
            SELECT DISTINCT c.id,
                   n.nom AS niveau,
                   s.nom AS serie,
                   g.nom AS groupe
            FROM classe c
            JOIN niveau n ON c.niveau_id = n.id
            LEFT JOIN serie s ON c.serie_id = s.id
            JOIN groupe g ON c.groupe_id = g.id
            JOIN inscription i ON i.classe_id = c.id
            WHERE i.annee_id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAnnee);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String niveau = rs.getString("niveau");
                String serie = rs.getString("serie");
                String groupe = rs.getString("groupe");

                String classeText =
                        niveau +
                        (serie != null ? " - " + serie : "") +
                        " - " + groupe;

                cbClasse.getItems().add(
                        rs.getInt("id") + " - " + classeText
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    
    
    
    private void loadNotesFromDB(long classeId, Eleve eleveFilter, String periodeFilter) {

        notes.clear();

        if (comboAnnee.getValue() == null) return;

        long anneeId = Long.parseLong(comboAnnee.getValue().split(" - ")[0]);

        StringBuilder sql = new StringBuilder("""
            SELECT n.id, n.periode, n.n_class, n.n_exem, n.coeff,
                   e.nom AS eleveNom, e.prenom AS elevePrenom,
                   m.nom AS matiereNom
            FROM note n
            JOIN eleve e ON n.eleve_id = e.id
            JOIN matiere m ON n.matiere_id = m.id
            WHERE n.classe_id = ?
            AND n.annee_id = ?
        """);

        if (eleveFilter != null) sql.append(" AND e.id = ?");
        if (periodeFilter != null) sql.append(" AND n.periode = ?");

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;

            ps.setLong(index++, classeId);
            ps.setLong(index++, anneeId);

            if (eleveFilter != null) ps.setLong(index++, eleveFilter.getId());
            if (periodeFilter != null) ps.setString(index++, periodeFilter);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Note note = new Note(
                        rs.getLong("id"),
                        rs.getString("periode"),
                        "", // classe non nécessaire
                        rs.getString("eleveNom") + " " + rs.getString("elevePrenom"),
                        rs.getString("matiereNom"),
                        rs.getDouble("n_class"),
                        rs.getDouble("n_exem"),
                        rs.getDouble("coeff")
                );

                notes.add(note);
            }

            tableNotes.setItems(notes);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    private Double calculerMoyenneElevePeriode(long eleveId, String periode, long classeId) {

        if (comboAnnee.getValue() == null) return null;

        long anneeId = Long.parseLong(comboAnnee.getValue().split(" - ")[0]);

        String sql = """
            SELECT SUM(((n.n_class + n.n_exem*2)/3) * n.coeff) / SUM(n.coeff) AS moyenne
            FROM note n
            WHERE n.eleve_id = ?
            AND n.periode = ?
            AND n.classe_id = ?
            AND n.annee_id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, eleveId);
            ps.setString(2, periode);
            ps.setLong(3, classeId);
            ps.setLong(4, anneeId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("moyenne");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }    
    
    
   

    // --- Quand on sélectionne une classe ---
    @FXML
    private void handleClasseSelected() {
        String selectedClasse = cbClasse.getValue();
        String selectedAnnee = comboAnnee.getValue();
        if (selectedClasse == null || selectedAnnee == null) return;

        elevesCombo.clear();
        long classeId = Long.parseLong(selectedClasse.split(" - ")[0]);
         String idAnnee = selectedAnnee.split(" - ")[0];

        // Charger élèves inscrits
        String sql = """
            SELECT e.id, e.numero_matricule, e.nom, e.prenom,
            
            n.nom AS niveau, s.nom AS serie, g.nom AS groupe
            
            FROM eleve e
            JOIN inscription i ON i.eleve_id = e.id
            JOIN classe c ON c.id = i.classe_id
            LEFT JOIN niveau n ON c.niveau_id = n.id
            LEFT JOIN serie s ON c.serie_id = s.id
            LEFT JOIN groupe g ON c.groupe_id = g.id
            
            WHERE i.classe_id = ? AND i.annee_id = ?
            ORDER BY e.nom, e.prenom
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, classeId);
            ps.setString(2, idAnnee);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Eleve e = new Eleve();
                e.setId(rs.getLong("id"));
                e.setNumeroMatricule(rs.getString("numero_matricule"));
                e.setNom(rs.getString("nom"));
                e.setPrenom(rs.getString("prenom"));
             // Affichage niveau + série + groupe
                String classeFull = rs.getString("niveau") + " " 
                                  + rs.getString("serie") + " " 
                                  + rs.getString("groupe");
                e.setClasseNom(classeFull);

                
                
                elevesCombo.add(e);
            }
            cbEleve.setItems(elevesCombo);

            // Charger matières de cette classe
            loadMatieresByClasse(classeId);

            cbPeriode.setDisable(false); // réactiver
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadMatieresByClasse(long classeId) {

        if (comboAnnee.getValue() == null) return;

        long anneeId = Long.parseLong(comboAnnee.getValue().split(" - ")[0]);

        matieresCombo.clear();

        String sql = """
            SELECT m.id, m.nom, e.coeff
            FROM enseignement e
            JOIN matiere m ON e.matiere_id = m.id
            WHERE e.annee_id = ?
            AND e.classe_id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, anneeId);
            ps.setLong(2, classeId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                matieresCombo.add(
                        rs.getLong("id") + " - " +
                        rs.getString("nom") + " (" +
                        rs.getInt("coeff") + ")"
                );
            }

            cbMatiere.setItems(matieresCombo);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }   
    private int getEffectifClasse(long classeId, long anneeId) {

        String sql = "SELECT COUNT(*) FROM inscription WHERE classe_id = ? AND annee_id = ?";

        try (Connection conn = Database.connect();
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
    
       private void afficherMoyenne() {

        if (cbEleve.getValue() == null || cbPeriode.getValue() == null || cbClasse.getValue() == null)
            return;

        Eleve eleve = cbEleve.getValue();
        String periode = cbPeriode.getValue();
        String classeNom = cbClasse.getValue().split(" - ")[1].split("\\(")[0].trim();

        long classeId = Long.parseLong(cbClasse.getValue().split(" - ")[0]);
        long anneeId = Long.parseLong(comboAnnee.getValue().split(" - ")[0]);

        Double moyenne = calculerMoyenneElevePeriode(eleve.getId(), periode, classeId);
        Integer rang = calculerRangEleve(eleve.getId(), classeId, periode);
        int effectif = getEffectifClasse(classeId, anneeId);

        if (moyenne != null && rang != null) {

            lblMoyenne.setText(String.format("%.2f", moyenne));
            lblRang.setText(rang + " / " + effectif);
            afficherMention(moyenne);  // 🔥 AJOUTER ICI
        
        }
    }
    
       private ResultSet getMoyennesClasse(long classeId, String periode, long anneeId, Connection conn) throws SQLException {

    	    String sql = """
    	        SELECT n.eleve_id,
    	               SUM(((IFNULL(n.n_class,0) + IFNULL(n.n_exem,0)*2)/3) * IFNULL(n.coeff,1)) 
    	               / SUM(IFNULL(n.coeff,1)) AS moyenne
    	        FROM note n
    	        WHERE n.classe_id = ?
    	        AND n.periode = ?
    	        AND n.annee_id = ?
    	        GROUP BY n.eleve_id
    	        ORDER BY moyenne DESC
    	    """;

    	    PreparedStatement ps = conn.prepareStatement(sql);
    	    ps.setLong(1, classeId);
    	    ps.setString(2, periode);
    	    ps.setLong(3, anneeId);

    	    return ps.executeQuery();
    	}       
       private Integer calculerRangEleve(long eleveId, long classeId, String periode) {

    	    if (comboAnnee.getValue() == null) return null;

    	    long anneeId = Long.parseLong(comboAnnee.getValue().split(" - ")[0]);

    	    try (Connection conn = Database.connect()) {

    	        ResultSet rs = getMoyennesClasse(classeId, periode, anneeId, conn);

    	        int rang = 1;
    	        while (rs.next()) {
    	            if (rs.getLong("eleve_id") == eleveId) return rang;
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

        if (cbClasse.getValue() == null || 
            cbMatiere.getValue() == null || 
            comboAnnee.getValue() == null) return;

        long classeId = Long.parseLong(cbClasse.getValue().split(" - ")[0]);
        long matiereId = Long.parseLong(cbMatiere.getValue().split(" - ")[0]);
        long anneeId = Long.parseLong(comboAnnee.getValue().split(" - ")[0]);

        String sql = """
            SELECT coeff FROM enseignement
            WHERE classe_id = ? 
            AND matiere_id = ?
            AND annee_id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, classeId);
            ps.setLong(2, matiereId);
            ps.setLong(3, anneeId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtCoeff.setText(String.valueOf(rs.getInt("coeff")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        String classeStr = cbClasse.getValue();
        String anneeStr = comboAnnee.getValue();

        if ("Année".equals(periode)) {
            showAlert("Erreur", "On ne saisit pas de notes pour la période Année.");
            return;
        }

        if (e == null || matiereStr == null || periode == null ||
            classeStr == null || anneeStr == null ||
            txtNClass.getText().isEmpty() ||
            txtNExem.getText().isEmpty()) {

            showAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        try {
            double nClass = Double.parseDouble(txtNClass.getText());
            double nExem = Double.parseDouble(txtNExem.getText());

            if (nClass < 0 || nClass > 20 || nExem < 0 || nExem > 20) {
                showAlert("Erreur", "Les notes doivent être comprises entre 0 et 20 !");
                return;
            }

            // 🔥 Extraire les ID correctement
            long classeId = Long.parseLong(classeStr.split(" - ")[0]);
            long matiereId = Long.parseLong(matiereStr.split(" - ")[0]);
            long anneeId = Long.parseLong(anneeStr.split(" - ")[0]);

            try (Connection conn = Database.connect()) {

                // 🔹 1️⃣ Vérifier que l’enseignement existe
                String checkEns = """
                    SELECT coeff FROM enseignement
                    WHERE classe_id = ?
                    AND matiere_id = ?
                    AND annee_id = ?
                """;

                double coeff;

                try (PreparedStatement ps = conn.prepareStatement(checkEns)) {
                    ps.setLong(1, classeId);
                    ps.setLong(2, matiereId);
                    ps.setLong(3, anneeId);

                    ResultSet rs = ps.executeQuery();

                    if (!rs.next()) {
                        showAlert("Erreur", "Cette matière n'est pas enseignée dans cette classe !");
                        return;
                    }

                    coeff = rs.getDouble("coeff"); // 🔥 coeff officiel
                }

                // 🔹 2️⃣ Vérifier doublon
                String checkNote = """
                    SELECT COUNT(*) FROM note
                    WHERE eleve_id = ?
                    AND matiere_id = ?
                    AND periode = ?
                    AND classe_id = ?
                    AND annee_id = ?
                """;

                try (PreparedStatement ps = conn.prepareStatement(checkNote)) {
                    ps.setLong(1, e.getId());
                    ps.setLong(2, matiereId);
                    ps.setString(3, periode);
                    ps.setLong(4, classeId);
                    ps.setLong(5, anneeId);

                    ResultSet rs = ps.executeQuery();
                    rs.next();

                    if (rs.getInt(1) > 0) {
                        showAlert("Attention", "Une note existe déjà pour cet élève !");
                        return;
                    }
                }

                // 🔹 3️⃣ Insertion propre
                String insertSql = """
                    INSERT INTO note(
                        eleve_id,
                        matiere_id,
                        classe_id,
                        annee_id,
                        periode,
                        n_class,
                        n_exem,
                        coeff
                    )
                    VALUES(?, ?, ?, ?, ?, ?, ?, ?)
                """;

                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setLong(1, e.getId());
                    ps.setLong(2, matiereId);
                    ps.setLong(3, classeId);
                    ps.setLong(4, anneeId);
                    ps.setString(5, periode);
                    ps.setDouble(6, nClass);
                    ps.setDouble(7, nExem);
                    ps.setDouble(8, coeff);

                    ps.executeUpdate();
                }

                showAlert("Succès", "Note ajoutée avec succès !");
                txtNClass.clear();
                txtNExem.clear();
             // 🔄 Recharger la liste des notes après insertion
                String classeNom = classeStr.split(" - ")[1]
                        .split("\\(")[0]
                        .trim();

                loadNotesFromDB(classeId, cbEleve.getValue(), cbPeriode.getValue());
                afficherMoyenne();


            }

        } catch (NumberFormatException ex) {
            showAlert("Erreur", "Les notes doivent être des nombres !");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Erreur", "Erreur base de données !");
        }
    }
    @FXML
    void openEmargement() {
    	changerScene("EmargementView.fxml", "Gestion des Emargements");

    }
   
    @FXML private void openClasse() { changerScene("ClasseView.fxml", "Gestion des Classes"); }
    @FXML private void openEleve() { changerScene("ElevesView.fxml", "Gestion des Élèves"); }
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
    @FXML
    void openHabilitation() {
    	 changerScene("HabilitationView.fxml", "Habilitation");
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

    private String getMention(double moyenne) {

        if (moyenne < 10) return "Insuffisant";
        else if (moyenne < 12) return "Passable";
        else if (moyenne < 14) return "Assez Bien";
        else if (moyenne < 16) return "Bien";
        else if (moyenne < 18) return "Très Bien";
        else return "Excellent";
    }
    private void afficherMention(double moyenne) {

        String mention = getMention(moyenne);
        lblMention.setText(mention);

        if (moyenne < 10) {
            lblMention.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else if (moyenne < 14) {
            lblMention.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
        } else {
            lblMention.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }
    }
    
    private double getMoyennePremier(String classeId, String periode) {

        if (comboAnnee.getValue() == null) return 0;

        long anneeId = Long.parseLong(comboAnnee.getValue().split(" - ")[0]);

        double moyennePremier = 0;

        String sql = """
            SELECT MAX(moyenne_generale) AS max_moyenne
            FROM (
                SELECT n.eleve_id,
                       SUM(((n.n_class + 2*n.n_exem)/3)*n.coeff) / SUM(n.coeff) AS moyenne_generale
                FROM note n
                WHERE n.classe_id = ?
                  AND n.periode = ?
                  AND n.annee_id = ?
                GROUP BY n.eleve_id
            ) AS moyennes
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, classeId);
            ps.setString(2, periode);
            ps.setLong(3, anneeId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                moyennePremier = rs.getDouble("max_moyenne");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return moyennePremier;
    }
    @FXML
    private void exporterPDF() {

        Eleve eleve = cbEleve.getValue();
        String periode = cbPeriode.getValue();
        String classeNom = cbClasse.getValue() != null
                ? cbClasse.getValue().split(" - ")[1].split("\\(")[0].trim()
                : null;
       String classeid = cbClasse.getValue() != null
                ? cbClasse.getValue().split(" - ")[0].split("\\(")[0].trim()
                : null;
       
        String anneeLabel = comboAnnee.getValue() != null
                ? comboAnnee.getValue().split(" - ")[1]
                : null;

        if (eleve == null || classeNom == null || periode == null || anneeLabel == null) {
            showAlert("Erreur", "Sélectionner un élève, une classe, une année et une période.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le bulletin PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(cbClasse.getScene().getWindow());
        if (file == null) return;

        try (FileOutputStream fos = new FileOutputStream(file)) {

            Document doc = new Document();
            PdfWriter.getInstance(doc, fos);
            doc.open();

            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            com.itextpdf.text.Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            // ==========================
            // EN-TÊTE
            // ==========================
            Paragraph lycee = new Paragraph("LYCEE PRIVE ELMOCTAR KONATE", titleFont);
            lycee.setAlignment(Element.ALIGN_CENTER);
            doc.add(lycee);

            Paragraph bulletin = new Paragraph("BULLETIN DE NOTES", boldFont);
            bulletin.setAlignment(Element.ALIGN_CENTER);
            doc.add(bulletin);

            doc.add(new Paragraph(" "));

            // ==========================
            // INFOS ELEVE
            // ==========================
            doc.add(new Paragraph("Année scolaire : " + anneeLabel, normalFont));
            doc.add(new Paragraph("Classe : " + classeNom, normalFont));
            doc.add(new Paragraph("Période : " + periode, normalFont));
            doc.add(new Paragraph("Nom : " + eleve.getNom() + " " + eleve.getPrenom(), normalFont));
            doc.add(new Paragraph("Matricule : " + eleve.getNumeroMatricule(), normalFont));
            doc.add(new Paragraph(" "));

            // ==========================
            // TABLEAU NOTES
            // ==========================
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);

            String[] headers = {
                    "Matières", "Note Classe", "Note Examen",
                    "Coeff", "Moy x Coef", "Appréciation"
            };

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Paragraph(h, boldFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            double totalGeneral = 0;
            double totalCoeff = 0;

            for (Note n : notes) {

                double moyCoef = n.getMoyenne() * n.getCoeff();

                table.addCell(new PdfPCell(new Paragraph(n.getMatiereNom(), normalFont)));
                table.addCell(centerCell(String.format("%.2f", n.getNClass()), normalFont));
                table.addCell(centerCell(String.format("%.2f", n.getNExem()), normalFont));
                table.addCell(centerCell(String.valueOf(n.getCoeff()), normalFont));
                table.addCell(centerCell(String.format("%.2f", moyCoef), normalFont));
                table.addCell(centerCell(n.getMention(), normalFont));

                totalGeneral += moyCoef;
                totalCoeff += n.getCoeff();
            }

            doc.add(table);

            // ==========================
            // RESULTATS
            // ==========================
            double moyenneGenerale = totalCoeff > 0 ? totalGeneral / totalCoeff : 0;

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Total Général : " + String.format("%.2f", totalGeneral), boldFont));
            doc.add(new Paragraph("Total Coeff : " + totalCoeff, boldFont));
            doc.add(new Paragraph("Moyenne Générale : " + String.format("%.2f", moyenneGenerale), boldFont));
            double moyennePremier = getMoyennePremier(classeid, periode);
            doc.add(new Paragraph("Moyenne du Premier : " 
                    + String.format("%.2f", moyennePremier), boldFont));
            doc.add(new Paragraph("Rang : " + lblRang.getText(), boldFont));

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("APPRÉCIATION GÉNÉRALE : " + getMention(moyenneGenerale), titleFont));

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Le Proviseur                         Parent", normalFont));

            doc.close();

            showAlert("Succès", "Bulletin généré avec succès !");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de générer le PDF : " + e.getMessage());
        }
    }

    
    private PdfPCell centerCell(String text, com.itextpdf.text.Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
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
    
    
    

   