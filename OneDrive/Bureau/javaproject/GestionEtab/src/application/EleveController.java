 package application;

            import javafx.collections.FXCollections;
            import javafx.collections.ObservableList;
            import javafx.event.ActionEvent;
            import javafx.fxml.FXML;
            import javafx.scene.control.*;
            import javafx.scene.control.Alert.AlertType;

            import java.sql.*;
            import java.time.LocalDate;

            public class EleveController {

                @FXML private TextField txtNom;
                @FXML private TextField txtPrenom;
                @FXML private DatePicker dpNaissance;
                @FXML private TextField txtAdresse;
                @FXML private TextField txtTelephone;
                @FXML private ComboBox<String> cbClasse;
                @FXML private ComboBox<String> cbSexe;
                @FXML private ComboBox<String> cbStatut;
               

                @FXML private TableView<Eleve> tableEleves;
                @FXML private TableColumn<Eleve, String> colMatricule;
                @FXML private TableColumn<Eleve, String> colNom;
                @FXML private TableColumn<Eleve, String> colPrenom;
                @FXML private TableColumn<Eleve, String> colClasse;
                @FXML private TableColumn<Eleve, String> colAnnee;

                private ObservableList<Eleve> listeEleves = FXCollections.observableArrayList();
                private ObservableList<String> classes = FXCollections.observableArrayList();
                private ObservableList<String> statuts = FXCollections.observableArrayList();
              

                // ================== Connexion BDD ==================
                private Connection getConnection() throws SQLException {
                    String url = "jdbc:mysql://localhost:3306/gestionEcole";
                    String user = "root";
                    String password = "";
                    return DriverManager.getConnection(url, user, password);
                }

                // ================== Initialisation ==================
                @FXML
                public void initialize() {
                    cbSexe.setItems(FXCollections.observableArrayList("Fille", "Garçon"));
                    cbSexe.getSelectionModel().select("Garçon");

                   

                    
                    loadStatuts();
                    loadClasses();
                  
                }

                // ================== Charger les classes ==================
                private void loadClasses() {
                    classes.clear();
                    try (Connection conn = getConnection();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT id, nom FROM classe")) {

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

                // ================== Charger les statuts ==================
                private void loadStatuts() {
                    statuts.clear();
                    try (Connection conn = getConnection();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT id, nom FROM statut")) {

                        while (rs.next()) {
                            int id = rs.getInt("id");
                            String nom = rs.getString("nom");
                            statuts.add(id + " - " + nom);
                        }
                        cbStatut.setItems(statuts);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                // ================== Charger tous les élèves ==================
                private void chargerEleves() {
                    listeEleves.clear();
                    String sql = "SELECT * FROM eleve";

                    try (Connection conn = getConnection();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {

                        while (rs.next()) {
                            Eleve eleve = new Eleve(
                                    rs.getString("nom"),
                                    rs.getString("prenom"),
                                    rs.getDate("date_naissance") != null ? rs.getDate("date_naissance").toLocalDate() : null,
                                    rs.getString("adresse"),
                                    rs.getString("telephone"),
                                    rs.getString("classe_nom"),
                                    rs.getString("sexe"),
                                    rs.getString("statutId"),
                                    rs.getLong("anneeId")
                            );
                            eleve.setNumeroMatricule(rs.getString("numero_matricule"));
                            listeEleves.add(eleve);
                        }

                        tableEleves.setItems(listeEleves);

                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Erreur", "Impossible de charger les élèves !");
                    }
                }
                
             // 🔹 Récupérer l'année scolaire active
                private long getAnneeActive() {

                    String sql = "SELECT id FROM anneescolaire WHERE active = 1 LIMIT 1";

                    try (Connection conn = getConnection();
                         PreparedStatement ps = conn.prepareStatement(sql);
                         ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            return rs.getLong("id");
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    return 1; // valeur par défaut
                }

                
                // ================== Ajouter un élève ==================
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

                    int classeId = Integer.parseInt(classeItem.split(" - ")[0].trim());
                    String classeNom = classeItem.split(" - ")[1].trim();
                    long anneeId = getAnneeActive();   // ✅ année scolaire active
                    String statutId = statutItem.split(" - ")[0].trim();

                    Eleve eleve = new Eleve(
                            nom, prenom, dateNaissance, adresse, telephone,
                            classeNom, sexeItem, statutId, anneeId
                    );

                    // ✅ REQUÊTE SQL MANQUANTE (corrigée)
                    String sql = """
                        INSERT INTO eleve
                        (nom, prenom, date_naissance, adresse, telephone,
                         classe_nom, classe_id, numero_matricule, sexe, statutId, annee_id)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

                    try (Connection conn = getConnection();
                         PreparedStatement ps = conn.prepareStatement(sql)) {

                        ps.setString(1, eleve.getNom());
                        ps.setString(2, eleve.getPrenom());
                        ps.setDate(3, Date.valueOf(eleve.getDateNaissance()));
                        ps.setString(4, eleve.getAdresse());
                        ps.setString(5, eleve.getTelephone());
                        ps.setString(6, classeNom);
                        ps.setInt(7, classeId);
                        ps.setString(8, eleve.getNumeroMatricule());
                        ps.setString(9, eleve.getSexe());
                        ps.setString(10, eleve.getStatutId());
                        ps.setLong(11, eleve.getAnneeId());

                        ps.executeUpdate();

                        listeEleves.add(eleve);
                      
                        clearForm();
                        // ✅ Recalculer les stats de la classe
                        // ✅ Recalculer les stats de la classe pour une année scolaire donnée
                        String updateStatsSql = """
                            UPDATE classe
                            SET 
                                effectif = (
                                    SELECT COUNT(*) FROM eleve 
                                    WHERE classe_id = ? AND annee_id = ?
                                ),
                                nb_garçons = (
                                    SELECT COUNT(*) FROM eleve 
                                    WHERE classe_id = ? AND annee_id = ? AND sexe = 'garçon'
                                ),
                                nb_filles = (
                                    SELECT COUNT(*) FROM eleve 
                                    WHERE classe_id = ? AND annee_id = ? AND sexe = 'Fille'
                                )
                            WHERE id = ?
                        """;

                        try (PreparedStatement psStats = conn.prepareStatement(updateStatsSql)) {

                            psStats.setInt(1, classeId);
                            psStats.setLong(2, anneeId);

                            psStats.setInt(3, classeId);
                            psStats.setLong(4, anneeId);

                            psStats.setInt(5, classeId);
                            psStats.setLong(6, anneeId);

                            psStats.setInt(7, classeId);

                            psStats.executeUpdate();
                        }


                        showAlert("Succès", "Élève ajouté avec succès !");

                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Erreur", "Impossible d'ajouter l'élève !");
                    }
                }

                // ================== Filtrer par année scolaire ==================
                private void filtrerElevesParAnnee(String anneeScolaire) {
                    if (anneeScolaire == null) return;

                    ObservableList<Eleve> filtres = FXCollections.observableArrayList();
                    for (Eleve e : listeEleves) {
                        if (anneeScolaire.equals(e.getAnneeScolaire())) {
                            filtres.add(e);
                        }
                    }
                    tableEleves.setItems(filtres);
                }

                private void clearForm() {
                    txtNom.clear();
                    txtPrenom.clear();
                    dpNaissance.setValue(null);
                    txtAdresse.clear();
                    txtTelephone.clear();
                    cbClasse.setValue(null);
                    cbSexe.setValue(null);
                    cbStatut.setValue(null);
                   
                }

                private void showAlert(String title, String msg) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle(title);
                    alert.setHeaderText(null);
                    alert.setContentText(msg);
                    alert.showAndWait();
                }
            }
