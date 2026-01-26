package application;


import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;



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

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.DayOfWeek;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class EmploiDuTempsController {

    // JDBC
    private final String URL = "jdbc:mysql://localhost:3306/gestionEcole";
    private final String USER = "root";
    private final String PASSWORD = "";

    // Table et colonnes
    
    @FXML
    private Button btnBulletin;

    @FXML
    private Button btnClasse;

    @FXML
    private Button btnComptabilite;

    @FXML
    private Button btnEleve;

    @FXML
    private Button btnEmploidutemps;

    @FXML
    private Button btnEnseignant;

    @FXML
    private Button btnMatiere;

    @FXML
    private Button btnNote;

    @FXML
    private Button btnTableaudebord;

    @FXML
    private Button btnTransaction;
    @FXML private ComboBox<AnneeScolaires> cbAnnee;

    @FXML private TableColumn<EmploiDutemps, String> colAnnee;



    
    
    @FXML private TableView<EmploiDutemps> tableEmplois;
    @FXML private TableColumn<EmploiDutemps, Integer> colId;
    @FXML private TableColumn<EmploiDutemps, String> colJour;
    @FXML private TableColumn<EmploiDutemps, String> colHeureDebut;
    @FXML private TableColumn<EmploiDutemps, String> colHeureFin;
    @FXML private TableColumn<EmploiDutemps, String> colMatiere;
    @FXML private TableColumn<EmploiDutemps, String> colProfesseur;
    @FXML private TableColumn<EmploiDutemps, String> colClasse;
    

  


    @FXML
    void openTableaudebord(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Accueil.fxml"));
            Parent root = loader.load();

            // Création d'une nouvelle scène
            Scene scene = new Scene(root, 1024, 600);

            // Récupération de la fenêtre actuelle
            Stage stage = (Stage) cbClasse.getScene().getWindow();
            
            // Changement de scène
            stage.setScene(scene);
            stage.setTitle("Accueil");

        } catch (IOException e) {
            e.printStackTrace();
        }


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
    
    

    // 🔹 Navigation entre les pages
    
    @FXML
    void openEmargement(ActionEvent event) {
    	changerScene("EmargementView.fxml", "Gestion des Emargements");

    }
    @FXML private void retourAccueil() { System.out.println("Retour au Tableau de Bord"); }
    @FXML private void openClasse(ActionEvent event) { changerScene("ClasseView.fxml", "Gestion des Classes"); }
    @FXML private void openEleve(ActionEvent event) { changerScene("EleveForm.fxml", "Gestion des Élèves"); }
    @FXML private void openMatiere(ActionEvent event) { changerScene("Matiere.fxml", "Gestion des Matières"); }
    @FXML private void openNote(ActionEvent event) { changerScene("NoteView.fxml", "Gestion des Notes"); }
    @FXML private void openEnseignant(ActionEvent event) { changerScene("EnseignantView.fxml", "Gestion des Enseignants"); }
    @FXML private void openTransaction(ActionEvent event) { changerScene("PaiementEnseignant.fxml", "Gestion des Transactions"); }
    @FXML private void openBulletin(ActionEvent event) { changerScene("BulletinView.fxml", "Gestion des Bulletins"); }
    @FXML private void openEmploidutemps(ActionEvent event) { changerScene("EmploiDutempsView.fxml", "Gestion des Emplois du Temps"); }
    @FXML private void openComptabilite(ActionEvent event) { changerScene("EnseignementView.fxml", "Gestion des Cours"); }
    

    @FXML
    private void handleExporterPDF() {

        String classe = cbClasse.getValue();

        if (classe == null) {
            System.out.println("Veuillez choisir une classe");
            return;
        }

        genererPdfEmploiDuTempsParClasse(classe);
    }


    
    

    
   
   


   

  

 
  


    // Formulaire
    @FXML private ComboBox<String> cbJour;
    @FXML private ComboBox<String> cbHeureDebut;
    @FXML private ComboBox<String> cbHeureFin;
    @FXML private ComboBox<String> cbMatiere;
    @FXML private ComboBox<String> cbProfesseur;
    @FXML private ComboBox<String> cbClasse;

    private ObservableList<EmploiDutemps> emplois = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Config TableView
    	 colId.setCellValueFactory(new PropertyValueFactory<>("id"));
    	 colJour.setCellValueFactory(d ->
    	    new javafx.beans.property.SimpleStringProperty(d.getValue().getJourFrancais()));

    	    colHeureDebut.setCellValueFactory(new PropertyValueFactory<>("heureDebut"));
    	    colHeureFin.setCellValueFactory(new PropertyValueFactory<>("heureFin"));
    	    colMatiere.setCellValueFactory(new PropertyValueFactory<>("matiere"));
    	    colProfesseur.setCellValueFactory(new PropertyValueFactory<>("professeur"));
    	    colClasse.setCellValueFactory(new PropertyValueFactory<>("classe"));
    	    colAnnee.setCellValueFactory(new PropertyValueFactory<>("anneeLibelle"));


    	    loadJours();
    	    loadHeures();
    	    loadAnnees();
    	    loadEmplois();
    	    
    	    cbAnnee.setOnAction(e -> {
    	        AnneeScolaires a = cbAnnee.getValue();
    	        if (a != null) {
    	            loadClassesByAnnee(a.getId());   // classes de l'année
    	            loadEmploisParAnnee(a.getId());  // emplois de l'année ✅
    	            cbMatiere.getItems().clear();
    	            cbProfesseur.getItems().clear();
    	        }
    	    });


        
        // ✅ Quand on change de matière → charger les professeurs correspondants
    	    cbClasse.setOnAction(e -> {
    	        String classe = cbClasse.getValue();
    	        AnneeScolaires annee = cbAnnee.getValue();

    	        if (classe != null && annee != null) {
    	            loadMatieres(classe);
    	            loadEmploiDuTempsParClasseEtAnnee(classe, annee.getId()); 
    	        }
    	    });


        cbMatiere.setOnAction(e -> {
            String matiere = cbMatiere.getValue();
            String classe = cbClasse.getValue();
            if (matiere != null && classe != null) {
                loadProfesseurs(matiere, classe);
            }
        });

      
        loadEmplois();
    }

    private void loadJours() {
        cbJour.getItems().clear();
        for (DayOfWeek day : DayOfWeek.values()) {
            cbJour.getItems().add(day.name());
        }
    }

    private void loadHeures() {
        cbHeureDebut.getItems().clear();
        cbHeureFin.getItems().clear();
        for (int h = 8; h <= 18; h++) {
            cbHeureDebut.getItems().add(String.format("%02d:00", h));
            cbHeureDebut.getItems().add(String.format("%02d:30", h));
            cbHeureFin.getItems().add(String.format("%02d:00", h));
            cbHeureFin.getItems().add(String.format("%02d:30", h));
        }
    }
    
    private void loadAnnees() {

        cbAnnee.getItems().clear();

        String sql = "SELECT id, libelle FROM anneescolaire";

        try (Connection c = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                cbAnnee.getItems().add(
                    new AnneeScolaires(
                        rs.getInt("id"),
                        rs.getString("libelle")
                    )
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public void loadEmploiDuTempsParClasseEtAnnee(String classe, int anneeId) {

        emplois.clear();
        tableEmplois.getItems().clear();

        String sql = """
            SELECT e.*, a.libelle AS annee
            FROM emploi_du_temps e
            JOIN anneescolaire a ON e.annee_id = a.id
            WHERE e.classe = ? AND e.annee_id = ?
            ORDER BY 
              FIELD(e.jour,'MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'),
              e.heure_debut
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, classe);
            ps.setInt(2, anneeId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EmploiDutemps e = new EmploiDutemps(
                        rs.getInt("id"),
                        DayOfWeek.valueOf(rs.getString("jour")),
                        rs.getString("heure_debut"),
                        rs.getString("heure_fin"),
                        rs.getString("matiere"),
                        rs.getString("professeur"),
                        rs.getString("classe"),
                        rs.getInt("annee_id"),
                        rs.getString("annee")
                );
                emplois.add(e);
            }

            tableEmplois.setItems(emplois);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadEmploisParAnnee(int anneeId) {

        emplois.clear();
        tableEmplois.getItems().clear();

        String sql = """
            SELECT e.*, a.libelle AS annee
            FROM emploi_du_temps e
            JOIN anneescolaire a ON e.annee_id = a.id
            WHERE e.annee_id = ?
            ORDER BY 
              FIELD(e.jour,'MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'),
              e.heure_debut
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, anneeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EmploiDutemps e = new EmploiDutemps(
                        rs.getInt("id"),
                        DayOfWeek.valueOf(rs.getString("jour")),
                        rs.getString("heure_debut"),
                        rs.getString("heure_fin"),
                        rs.getString("matiere"),
                        rs.getString("professeur"),
                        rs.getString("classe"),
                        rs.getInt("annee_id"),
                        rs.getString("annee")
                );
                emplois.add(e);
            }

            tableEmplois.setItems(emplois);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadMatieres(String classeNom) {
        cbMatiere.getItems().clear();
        String sql = "SELECT DISTINCT matiere FROM enseignement WHERE classe = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, classeNom);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cbMatiere.getItems().add(rs.getString("matiere"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Quand on change de matiere ---
   
    

    // 🔹 Charger les professeurs par matière
    private void loadProfesseurs(String matiere, String classe) {
        cbProfesseur.getItems().clear();
        String sql = "SELECT DISTINCT enseignant FROM enseignement WHERE matiere = ? AND classe = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matiere);
            ps.setString(2, classe);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cbProfesseur.getItems().add(rs.getString("enseignant"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadClasses() {
        cbClasse.getItems().clear();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nom FROM classe")) {

            while (rs.next()) {
                cbClasse.getItems().add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadEmplois() {


    	    emplois.clear();              // ✅ important
    	    tableEmplois.getItems().clear();


        String sql = """
            SELECT e.*, a.libelle AS annee
            FROM emploi_du_temps e
            JOIN anneescolaire a ON e.annee_id = a.id
        """;

        try (Connection c = DB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                EmploiDutemps e = new EmploiDutemps(
                    rs.getInt("id"),
                    DayOfWeek.valueOf(rs.getString("jour")),
                    rs.getString("heure_debut"),
                    rs.getString("heure_fin"),
                    rs.getString("matiere"),
                    rs.getString("professeur"),
                    rs.getString("classe"),
                    rs.getInt("annee_id"),
                    rs.getString("annee")
                );
                emplois.add(e);
               
            }
            tableEmplois.setItems(emplois);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadClassesByAnnee(int anneeId) {

        cbClasse.getItems().clear();

        String sql = "SELECT DISTINCT classe FROM enseignement WHERE annee_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, anneeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cbClasse.getItems().add(rs.getString("classe"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   
               
            
            


    @FXML
    private void ajouterEmploi() {
        String jour = cbJour.getValue();
        String debut = cbHeureDebut.getValue();
        String fin = cbHeureFin.getValue();
        String matiere = cbMatiere.getValue();
     
        String prof = cbProfesseur.getValue();
        String classe = cbClasse.getValue();
        AnneeScolaires annee = cbAnnee.getValue();

        if (annee == null) {
            showAlert("Erreur", "Veuillez sélectionner l'année scolaire");
            return;
        }

        if (jour == null || debut == null || fin == null || matiere == null || prof == null || classe == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !").show();
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            // Vérifier chevauchement pour PROFESSEUR et CLASSE
            String checkSql = """
                SELECT COUNT(*) 
                FROM emploi_du_temps 
              WHERE jour = ?
AND annee_id = ?
AND (professeur = ? OR classe = ?)
AND NOT ( ? >= heure_fin OR ? <= heure_debut )
       """;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            	checkPs.setString(1, jour);
            	checkPs.setInt(2, annee.getId());
            	checkPs.setString(3, prof);
            	checkPs.setString(4, classe);
            	checkPs.setString(5, debut);
            	checkPs.setString(6, fin);
       ResultSet rs = checkPs.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    new Alert(Alert.AlertType.WARNING, "Chevauchement détecté pour le professeur ou la classe !").show();
                    return;
                }
            }

            // Insertion si pas de conflit
            String sql = """
                    INSERT INTO emploi_du_temps
                    (jour, heure_debut, heure_fin, matiere, professeur, classe, annee_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;   try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, jour);
                ps.setString(2, debut);
                ps.setString(3, fin);
                ps.setString(4, matiere);
                ps.setString(5, prof);
                ps.setString(6, classe);
                ps.setInt(7, annee.getId());

                int n = ps.executeUpdate();
                if (n > 0) {
                    ResultSet keys = ps.getGeneratedKeys();
                    int id = keys.next() ? keys.getInt(1) : 0;

                    EmploiDutemps e = new EmploiDutemps(id, DayOfWeek.valueOf(jour), debut, fin, matiere, prof, classe,annee.getId(),annee.getLibelle());
                    emplois.add(e);
                    tableEmplois.refresh();
                    clearForm();
                    new Alert(Alert.AlertType.INFORMATION, "Emploi ajouté !").show();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout !").show();
        }
    }
    public void loadEmploiDuTempsParClasse(String classe) {
    	 
       
    
        String sql = " SELECT e.*, a.libelle AS annee\r\n"
        		+ "FROM emploi_du_temps e\r\n"
        		+ "JOIN anneescolaire a ON e.annee_id = a.id\r\n"
        		+ "WHERE e.classe = ?\r\n"
        		+ "ORDER BY \r\n"
        		+ "  FIELD(e.jour, 'MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'),\r\n"
        		+ "  e.heure_debut;\r\n";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, classe);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

            	 EmploiDutemps e = new EmploiDutemps(
                         rs.getInt("id"),
                         DayOfWeek.valueOf(rs.getString("jour")),
                         rs.getString("heure_debut"),
                         rs.getString("heure_fin"),
                         rs.getString("matiere"),
                         rs.getString("professeur"),
                         rs.getString("classe"),
                         rs.getInt("annee_id"),
                         rs.getString("annee")
                     );

            	 emplois.add(e); // ta ObservableList
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierEmploi() {
    	AnneeScolaires annee = cbAnnee.getValue();
    	if (annee == null) {
    	    showAlert("Erreur", "Veuillez sélectionner l'année scolaire");
    	    return;
    	}

        EmploiDutemps selected = tableEmplois.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un emploi !").show();
            return;
        }

        String jour = cbJour.getValue();
        String debut = cbHeureDebut.getValue();
        String fin = cbHeureFin.getValue();
        String matiere = cbMatiere.getValue();
  
        String prof = cbProfesseur.getValue();
        String classe = cbClasse.getValue();

        if (jour == null || debut == null || fin == null || matiere == null || prof == null || classe == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !").show();
            return;
        }

        String sql = """
        		UPDATE emploi_du_temps SET
        		jour=?, heure_debut=?, heure_fin=?, matiere=?, professeur=?, classe=?, annee_id=?
        		WHERE id=?
        		""";

        		

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, jour);
            ps.setString(2, debut);
            ps.setString(3, fin);
            ps.setString(4, matiere);
          
            ps.setString(5, prof);
            ps.setString(6, classe);
            ps.setInt(7, annee.getId());
            ps.setInt(8, selected.getId());

            ps.executeUpdate();

            // Mettre à jour l'objet
            selected.setJour(DayOfWeek.valueOf(jour));
            selected.setHeureDebut(debut);
            selected.setHeureFin(fin);
            selected.setMatiere(matiere);
            selected.setProfesseur(prof);
            selected.setClasse(classe);
            selected.setAnneeId(annee.getId());
            selected.setAnneeLibelle(annee.getLibelle());


            tableEmplois.refresh();
            clearForm();
            new Alert(Alert.AlertType.INFORMATION, "Emploi modifié !").show();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification !").show();
        }
    }

    @FXML
    private void supprimerEmploi() {
        EmploiDutemps selected = tableEmplois.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un emploi !").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cet emploi ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            String sql = "DELETE FROM emploi_du_temps WHERE id=?";
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, selected.getId());
                ps.executeUpdate();
                emplois.remove(selected);
                tableEmplois.refresh();
                clearForm();
                new Alert(Alert.AlertType.INFORMATION, "Emploi supprimé !").show();

            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression !").show();
            }
        }
    }
    
  
    public void genererPdfEmploiDuTempsParClasse(String classe) {

        String dossier = System.getProperty("user.home") + "/Documents/";
        String fichier = dossier + "emploi_du_temps_" + classe + "_" + System.currentTimeMillis() + ".pdf";


        String sql = """
            SELECT jour, heure_debut, heure_fin, matiere, professeur
            FROM emploi_du_temps
            WHERE classe = ?
        """;

        // ⚠️ mêmes valeurs que dans la DB
        String[] joursDB = {"MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
        String[] joursFR = {"Lundi","Mardi","Mercredi","Jeudi","Vendredi","Samedi"};


        try {
            Document document = new Document(PageSize.A4.rotate()); // paysage
            PdfWriter.getInstance(document, new FileOutputStream(fichier));
            document.open();

            // ===== TITRE =====
            Font titreFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph titre = new Paragraph("EMPLOI DU TEMPS — Classe : " + classe, titreFont);
            titre.setAlignment(Element.ALIGN_CENTER);
            titre.setSpacingAfter(15);
            document.add(titre);

            // ===== LECTURE DB =====
            java.util.Map<String, java.util.Map<String, String>> grille = new java.util.HashMap<>();
            java.util.Set<String> heures = new java.util.TreeSet<>();

            Connection conn = DB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, classe);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String jour = rs.getString("jour");
                String heure = rs.getString("heure_debut") + "-" + rs.getString("heure_fin");
                String contenu = rs.getString("matiere") + "\n" + rs.getString("professeur");

                heures.add(heure);

                grille.putIfAbsent(heure, new java.util.HashMap<>());
                grille.get(heure).put(jour, contenu);
            }

            // ===== TABLE =====
            PdfPTable table = new PdfPTable(joursFR.length + 1); // +1 colonne heure
            table.setWidthPercentage(100);

            table.addCell(headerCell("Heure"));
            for (String j : joursFR) table.addCell(headerCell(j));

            for (String h : heures) {
                table.addCell(normalCell(h));
                for (String j : joursDB) {
                    String val = grille.getOrDefault(h, java.util.Map.of()).getOrDefault(j, "");
                    table.addCell(normalCell(val));
                }
            }


            document.add(table);
            document.close();

            java.awt.Desktop.getDesktop().open(new java.io.File(fichier));
            System.out.println("PDF généré : " + fichier);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private PdfPCell headerCell(String text) {
        Font f = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setBackgroundColor(BaseColor.LIGHT_GRAY);
        c.setPadding(6);
        return c;
    }

    private PdfPCell normalCell(String text) {
        Font f = new Font(Font.FontFamily.HELVETICA, 10);
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setPadding(6);
        return c;
    }
    

    private void ajouterCelluleHeader(PdfPTable table, String texte) {
        Font font = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(texte, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }


    private void clearForm() {
        cbJour.setValue(null);
        cbHeureDebut.setValue(null);
        cbHeureFin.setValue(null);
        cbMatiere.setValue(null);
        cbProfesseur.setValue(null);
        cbClasse.setValue(null);
        cbAnnee.setValue(null);

    }
    
    // Optionnel : simple alert
    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
