package application;

import javafx.collections.FXCollections;


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;


import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class EleveControler {

    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private DatePicker dpNaissance;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtTelephone;
    @FXML private ComboBox<String> cbClasse;
    @FXML private ComboBox<String> cbSexe;
    @FXML private ComboBox<String> cbStatut;
    @FXML private ComboBox<String> cbClasseFiltre;
    @FXML private ComboBox<String> cbAnneeFiltre;
    
    @FXML private Label lblStats;

    @FXML private TableView<Eleve> tableEleves;
    @FXML private TableColumn<Eleve, String> colMatricule;
    @FXML private TableColumn<Eleve, String> colNom;
    @FXML private TableColumn<Eleve, String> colPrenom;
    @FXML private TableColumn<Eleve, String> colClasse;
    @FXML private TableColumn<Eleve, String> colAnnee;
    @FXML private TableColumn<Eleve, String> colStatut;

    private ObservableList<Eleve> listeEleves = FXCollections.observableArrayList();
    private ObservableList<String> classes = FXCollections.observableArrayList();
    private ObservableList<String> statuts = FXCollections.observableArrayList();
    private Long eleveEnCoursId = null;


    

    // ================== Initialisation ==================
    @FXML
    public void initialize() {
    	tableEleves.setRowFactory(tv -> new TableRow<>() {
    	    @Override
    	    protected void updateItem(Eleve e, boolean empty) {
    	        super.updateItem(e, empty);

    	        if (e == null || empty) {
    	            setStyle("");
    	        } else if ("INSCRIT".equals(e.getStatutId())) {
    	            setStyle("-fx-background-color:#d4edda;");
    	        } else {
    	            setStyle("-fx-background-color:#fff3cd;");
    	        }
    	    }
    	});

        cbSexe.setItems(FXCollections.observableArrayList("Fille", "Garçon"));
        cbSexe.getSelectionModel().select("Garçon");

        loadStatuts();
        loadClasses();
        chargerInscriptions();
        TableColumn<Eleve, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(160);

        colActions.setCellFactory(param -> new TableCell<>() {

            private final Button btnEdit = new Button("✏");
            private final Button btnDelete = new Button("🗑");
            private final AnchorPane pane = new AnchorPane();

            {
                btnEdit.setStyle("-fx-background-color:#3498db; -fx-text-fill:white;");
                btnDelete.setStyle("-fx-background-color:#e74c3c; -fx-text-fill:white;");

                btnEdit.setLayoutX(10);
                btnEdit.setLayoutY(2);

                btnDelete.setLayoutX(80);
                btnDelete.setLayoutY(2);

                btnEdit.setOnAction(e -> {
                    Eleve eleve = getTableView().getItems().get(getIndex());
                    remplirFormulaire(eleve);
                });

                btnDelete.setOnAction(e -> {
                    Eleve eleve = getTableView().getItems().get(getIndex());
                    supprimerEleve(eleve);
                });

                pane.getChildren().addAll(btnEdit, btnDelete);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        if (tableEleves.getColumns().stream().noneMatch(c -> "Actions".equals(c.getText()))) {
            tableEleves.getColumns().add(colActions);
        }


        colMatricule.setCellValueFactory(new PropertyValueFactory<>("numeroMatricule"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colClasse.setCellValueFactory(new PropertyValueFactory<>("classeNom"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("anneeScolaire"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statutId"));
        

        // 🔹 Charger filtres
        loadClasseFiltre();
        loadAnneeFiltre();
    
    }
    // ================== OUTILS ==================
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
    @FXML
    private void retourAccueil() {
    	changerScene("DashboardView.fxml", "Tableau de bord");
    }

    
    @FXML
    void openEmargement(ActionEvent event) {
    	changerScene("EmargementView.fxml", "Gestion des Emargements");

    }
    
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
    void openHabilitation(ActionEvent event) {
    	 changerScene("HabilitationView.fxml", "Habilitation");
    }

  

    
    private void remplirFormulaire(Eleve e) {
    	
    	eleveEnCoursId = e.getId();

        txtNom.setText(e.getNom());
        txtPrenom.setText(e.getPrenom());
        dpNaissance.setValue(e.getDateNaissance());
        txtAdresse.setText(e.getAdresse());
        txtTelephone.setText(e.getTelephone());
        cbSexe.setValue(e.getSexe());
        cbStatut.setValue(e.getStatutId());

        // classe (si format "id - nom")
        for (String c : cbClasse.getItems()) {
            if (c.contains(e.getClasseNom())) {
                cbClasse.setValue(c);
                break;
            }
        }
        
        
    }
    
    @FXML
    private void telechargerModele() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le modèle Excel");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier Excel", "*.xlsx")
        );

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Eleves");

                // En-têtes
                Row header = sheet.createRow(0);
                String[] headers = {"Nom", "Prenom", "Telephone", "Adresse", "Sexe", "Classe", "Statut"};
                for (int i = 0; i < headers.length; i++) {
                    header.createCell(i).setCellValue(headers[i]);
                }

                // Exemple de ligne (optionnel)
                Row exemple = sheet.createRow(1);
                exemple.createCell(0).setCellValue("Doe");
                exemple.createCell(1).setCellValue("John");
                exemple.createCell(2).setCellValue("77xxxxxxx");
                exemple.createCell(3).setCellValue("Bamako");
                exemple.createCell(4).setCellValue("Garçon");
                exemple.createCell(5).setCellValue("2"); // id-classe ou nom
                exemple.createCell(6).setCellValue("PREINSCRIT");

                // Ajuster largeur
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Sauvegarde
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                    showAlert("Succès", "Modèle Excel créé !");
                }

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de créer le modèle Excel !");
            }
        }
    }


    @FXML
    private void importerFichier() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier Excel", "*.xlsx")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = new XSSFWorkbook(fis);
                 Connection conn = Database.connect()) {

                Sheet sheet = workbook.getSheetAt(0);
                conn.setAutoCommit(false); // transaction

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    String nom = row.getCell(0).getStringCellValue();
                    String prenom = row.getCell(1).getStringCellValue();
                    String telephone = row.getCell(2).getStringCellValue();
                    String adresse = row.getCell(3).getStringCellValue();
                    String sexe = row.getCell(4).getStringCellValue();
                    String classeItem = row.getCell(5).getStringCellValue();
                    String statut = row.getCell(6).getStringCellValue();

                    if (nom.isEmpty() || prenom.isEmpty() || classeItem.isEmpty()) continue;

                    int classeId = Integer.parseInt(classeItem.split(" - ")[0]);
                    long anneeId = getAnneeActive();

                    // 🔹 Utiliser la classe Eleve pour générer le matricule
                    Eleve eleve = new Eleve(nom, prenom, LocalDate.now(), adresse, telephone,
                                            classeItem, sexe, statut, anneeId);
                    String numeroMatricule = eleve.getNumeroMatricule();

                    // ===== INSERT ELEVE =====
                    String sqlEleve = """
                        INSERT INTO eleve (nom, prenom, date_naissance, adresse, telephone, sexe, numero_matricule)
                        VALUES (?, ?, DATE('now'), ?, ?, ?, ?)
                    """;

                    long eleveId;
                    try (PreparedStatement ps = conn.prepareStatement(sqlEleve, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, nom);
                        ps.setString(2, prenom);
                        ps.setString(3, adresse);
                        ps.setString(4, telephone);
                        ps.setString(5, sexe);
                        ps.setString(6, numeroMatricule);
                        ps.executeUpdate();

                        ResultSet rs = ps.getGeneratedKeys();
                        rs.next();
                        eleveId = rs.getLong(1);
                    }

                    // ===== INSERT INSCRIPTION =====
                    String sqlInscription = """
                        INSERT INTO inscription (eleve_id, classe_id, annee_id, date_inscription, statut)
                        VALUES (?, ?, ?, DATE('now'), ?)
                    """;

                    try (PreparedStatement ps2 = conn.prepareStatement(sqlInscription)) {
                        ps2.setLong(1, eleveId);
                        ps2.setInt(2, classeId);
                        ps2.setLong(3, anneeId);
                        ps2.setString(4, statut);
                        ps2.executeUpdate();
                    }
                }

                conn.commit();

                chargerInscriptions(); // mettre à jour la table
                showAlert("Succès", "Importation et inscription réussies !");

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de l'importation !");
            }
        }
    }


    @FXML
    private void modifierEleve() {

        if (eleveEnCoursId == null) {
            showAlert("Info", "Sélectionnez d'abord un élève à modifier");
            return;
        }

        String sql = """
            UPDATE eleve
            SET nom=?, prenom=?, date_naissance=?, adresse=?, telephone=?, sexe=?
            WHERE id=?
        """;

        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, txtNom.getText());
            ps.setString(2, txtPrenom.getText());
            ps.setDate(3, Date.valueOf(dpNaissance.getValue()));
            ps.setString(4, txtAdresse.getText());
            ps.setString(5, txtTelephone.getText());
            ps.setString(6, cbSexe.getValue());
            ps.setLong(7, eleveEnCoursId);

            ps.executeUpdate();

            eleveEnCoursId = null;
            chargerInscriptions();
            clearForm();

            showAlert("Succès", "Élève modifié avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Modification échouée");
        }
    }

    private void supprimerEleve(Eleve e) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer cet élève ?");

        if (confirm.showAndWait().get() != ButtonType.OK) return;

        String sql1 = "DELETE FROM inscription WHERE eleve_id=?";
        String sql2 = "DELETE FROM eleve WHERE id=?";

        try (Connection c = Database.connect()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps1 = c.prepareStatement(sql1);
                 PreparedStatement ps2 = c.prepareStatement(sql2)) {

                ps1.setLong(1, e.getId());
                ps1.executeUpdate();

                ps2.setLong(1, e.getId());
                ps2.executeUpdate();

                c.commit();
            }

            chargerInscriptions();

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Erreur", "Suppression impossible");
        }
    }


    // ================== Charger les classes ==================
    private void loadClasses() {
        classes.clear();
        String sql = """
            SELECT 
                c.id, n.nom AS niveau, s.nom AS serie, g.nom AS groupe
            FROM classe c
            LEFT JOIN niveau n ON c.niveau_id = n.id
            LEFT JOIN serie s ON c.serie_id = s.id
            LEFT JOIN groupe g ON c.groupe_id = g.id
        """;

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String display = id + " - " 
                                 + rs.getString("niveau") + " " 
                                 + rs.getString("serie") + " " 
                                 + rs.getString("groupe");
                classes.add(display);
            }
            cbClasse.setItems(classes);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // ================== Charger les statuts ==================
    private void loadStatuts() {
        statuts.clear();
        statuts.addAll("PREINSCRIT", "INSCRIT");
        cbStatut.setItems(statuts);
        cbStatut.getSelectionModel().select("PREINSCRIT");
    }

    // ================== Charger toutes les inscriptions ==================
    private void chargerInscriptions() {
        listeEleves.clear();

        String sql = """
            SELECT 
                e.id, e.nom, e.prenom, e.sexe, e.date_naissance,
                e.adresse, e.telephone, e.numero_matricule,
                n.nom AS niveau, s.nom AS serie, g.nom AS groupe,
                a.libelle AS anneeScolaire, i.statut
            FROM inscription i
            JOIN eleve e ON i.eleve_id = e.id
            JOIN classe c ON i.classe_id = c.id
            LEFT JOIN niveau n ON c.niveau_id = n.id
            LEFT JOIN serie s ON c.serie_id = s.id
            LEFT JOIN groupe g ON c.groupe_id = g.id
            JOIN anneescolaire a ON i.annee_id = a.id
            WHERE a.active = 1
        """;

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Eleve eleve = new Eleve();
                eleve.setId(rs.getLong("id"));
                eleve.setNom(rs.getString("nom"));
                eleve.setPrenom(rs.getString("prenom"));

                long timestamp = rs.getLong("date_naissance");
                LocalDate dateNaissance = Instant.ofEpochMilli(timestamp)
                                                 .atZone(ZoneId.systemDefault())
                                                 .toLocalDate();
                eleve.setDateNaissance(dateNaissance);

                eleve.setAdresse(rs.getString("adresse"));
                eleve.setTelephone(rs.getString("telephone"));
                eleve.setSexe(rs.getString("sexe"));
                eleve.setNumeroMatricule(rs.getString("numero_matricule"));

                // Affichage niveau + série + groupe
                String classeFull = rs.getString("niveau") + " " 
                                  + rs.getString("serie") + " " 
                                  + rs.getString("groupe");
                eleve.setClasseNom(classeFull);

                eleve.setAnneeScolaire(rs.getString("anneeScolaire"));
                eleve.setStatutId(rs.getString("statut"));

                listeEleves.add(eleve);
            }

            tableEleves.setItems(listeEleves);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les inscriptions !");
        }
    }
    // ================== Année scolaire active ==================
    private long getAnneeActive() {
        String sql = "SELECT id FROM anneescolaire WHERE active = 1 LIMIT 1";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getLong("id");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
    
    
    // ================== LOAD FILTRES ==================
    private void loadClasseFiltre() {
        ObservableList<String> list = FXCollections.observableArrayList();
        classes.clear();
        String sql = """
            SELECT 
                c.id, n.nom AS niveau, s.nom AS serie, g.nom AS groupe
            FROM classe c
            LEFT JOIN niveau n ON c.niveau_id = n.id
            LEFT JOIN serie s ON c.serie_id = s.id
            LEFT JOIN groupe g ON c.groupe_id = g.id
        """;
        try (Connection c = Database.connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
        	   while (rs.next()) {
                   int id = rs.getInt("id");
                   String display = id + " - " 
                                    + rs.getString("niveau") + " " 
                                    + rs.getString("serie") + " " 
                                    + rs.getString("groupe");
                   classes.add(display);
               }
               
 cbClasseFiltre.setItems(classes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAnneeFiltre() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT id, libelle FROM anneescolaire";

        try (Connection c = Database.connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(rs.getInt("id") + " - " + rs.getString("libelle"));
            }
            cbAnneeFiltre.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    
    @FXML
    private void chargerInscription() {

        if (cbClasseFiltre.getValue() == null || cbAnneeFiltre.getValue() == null) {
            showAlert("Erreur", "Choisissez classe et année !");
            return;
        }

        int classeId = Integer.parseInt(cbClasseFiltre.getValue().split(" - ")[0]);
        int anneeId  = Integer.parseInt(cbAnneeFiltre.getValue().split(" - ")[0]);

        listeEleves.clear();

        String sql = """
            SELECT 
                e.id,
                e.nom, e.prenom, e.sexe, e.date_naissance,
                e.adresse, e.telephone,
                e.numero_matricule,
                n.nom AS niveau, s.nom AS serie, g.nom AS groupe,
                a.libelle AS anneeScolaire,
                i.statut
            FROM inscription i
            JOIN eleve e ON i.eleve_id = e.id
            JOIN classe c ON i.classe_id = c.id
            LEFT JOIN niveau n ON c.niveau_id = n.id
            LEFT JOIN serie s ON c.serie_id = s.id
            LEFT JOIN groupe g ON c.groupe_id = g.id
            JOIN anneescolaire a ON i.annee_id = a.id
            WHERE i.classe_id = ? AND i.annee_id = ?
        """;

        int garcons = 0, filles = 0;

        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, classeId);
            ps.setInt(2, anneeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String sexe = rs.getString("sexe");
                if ("Garçon".equalsIgnoreCase(sexe)) garcons++;
                else filles++;

                Eleve eleve = new Eleve();
                eleve.setId(rs.getLong("id"));
                eleve.setNom(rs.getString("nom"));
                eleve.setPrenom(rs.getString("prenom"));

                // 🔹 Gestion sûre de la date de naissance
                long timestamp = rs.getLong("date_naissance");
                if (!rs.wasNull()) {
                    LocalDate ld = Instant.ofEpochMilli(timestamp)
                                          .atZone(ZoneId.systemDefault())
                                          .toLocalDate();
                    eleve.setDateNaissance(ld);
                } else {
                    eleve.setDateNaissance(null); // pas de date renseignée
                }

                eleve.setAdresse(rs.getString("adresse"));
                eleve.setTelephone(rs.getString("telephone"));
                eleve.setSexe(sexe);
                eleve.setNumeroMatricule(rs.getString("numero_matricule"));

                // 🔹 Niveau + Série + Groupe combinés
                String classeFull = rs.getString("niveau") + " "
                                  + rs.getString("serie") + " "
                                  + rs.getString("groupe");
                eleve.setClasseNom(classeFull);

                eleve.setAnneeScolaire(rs.getString("anneeScolaire"));
                eleve.setStatutId(rs.getString("statut"));

                listeEleves.add(eleve);
            }

            tableEleves.setItems(listeEleves);
            lblStats.setText("Effectif: " + listeEleves.size() +
                    " | Garçons: " + garcons +
                    " | Filles: " + filles);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur chargement inscriptions");
        }
    }

    // ================== Ajouter une préinscription ==================
    @FXML
    private void ajouterEleve() {
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        LocalDate dateNaissance = dpNaissance.getValue();
        String adresse = txtAdresse.getText().trim();
        String telephone = txtTelephone.getText().trim();
        String classeItem = cbClasse.getValue();
        String sexeItem = cbSexe.getValue();
        String statutItem = cbStatut.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null
                || classeItem == null || sexeItem == null || statutItem == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        // Extraire l'ID de la classe depuis le ComboBox
        int classeId = Integer.parseInt(classeItem.split(" - ")[0]);
        long anneeId = getAnneeActive();

        Eleve eleve = new Eleve(nom, prenom, dateNaissance, adresse, telephone,
                                classeItem, sexeItem, statutItem, anneeId);
        String matricule = eleve.getNumeroMatricule();

        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false);

            String sqlEleve = """
                INSERT INTO eleve (nom, prenom, date_naissance, adresse, telephone, sexe, numero_matricule)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

            long eleveId;
            try (PreparedStatement ps = conn.prepareStatement(sqlEleve, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nom);
                ps.setString(2, prenom);
                ps.setDate(3, Date.valueOf(dateNaissance));
                ps.setString(4, adresse);
                ps.setString(5, telephone);
                ps.setString(6, sexeItem);
                ps.setString(7, matricule);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                eleveId = rs.getLong(1);
            }

            // Inscription
            String sqlInscription = """
                INSERT INTO inscription (eleve_id, classe_id, annee_id, date_inscription, statut)
                VALUES (?, ?, ?, DATE('now'), ?)
            """;

            try (PreparedStatement ps2 = conn.prepareStatement(sqlInscription)) {
                ps2.setLong(1, eleveId);
                ps2.setInt(2, classeId);
                ps2.setLong(3, anneeId);
                ps2.setString(4, statutItem);
                ps2.executeUpdate();
            }

            conn.commit();
            chargerInscriptions();
            clearForm();
            showAlert("Succès", "Préinscription ajoutée avec succès !");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'enregistrement !");
        }
    }
    @FXML
    private void validerInscription() {
        Eleve selected = tableEleves.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner une préinscription !");
            return;
        }

        String sql = """
            UPDATE inscription
            SET statut = 'INSCRIT'
            WHERE eleve_id = (SELECT id FROM eleve WHERE numero_matricule = ?)
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, selected.getNumeroMatricule());
            int updatedRows = ps.executeUpdate();

            if (updatedRows > 0) {
                chargerInscriptions();
                showAlert("Succès", "Inscription validée !");
            } else {
                showAlert("Info", "Aucune inscription trouvée pour ce numéro matricule !");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de valider l'inscription !");
        }
    }

    // ================== Utils ==================
    private void clearForm() {
        txtNom.clear();
        txtPrenom.clear();
        dpNaissance.setValue(null);
        txtAdresse.clear();
        txtTelephone.clear();
        cbClasse.setValue(null);
        cbSexe.setValue("Garçon");
        cbStatut.setValue("PREINSCRIT");
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
