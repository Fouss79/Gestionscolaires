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
    import java.time.LocalDate;

    public class EleveController1 {

        @FXML private TextField txtNom;
        @FXML private TextField txtPrenom;
        @FXML private DatePicker dpNaissance;
        @FXML private TextField txtAdresse;
        @FXML private TextField txtTelephone;
        @FXML private ComboBox<String> cbClasse;
        @FXML private ComboBox<String> cbAnneeScolaire;

       
    
        
        @FXML private Button btnClasse;
        @FXML private Button btnEleve;
        @FXML private Button btnMatiere;
        @FXML private Button btnNote;
        @FXML private Button btnRetour;
        @FXML private Button btnEnseignant;
        @FXML private Button btnEmargement;
        @FXML private Button btnClasse1;
        @FXML private Button btnComptabilite;
        @FXML private Button btnEmploidutemps;
        @FXML private Button btnTransaction;




        @FXML private TableView<Eleve> tableEleves;
        @FXML private TableColumn<Eleve, String> colId;
        @FXML private TableColumn<Eleve, String> colNom;
        @FXML private TableColumn<Eleve, String> colPrenom;
        @FXML private TableColumn<Eleve, String> colMat;
        @FXML private TableColumn<Eleve, String> colClasse;
        @FXML private TableColumn<Eleve, String> colSexe;
        @FXML private TableColumn<Eleve, String> colAdresse;
        @FXML private TableColumn<Eleve, LocalDate> colDateNaiss;
        @FXML
        private TableColumn<Eleve, String> colAnneeScolaire;


        private ObservableList<String> classes = FXCollections.observableArrayList();
        private ObservableList<Eleve> listeEleves = FXCollections.observableArrayList();

        // ================== MÉTHODES BDD ==================
        private Connection getConnection() throws SQLException {
            String url = "jdbc:mysql://localhost:3306/gestionEcole";
            String user = "root";
            String password = "";
            return DriverManager.getConnection(url, user, password);
        }

        
        // ================== MÉTHODES DE NAVIGATION ==================
        
        
        @FXML
        void actualiserListe(ActionEvent event) { }

        @FXML
        void openEmargement(ActionEvent event) { changerScene("EmargementView.fxml", "Les émargements"); }

        @FXML
        void openComptabilite(ActionEvent event) { changerScene("EnseignementView.fxml", "Gestion des Enseignements"); }
        @FXML
        void ajouterEleve(ActionEvent event) { changerScene("EleveView.fxml", "Ajout Élève"); }

    

        @FXML
        void openEmploidutemps(ActionEvent event) { changerScene("EmploiDutempsView.fxml", "Gestion des Emplois du temps"); }

        @FXML
        void openTransaction(ActionEvent event) { changerScene("PaiementEnseignant.fxml", "Gestion des Paiements"); }

        @FXML
        void openClasse(ActionEvent event) { changerScene("ClasseView.fxml", "Gestion des Classes"); }

        @FXML
        void openEnseignant(ActionEvent event) { changerScene("EnseignantView.fxml", "Gestion des Enseignants"); }

        @FXML
        void openEleve(ActionEvent event) { changerScene("EleveForm.fxml", "Gestion des Élèves"); }

        @FXML
        void openMatiere(ActionEvent event) { changerScene("Matiere.fxml", "Gestion des Matières"); }

        @FXML
        void openNote(ActionEvent event) { changerScene("NoteView.fxml", "Gestion des Notes"); }

      

       
        private void loadClasses() {
            classes.clear();
            String sql = "SELECT id, nom FROM classe";
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nom = rs.getString("nom");
                    classes.add(id + " - " + nom);
                }
                cbClasse.setItems(classes);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void chargerEleves() {
            listeEleves.clear();
            String sql = "SELECT e.nom, e.prenom, e.classe_nom, e.telephone, e.numero_matricule, " +
                         "e.sexe, e.date_naissance, e.adresse, e.statutId, e.annee_id, a.libelle AS anneeScolaire " +
                         "FROM eleve e " +
                         "LEFT JOIN anneescolaire a ON e.annee_id = a.id";

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Eleve eleve = extraireEleve(rs);
                    // Définir dynamiquement l'année scolaire depuis la BDD
                    eleve.setAnneeScolaire(rs.getString("anneeScolaire"));
                    listeEleves.add(eleve);
                }
                tableEleves.setItems(listeEleves);

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors du chargement des élèves !");
            }
        }
        private Eleve extraireEleve(ResultSet rs) throws SQLException {

            String matricule = rs.getString("numero_matricule");
            String nom = rs.getString("nom");
            String prenom = rs.getString("prenom");
            String sexe = rs.getString("sexe");

            Date sqlDate = rs.getDate("date_naissance");
            LocalDate dateNaiss = (sqlDate != null) ? sqlDate.toLocalDate() : null;

            String adresse = rs.getString("adresse");
            String classeNom = rs.getString("classe_nom");
            String telephone = rs.getString("telephone");
            String statutId = rs.getString("statutId");
            Long anneeId = rs.getLong("annee_id");

            Eleve eleve = new Eleve(nom, prenom, dateNaiss, adresse, telephone, classeNom, sexe, statutId, anneeId);
            eleve.setNumeroMatricule(matricule);

            // ✅ si la colonne existe dans la requête
            try {
                eleve.setAnneeScolaire(rs.getString("anneeScolaire"));
            } catch (SQLException ignored) {}

            return eleve;
        }


     
        // ================== FILTRAGE ==================
        private void filtrerElevesParClasse(String classeNom) {
            listeEleves.clear();
            String sql = "SELECT nom, prenom, classe_nom, telephone, numero_matricule, sexe, date_naissance, adresse, statutId, annee_id FROM eleve WHERE classe_nom = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, classeNom);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Eleve eleve = extraireEleve(rs);
                    listeEleves.add(eleve);
                }
                tableEleves.setItems(listeEleves);

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors du filtrage des élèves !");
            }
        }
         
        private void loadAnneesScolaires() {
            ObservableList<String> annees = FXCollections.observableArrayList("2025-2026", "2026-2027");
            cbAnneeScolaire.setItems(annees);
        }

        // ================== INITIALISATION ==================
        @FXML
        public void initialize() {
        	 loadAnneesScolaires(); 
            loadClasses();
            chargerEleves();
            colAnneeScolaire.setCellValueFactory(new PropertyValueFactory<>("anneeScolaire"));
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            colClasse.setCellValueFactory(new PropertyValueFactory<>("classeNom"));
            colMat.setCellValueFactory(new PropertyValueFactory<>("numeroMatricule"));
            colSexe.setCellValueFactory(new PropertyValueFactory<>("sexe"));
            colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
            colDateNaiss.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
            
            

            // Filtrage dynamique par classe
            cbClasse.setOnAction(e -> {
                String valeur = cbClasse.getValue();
                if (valeur != null && !valeur.isEmpty()) {
                    String[] parts = valeur.split(" - ");
                    if (parts.length > 1) {
                        String classeNom = parts[1].trim();
                        filtrerElevesParClasse(classeNom);
                    }
                } else {
                    chargerEleves();
                }
            });
            
            cbAnneeScolaire.setOnAction(e -> {
                String selectedAnnee = cbAnneeScolaire.getValue();
                if (selectedAnnee != null && !selectedAnnee.isEmpty()) {
                    filtrerElevesParAnnee(selectedAnnee);
                } else {
                    chargerEleves();
                }
            });


            // Initialisation ComboBox Sexe
      

            // Initialisation ComboBox Année
           
        }
        
        private void filtrerElevesParAnnee(String anneeScolaire) {
            listeEleves.clear();

            String sql =
                "SELECT e.nom, e.prenom, e.classe_nom, e.telephone, e.numero_matricule, " +
                "e.sexe, e.date_naissance, e.adresse, e.statutId, e.annee_id, a.libelle AS anneeScolaire " +
                "FROM eleve e " +
                "JOIN anneescolaire a ON e.annee_id = a.id " +
                "WHERE a.libelle = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, anneeScolaire);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Eleve eleve = extraireEleve(rs);
                    eleve.setAnneeScolaire(rs.getString("anneeScolaire"));
                    listeEleves.add(eleve);
                }

                tableEleves.setItems(listeEleves);

            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Erreur lors du filtrage par année scolaire !");
            }
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

        private void showAlert(String title, String msg) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        }

        // ================== MODIFIER / SUPPRIMER ==================
        @FXML
        void modifierEleve(ActionEvent event) {

            Eleve selected = tableEleves.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Attention", "Veuillez sélectionner un élève à modifier !");
                return;
            }

            // ----- NOM -----
            TextInputDialog dialogNom = new TextInputDialog(selected.getNom());
            dialogNom.setTitle("Modifier Élève");
            dialogNom.setHeaderText("Modifier l'élève sélectionné");
            dialogNom.setContentText("Nom :");
            String nouveauNom = dialogNom.showAndWait().orElse(null);
            if (nouveauNom == null || nouveauNom.isBlank()) return;

            // ----- PRENOM -----
            TextInputDialog dialogPrenom = new TextInputDialog(selected.getPrenom());
            dialogPrenom.setContentText("Prénom :");
            String nouveauPrenom = dialogPrenom.showAndWait().orElse(null);
            if (nouveauPrenom == null || nouveauPrenom.isBlank()) return;

            // ----- TELEPHONE -----
            TextInputDialog dialogTel = new TextInputDialog(selected.getTelephone());
            dialogTel.setContentText("Téléphone :");
            String nouveauTel = dialogTel.showAndWait().orElse(null);

            // ----- ADRESSE -----
            TextInputDialog dialogAdresse = new TextInputDialog(selected.getAdresse());
            dialogAdresse.setContentText("Adresse :");
            String nouvelleAdresse = dialogAdresse.showAndWait().orElse(null);

            // ----- SEXE -----
            ChoiceDialog<String> dialogSexe =
                    new ChoiceDialog<>(selected.getSexe(), "FIlle", "Garçon");
            dialogSexe.setTitle("Modifier Élève");
            dialogSexe.setHeaderText(null);
            dialogSexe.setContentText("Sexe :");
            String nouveauSexe = dialogSexe.showAndWait().orElse(selected.getSexe());

            // ----- CLASSE -----
            ChoiceDialog<String> dialogClasse =
                    new ChoiceDialog<>(selected.getClasseNom(), classes);
            dialogClasse.setTitle("Modifier Élève");
            dialogClasse.setHeaderText(null);
            dialogClasse.setContentText("Classe :");
            String nouvelleClasse = dialogClasse.showAndWait().orElse(selected.getClasseNom());

            String classeNom = nouvelleClasse;
            if (nouvelleClasse.contains(" - ")) {
                classeNom = nouvelleClasse.split(" - ")[1].trim();
            }

            // ----- DATE DE NAISSANCE -----
            DatePicker datePicker = new DatePicker(selected.getDateNaissance());
            Dialog<LocalDate> dialogDate = new Dialog<>();
            dialogDate.setTitle("Modifier Élève");
            dialogDate.setHeaderText("Date de naissance");
            dialogDate.getDialogPane().setContent(datePicker);
            dialogDate.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialogDate.setResultConverter(btn -> btn == ButtonType.OK ? datePicker.getValue() : null);
            LocalDate nouvelleDate = dialogDate.showAndWait().orElse(selected.getDateNaissance());

            // ====== UPDATE SQL ======
            String sql = """
                UPDATE eleve SET
                    nom = ?, prenom = ?, telephone = ?, adresse = ?,
                    sexe = ?, classe_nom = ?, date_naissance = ?
                WHERE numero_matricule = ?
                """;

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, nouveauNom);
                stmt.setString(2, nouveauPrenom);
                stmt.setString(3, nouveauTel);
                stmt.setString(4, nouvelleAdresse);
                stmt.setString(5, nouveauSexe);
                stmt.setString(6, classeNom);
                stmt.setDate(7, Date.valueOf(nouvelleDate));
                stmt.setString(8, selected.getNumeroMatricule());

                stmt.executeUpdate();

                // ====== MAJ TABLE ======
                selected.setNom(nouveauNom);
                selected.setPrenom(nouveauPrenom);
                selected.setTelephone(nouveauTel);
                selected.setAdresse(nouvelleAdresse);
                selected.setSexe(nouveauSexe);
                selected.setClasseNom(classeNom);
                selected.setDateNaissance(nouvelleDate);

                tableEleves.refresh();
                showAlert("Succès", "Élève modifié avec succès !");

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la modification !");
            }
        }

        @FXML
        void supprimerEleve(ActionEvent event) {
            Eleve selected = tableEleves.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Attention", "Veuillez sélectionner un élève à supprimer !");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cet élève ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                String sql = "DELETE FROM eleve WHERE numero_matricule = ?";
                try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, selected.getNumeroMatricule());
                    stmt.executeUpdate();
                    listeEleves.remove(selected);
                    tableEleves.refresh();
                    showAlert("Succès", "Élève supprimé avec succès !");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Erreur lors de la suppression !");
                }
            }
        }
    }








   




