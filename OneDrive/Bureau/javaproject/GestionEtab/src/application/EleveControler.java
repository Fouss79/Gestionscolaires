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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

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


    // ================== Connexion BDD ==================
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/gestionecole?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Accueil.fxml")); // mets le chemin exact
            Parent root = loader.load();
            Stage stage = (Stage) cbClasse.getScene().getWindow();
            stage.setScene(new Scene(root, 1024, 600));
            stage.setTitle("Accueil");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à l'accueil !");
        }
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

        try (Connection c = getConnection();
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

        try (Connection c = getConnection()) {
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
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nom FROM classe")) {

            while (rs.next()) {
                classes.add(rs.getInt("id") + " - " + rs.getString("nom"));
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
                e.id,
                e.nom, e.prenom, e.sexe, e.date_naissance,
                e.adresse, e.telephone,
                e.numero_matricule,
                c.nom AS classeNom,
                a.libelle AS anneeScolaire,
                i.statut
            FROM inscription i
            JOIN eleve e ON i.eleve_id = e.id
            JOIN classe c ON i.classe_id = c.id
            JOIN anneescolaire a ON i.annee_id = a.id
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Eleve eleve = new Eleve();
                eleve.setId(rs.getLong("id"));
                eleve.setNom(rs.getString("nom"));
                eleve.setPrenom(rs.getString("prenom"));
                Date d = rs.getDate("date_naissance");
                if (d != null) eleve.setDateNaissance(d.toLocalDate());

                eleve.setAdresse(rs.getString("adresse"));
                eleve.setTelephone(rs.getString("telephone"));
                eleve.setSexe(rs.getString("sexe"));
                eleve.setNumeroMatricule(rs.getString("numero_matricule"));
                eleve.setClasseNom(rs.getString("classeNom"));
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
        try (Connection conn = getConnection();
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
        String sql = "SELECT id, nom FROM classe";

        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(rs.getInt("id") + " - " + rs.getString("nom"));
            }
            cbClasseFiltre.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAnneeFiltre() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT id, libelle FROM anneescolaire";

        try (Connection c = getConnection();
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
                c.nom AS classeNom,
                a.libelle AS anneeScolaire,
                i.statut
            FROM inscription i
            JOIN eleve e ON i.eleve_id = e.id
            JOIN classe c ON i.classe_id = c.id
            JOIN anneescolaire a ON i.annee_id = a.id WHERE i.classe_id = ? AND i.annee_id = ?
        	""";

                listeEleves.clear();
        	int garcons = 0, filles = 0;

        	try (Connection c = getConnection();
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
                    Date d = rs.getDate("date_naissance");
                    if (d != null) eleve.setDateNaissance(d.toLocalDate());

                    eleve.setAdresse(rs.getString("adresse"));
                    eleve.setTelephone(rs.getString("telephone"));
                    eleve.setSexe(rs.getString("sexe"));
                    eleve.setNumeroMatricule(rs.getString("numero_matricule"));
                    eleve.setClasseNom(rs.getString("classeNom"));
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

        int classeId = Integer.parseInt(classeItem.split(" - ")[0]);
        long anneeId = getAnneeActive();

        // 🔹 Génération du matricule
        Eleve eleve = new Eleve(nom, prenom, dateNaissance, adresse, telephone,
                cbClasse.getValue(), sexeItem, statutItem, anneeId);
        String matricule = eleve.getNumeroMatricule();

        try (Connection conn = getConnection()) {

            conn.setAutoCommit(false);

            // ===== INSERT ELEVE avec matricule =====
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
                ps.setString(7, matricule); // ✅ matricule stocké

                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                eleveId = rs.getLong(1);
            }

            // ===== INSERT INSCRIPTION =====
            String sqlInscription = """
                INSERT INTO inscription (eleve_id, classe_id, annee_id, date_inscription, statut)
                VALUES (?, ?, ?, CURDATE(), ?)
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

    // ================== Valider une préinscription ==================
    @FXML
    private void validerInscription() {
        Eleve selected = tableEleves.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner une préinscription !");
            return;
        }

        String sql = """
            UPDATE inscription i
            JOIN eleve e ON i.eleve_id = e.id
            SET i.statut = 'INSCRIT'
            WHERE e.numero_matricule = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, selected.getNumeroMatricule());
            ps.executeUpdate();

            chargerInscriptions();
            showAlert("Succès", "Inscription validée !");
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
