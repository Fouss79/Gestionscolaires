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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class EmploiDuTempsGrilleController {

   
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



    
    
      
    @FXML private TableView<EmploiGrille> tableGrille;
    @FXML private TableColumn<EmploiGrille, String> colHeure;
    @FXML private TableColumn<EmploiGrille, String> colLundi;
    @FXML private TableColumn<EmploiGrille, String> colMardi;
    @FXML private TableColumn<EmploiGrille, String> colMercredi;
    @FXML private TableColumn<EmploiGrille, String> colJeudi;
    @FXML private TableColumn<EmploiGrille, String> colVendredi;
    @FXML private TableColumn<EmploiGrille, String> colSamedi;
    

  


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

        // ✅ TABLE GRILLE SEULEMENT
        if (colHeure != null) colHeure.setCellValueFactory(new PropertyValueFactory<>("heure"));
        if (colLundi != null) colLundi.setCellValueFactory(new PropertyValueFactory<>("lundi"));
        if (colMardi != null) colMardi.setCellValueFactory(new PropertyValueFactory<>("mardi"));
        if (colMercredi != null) colMercredi.setCellValueFactory(new PropertyValueFactory<>("mercredi"));
        if (colJeudi != null) colJeudi.setCellValueFactory(new PropertyValueFactory<>("jeudi"));
        if (colVendredi != null) colVendredi.setCellValueFactory(new PropertyValueFactory<>("vendredi"));
        if (colSamedi != null) colSamedi.setCellValueFactory(new PropertyValueFactory<>("samedi"));

        loadJours();
        loadHeures();
        loadAnnees();

        AnneeScolaires active = cbAnnee.getValue();
        if (active != null) {
            loadClassesByAnnee(active.getId());
        }

        cbJour.setOnAction(e -> {
            String classe = cbClasse.getValue();
            AnneeScolaires annee = cbAnnee.getValue();

            if (classe != null && annee != null) {
                chargerGrille(classe, annee.getId());
            }
        });

        cbAnnee.setOnAction(e -> {
            AnneeScolaires a = cbAnnee.getValue();
            if (a != null) {
                loadClassesByAnnee(a.getId());
                cbMatiere.getItems().clear();
                cbProfesseur.getItems().clear();
            }
        });

        cbClasse.setOnAction(e -> {
            String value = cbClasse.getValue();
            AnneeScolaires annee = cbAnnee.getValue();

            if (value != null && annee != null) {
                int classeId = Integer.parseInt(value.split(" - ")[0]);
                loadMatieres(classeId);

                // 🔥 IMPORTANT
                chargerGrille(value, annee.getId());
            }
        });

        cbMatiere.setOnAction(e -> {
            String valueClasse = cbClasse.getValue();
            String valueMatiere = cbMatiere.getValue();
            AnneeScolaires annee = cbAnnee.getValue();

            if (valueClasse != null && valueMatiere != null && annee != null) {
                String nomMatiere = valueMatiere.split(" - ")[1];
                loadProfesseursParHabilitation(nomMatiere, annee.getLibelle());
            }
        });
    }    private void loadJours() {
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

        try (Connection c = Database.connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

        	while (rs.next()) {
        	    AnneeScolaires a = new AnneeScolaires(
        	        rs.getInt("id"),
        	        rs.getString("libelle")
        	    );
        	    cbAnnee.getItems().add(a);

        	    if (ContexteApplication.getAnneeId() == a.getId()) {
        	        cbAnnee.setValue(a);
        	    }
        	}

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
     
    public void chargerGrille(String classe, int anneeId) {

        ObservableList<EmploiGrille> grilleList = FXCollections.observableArrayList();

        String sql = """
            SELECT jour, heure_debut, heure_fin, matiere, professeur
            FROM emploi_du_temps
            WHERE classe = ? AND annee_id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, classe);
            ps.setInt(2, anneeId);

            ResultSet rs = ps.executeQuery();

            Map<String, EmploiGrille> map = new TreeMap<>((h1, h2) -> {

                String[] t1 = h1.split("-");
                String[] t2 = h2.split("-");

                int cmp = t1[0].compareTo(t2[0]); // heure début
                if (cmp != 0) return cmp;

                return t1[1].compareTo(t2[1]); // heure fin
            });

            while (rs.next()) {

                String jour = rs.getString("jour");
                String heure = rs.getString("heure_debut") + "-" + rs.getString("heure_fin");
                String contenu = rs.getString("matiere") + "\n" + rs.getString("professeur");

                EmploiGrille ligne = map.getOrDefault(heure, new EmploiGrille(heure));

                switch (jour) {
                    case "MONDAY" -> ligne.setLundi(contenu);
                    case "TUESDAY" -> ligne.setMardi(contenu);
                    case "WEDNESDAY" -> ligne.setMercredi(contenu);
                    case "THURSDAY" -> ligne.setJeudi(contenu);
                    case "FRIDAY" -> ligne.setVendredi(contenu);
                    case "SATURDAY" -> ligne.setSamedi(contenu);
                }

                map.put(heure, ligne);
            }

            grilleList.addAll(map.values());
            tableGrille.setItems(grilleList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   

    private void loadMatieres(int classeId) {

        cbMatiere.getItems().clear();

        String sql = """
            SELECT DISTINCT m.id, m.nom
            FROM enseignement e
            JOIN matiere m ON e.matiere_id = m.id
            WHERE e.classe_id = ?
            ORDER BY m.nom
            """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, classeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");

                cbMatiere.getItems().add(id + " - " + nom);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   
    
    // --- Quand on change de matiere ---
     
    
 // 🔹 Charger les professeurs depuis la table habilitation selon la matière et l'année scolaire
    private void loadProfesseursParHabilitation(String matiere, String annee) {
        cbProfesseur.getItems().clear();

        String sql = """
            SELECT DISTINCT e.id, e.nom, e.prenom
            FROM habilitation h
            JOIN enseignant e ON h.enseignant_id = e.id
            JOIN matiere m ON h.matiere_id = m.id
            JOIN anneescolaire a ON h.annee_id = a.id
            WHERE m.nom = ? AND a.libelle = ?
            ORDER BY e.nom, e.prenom
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matiere);
            ps.setString(2, annee);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	String enseignant = rs.getString("id")+" - " +rs.getString("nom") + " - " + rs.getString("prenom");
                cbProfesseur.getItems().add(enseignant);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les professeurs.");
        }
    }

 // 🔹 Charger les professeurs depuis la table habilitation selon la matière et l'année scolaire
    private void loadProfesseur(String matiere, String annee) {
        cbProfesseur.getItems().clear();

        String sql = "SELECT DISTINCT e.nom, e.prenom " +
                     "FROM habilitation h " +
                     "JOIN enseignant e ON h.enseignant_id = e.id " +
                     "JOIN matiere m ON h.matiere_id = m.id " +
                     "JOIN anneescolaire a ON h.annee_id = a.id " +
                     "WHERE m.nom = ? AND a.libelle = ?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matiere);
            ps.setString(2, annee);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String enseignant = rs.getString("nom") + " " + rs.getString("prenom");
                cbProfesseur.getItems().add(enseignant);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les professeurs.");
        }
    }
    

    // 🔹 Charger les professeurs par matière
    private void loadProfesseurs(String matiere, String classe) {
        cbProfesseur.getItems().clear();
        String sql = "SELECT DISTINCT enseignant FROM enseignement WHERE matiere = ? AND classe = ?";

        try (Connection conn = Database.connect();
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
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nom FROM classe")) {

            while (rs.next()) {
                cbClasse.getItems().add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        private void loadClassesByAnnee(int anneeId) {

        cbClasse.getItems().clear();

        String sql = """
            SELECT DISTINCT c.id,
                   n.nom AS niveau,
                   s.nom AS serie,
                   g.nom AS groupe
            FROM enseignement e
            JOIN classe c ON e.classe_id = c.id
            LEFT JOIN niveau n ON c.niveau_id = n.id
            LEFT JOIN serie s ON c.serie_id = s.id
            LEFT JOIN groupe g ON c.groupe_id = g.id
            WHERE e.annee_id = ?
            ORDER BY n.nom, s.nom, g.nom
            """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, anneeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");

                String classeFull = rs.getString("niveau") + " "
                                  + rs.getString("serie") + " "
                                  + rs.getString("groupe");

                cbClasse.getItems().add(id + " - " + classeFull);
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

        try (Connection conn = Database.connect()) {

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
                    chargerGrille(classe, annee.getId());
                    clearForm();
                    new Alert(Alert.AlertType.INFORMATION, "Emploi ajouté !").show();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout !").show();
        }
    }
    @FXML
    private void modifierEmploi() {

        String jour = cbJour.getValue();
        String debut = cbHeureDebut.getValue();
        String fin = cbHeureFin.getValue();
        String matiere = cbMatiere.getValue();
        String prof = cbProfesseur.getValue();
        String classe = cbClasse.getValue();
        AnneeScolaires annee = cbAnnee.getValue();

        if (jour == null || debut == null || fin == null || matiere == null || prof == null || classe == null || annee == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        try (Connection conn = Database.connect()) {

            String sql = """
                UPDATE emploi_du_temps
                SET heure_fin = ?, matiere = ?, professeur = ?
                WHERE jour = ? AND heure_debut = ? AND classe = ? AND annee_id = ?
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, fin);
            ps.setString(2, matiere);
            ps.setString(3, prof);
            ps.setString(4, jour);
            ps.setString(5, debut);
            ps.setString(6, classe);
            ps.setInt(7, annee.getId());

            int n = ps.executeUpdate();

            if (n > 0) {
                showAlert("Succès", "Emploi modifié !");
                chargerGrille(classe, annee.getId());
                clearForm();
            } else {
                showAlert("Erreur", "Aucun enregistrement trouvé !");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Modification échouée !");
        }
    }
    @FXML
    private void supprimerEmploi() {

        String jour = cbJour.getValue();
        String debut = cbHeureDebut.getValue();
        String classe = cbClasse.getValue();
        AnneeScolaires annee = cbAnnee.getValue();

        if (jour == null || debut == null || classe == null || annee == null) {
            showAlert("Erreur", "Sélection incomplète !");
            return;
        }

        // 🔥 Confirmation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Voulez-vous supprimer cet emploi ?");
        
        if (confirm.showAndWait().get() != ButtonType.OK) return;

        try (Connection conn = Database.connect()) {

            String sql = """
                DELETE FROM emploi_du_temps
                WHERE jour = ? AND heure_debut = ? AND classe = ? AND annee_id = ?
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, jour);
            ps.setString(2, debut);
            ps.setString(3, classe);
            ps.setInt(4, annee.getId());

            int n = ps.executeUpdate();

            if (n > 0) {
                showAlert("Succès", "Emploi supprimé !");
                chargerGrille(classe, annee.getId());
                clearForm();
            } else {
                showAlert("Erreur", "Aucun emploi trouvé !");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Suppression échouée !");
        }
    }
  
    public void genererPdfEmploiDuTempsParClasse(String classe) {

        AnneeScolaires annee = cbAnnee.getValue();

        if (annee == null) {
            showAlert("Erreur", "Veuillez sélectionner l'année scolaire");
            return;
        }
       
          
        String dossier = System.getProperty("user.home") + "/Documents/";
        String fichier = dossier + "emploi_du_temps-" + classe + "-" 
                + annee.getLibelle().replace("/", "-") + "-" 
                + System.currentTimeMillis() + ".pdf";
      
     
        String sql = """
            SELECT jour, heure_debut, heure_fin, matiere, professeur
            FROM emploi_du_temps
            WHERE classe = ?
            AND annee_id = ?
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

            Connection conn = Database.connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, classe);
            ps.setInt(2, annee.getId());
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
    }
    
    // Optionnel : simple alert
    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
