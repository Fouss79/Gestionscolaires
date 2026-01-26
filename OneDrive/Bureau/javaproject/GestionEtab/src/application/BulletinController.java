package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.FileOutputStream;
import java.sql.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class BulletinController {

    // TableView Notes
    @FXML private TableView<Note> tableNotes;
    @FXML private TableColumn<Note, String> colMatiere;
    @FXML private TableColumn<Note, Double> colNClass;
    @FXML private TableColumn<Note, Double> colNExem;
    @FXML private TableColumn<Note, Double> colCoeff;
    @FXML private TableColumn<Note, Double> colMoyenne;

    // ComboBox
    @FXML private ComboBox<String> cbClasse;
    @FXML private ComboBox<Eleve> cbEleve;
    @FXML private ComboBox<String> cbPeriode;
    @FXML private ComboBox<String> cbAnnee;

    // Labels
    @FXML private Label lblMoyenne;
    @FXML private Label lblRang;

    private ObservableList<Note> notes = FXCollections.observableArrayList();
    private ObservableList<Eleve> elevesCombo = FXCollections.observableArrayList();
    private ObservableList<String> periodes = FXCollections.observableArrayList(
            "Trimestre 1", "Trimestre 2", "Trimestre 3", "Année"
    );

    // --- Connexion JDBC ---
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/gestionecole";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    @FXML
    public void initialize() {
        // Colonnes TableView Notes
        colMatiere.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("matiereNom"));
        colNClass.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nClass"));
        colNExem.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nExem"));
        colCoeff.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("coeff"));
        colMoyenne.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("moyenne"));
        tableNotes.setItems(notes);

        // Périodes
        cbPeriode.setItems(periodes);
        cbPeriode.setDisable(false);

        // Charger années et classes
        loadAnnees();
        loadClasses();

        // Actions
        cbAnnee.setOnAction(e -> handleAnneeSelected());
        cbClasse.setOnAction(e -> handleClasseSelected());
        cbEleve.setOnAction(e -> afficherBulletin());
        cbPeriode.setOnAction(e -> afficherBulletin());
    }

    // --- Charger années scolaires ---
    private void loadAnnees() {
        cbAnnee.getItems().clear();
        String sql = "SELECT id, libelle FROM anneescolaire ORDER BY libelle DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cbAnnee.getItems().add(rs.getInt("id") + " - " + rs.getString("libelle"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Charger toutes les classes ---
    private void loadClasses() {
        cbClasse.getItems().clear();
        String sql = "SELECT id, nom, niveau FROM classe ORDER BY nom";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cbClasse.getItems().add(
                        rs.getInt("id") + " - " + rs.getString("nom") + " (" + rs.getString("niveau") + ")"
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Quand on sélectionne une année ---
    private void handleAnneeSelected() {
        // Recharge élèves selon année et classe
        handleClasseSelected();
    }

    // --- Quand on sélectionne une classe ---
    private void handleClasseSelected() {
        String selectedClasse = cbClasse.getValue();
        String selectedAnnee = cbAnnee.getValue();
        if (selectedClasse == null || selectedAnnee == null) return;

        elevesCombo.clear();
        long classeId = Long.parseLong(selectedClasse.split(" - ")[0]);
        long anneeId = Long.parseLong(selectedAnnee.split(" - ")[0]);

        String sql = """
            SELECT e.id, e.numero_matricule, e.nom, e.prenom
            FROM inscription i
            JOIN eleve e ON i.eleve_id = e.id
            WHERE i.classe_id = ? AND i.annee_id = ?
            ORDER BY e.nom, e.prenom
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, classeId);
            ps.setLong(2, anneeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Eleve e = new Eleve();
                e.setId(rs.getLong("id"));
                e.setNumeroMatricule(rs.getString("numero_matricule"));
                e.setNom(rs.getString("nom"));
                e.setPrenom(rs.getString("prenom"));
                elevesCombo.add(e);
            }
            cbEleve.setItems(elevesCombo);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Afficher le bulletin d’un élève ---
    private void afficherBulletin() {
        Eleve eleve = cbEleve.getValue();
        String classeNom = cbClasse.getValue() != null
                ? cbClasse.getValue().split(" - ")[1].split("\\(")[0].trim()
                : null;
        String periode = cbPeriode.getValue();
        String anneeIdStr = cbAnnee.getValue() != null ? cbAnnee.getValue().split(" - ")[0] : null;

        if (eleve == null || classeNom == null || periode == null || anneeIdStr == null) return;

        long anneeId = Long.parseLong(anneeIdStr);

        if (!estInscrit(eleve.getId(), classeNom, anneeId)) {
            notes.clear();
            lblMoyenne.setText("");
            lblRang.setText("");
            return;
        }

        loadNotes(eleve.getId(), classeNom, periode);
        calculerMoyenneEtRang(eleve.getId(), classeNom, periode);
    }

    // --- Vérifier inscription ---
    private boolean estInscrit(long eleveId, String classeNom, long anneeId) {
        String sql = """
            SELECT COUNT(*) 
            FROM inscription i
            JOIN classe c ON i.classe_id = c.id
            WHERE i.eleve_id=? AND c.nom=? AND i.annee_id=?
        """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eleveId);
            ps.setString(2, classeNom);
            ps.setLong(3, anneeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // --- Charger notes depuis la DB ---
    private void loadNotes(long eleveId, String classe, String periode) {
        notes.clear();
        String sql = """
            SELECT n.n_class, n.n_exem, n.coeff, m.matiere AS matiereNom
            FROM note n
            JOIN enseignement m ON n.matiere_id = m.id
            WHERE n.eleve_id = ? AND n.classe = ? AND n.periode = ?
        """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eleveId);
            ps.setString(2, classe);
            ps.setString(3, periode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Note note = new Note(
                        0, periode, classe, "", rs.getString("matiereNom"),
                        rs.getDouble("n_class"), rs.getDouble("n_exem"), rs.getDouble("coeff")
                );
                notes.add(note);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Calculer moyenne et rang ---
    private void calculerMoyenneEtRang(long eleveId, String classe, String periode) {
        try (Connection conn = getConnection()) {
            // Moyenne élève
            String sqlMoyenne = """
                SELECT SUM(((n_class + n_exem*2)/3)*coeff)/SUM(coeff) AS moyenne
                FROM note
                WHERE eleve_id=? AND classe=? AND periode=?
            """;
            PreparedStatement ps = conn.prepareStatement(sqlMoyenne);
            ps.setLong(1, eleveId);
            ps.setString(2, classe);
            ps.setString(3, periode);
            ResultSet rs = ps.executeQuery();
            double moyenne = rs.next() ? rs.getDouble("moyenne") : 0;
            lblMoyenne.setText(String.format("%.2f", moyenne));

            // Rang
            String sqlRang = """
                SELECT eleve_id, SUM(((n_class + n_exem*2)/3)*coeff)/SUM(coeff) AS moyenne
                FROM note
                WHERE classe=? AND periode=?
                GROUP BY eleve_id
                ORDER BY moyenne DESC
            """;
            ps = conn.prepareStatement(sqlRang);
            ps.setString(1, classe);
            ps.setString(2, periode);
            rs = ps.executeQuery();
            int rang = 1;
            int effectif = 0;
            while (rs.next()) {
                effectif++;
                if (rs.getLong("eleve_id") == eleveId) break;
                rang++;
            }
            lblRang.setText(rang + " / " + effectif);

        } catch (SQLException e) { e.printStackTrace(); }
    }
    @FXML
    private void exporterPDF() {
        Eleve eleve = cbEleve.getValue();
        String periode = cbPeriode.getValue();
        String classeNom = cbClasse.getValue() != null
                ? cbClasse.getValue().split(" - ")[1].split("\\(")[0].trim()
                : null;
        String anneeIdStr = cbAnnee.getValue() != null ? cbAnnee.getValue().split(" - ")[0] : null;

        if (eleve == null || classeNom == null || periode == null || anneeIdStr == null) {
            showAlert("Erreur", "Sélectionner un élève, une classe, une année et une période.");
            return;
        }

        long anneeId = Long.parseLong(anneeIdStr);

        // Vérifier inscription
        if (!estInscrit(eleve.getId(), classeNom, anneeId)) {
            showAlert("Erreur", "L'élève n'est pas inscrit pour cette année et cette classe !");
            return;
        }

        if (notes.isEmpty()) {
            showAlert("Erreur", "Aucune note à exporter !");
            return;
        }

        // Choix du fichier PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le bulletin PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        var file = fileChooser.showSaveDialog(cbClasse.getScene().getWindow());
        if (file == null) return;

        try (FileOutputStream fos = new FileOutputStream(file)) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, fos);
            doc.open();

            // Titre
            com.itextpdf.text.Font titleFont = com.itextpdf.text.FontFactory.getFont(
                    com.itextpdf.text.FontFactory.HELVETICA_BOLD, 16
            );
            Paragraph titre = new Paragraph("BULLETIN SCOLAIRE", titleFont);
            titre.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            doc.add(titre);
            doc.add(new Paragraph(" "));

            // Infos élève
            com.itextpdf.text.Font infoFont = com.itextpdf.text.FontFactory.getFont(
                    com.itextpdf.text.FontFactory.HELVETICA, 12
            );
            doc.add(new Paragraph("Nom: " + eleve.getNom() + " " + eleve.getPrenom(), infoFont));
            doc.add(new Paragraph("Classe: " + classeNom + " | Année: " + cbAnnee.getValue().split(" - ")[1], infoFont));
            doc.add(new Paragraph("Période: " + periode, infoFont));
            doc.add(new Paragraph(" "));

            // Tableau des notes
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            String[] headers = {"Matière", "Note Classe", "Note Examen", "Coeff", "Moyenne"};
            com.itextpdf.text.Font headerFont = com.itextpdf.text.FontFactory.getFont(
                    com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12
            );

            for (String h : headers) {
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new Paragraph(h, headerFont));
                cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            com.itextpdf.text.Font cellFont = com.itextpdf.text.FontFactory.getFont(
                    com.itextpdf.text.FontFactory.HELVETICA, 12
            );
            for (Note n : notes) {
                com.itextpdf.text.pdf.PdfPCell c1 = new com.itextpdf.text.pdf.PdfPCell(new Paragraph(n.getMatiereNom(), cellFont));
                c1.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                table.addCell(c1);

                com.itextpdf.text.pdf.PdfPCell c2 = new com.itextpdf.text.pdf.PdfPCell(new Paragraph(String.valueOf(n.getNClass()), cellFont));
                c2.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                table.addCell(c2);

                com.itextpdf.text.pdf.PdfPCell c3 = new com.itextpdf.text.pdf.PdfPCell(new Paragraph(String.valueOf(n.getNExem()), cellFont));
                c3.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                table.addCell(c3);

                com.itextpdf.text.pdf.PdfPCell c4 = new com.itextpdf.text.pdf.PdfPCell(new Paragraph(String.valueOf(n.getCoeff()), cellFont));
                c4.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                table.addCell(c4);

                com.itextpdf.text.pdf.PdfPCell c5 = new com.itextpdf.text.pdf.PdfPCell(new Paragraph(String.format("%.2f", n.getMoyenne()), cellFont));
                c5.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                table.addCell(c5);
            }

            doc.add(table);

            // Moyenne et rang
            com.itextpdf.text.Font resultFont = com.itextpdf.text.FontFactory.getFont(
                    com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12
            );
            Paragraph moy = new Paragraph("Moyenne Générale: " + lblMoyenne.getText(), resultFont);
            moy.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            doc.add(moy);

            Paragraph rang = new Paragraph("Rang: " + lblRang.getText(), resultFont);
            rang.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            doc.add(rang);

            doc.close();
            showAlert("Succès", "Le bulletin PDF a été généré avec succès !");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de générer le PDF : " + e.getMessage());
        }
    }

    
    // Optionnel : simple alert
    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }
} 


   
