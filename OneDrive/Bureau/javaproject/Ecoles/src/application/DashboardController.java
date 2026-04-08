package application;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class DashboardController {

    // JDBC
  
    @FXML private Label lblUtilisateur;
    @FXML private Label lblTotalEleves;
    @FXML private Label lblTotalEnseignants;
    @FXML private Label lblTotalClasses;
    @FXML private Label lblTotalMatieres;
    @FXML private Label lblTotalCours;
    @FXML private Label lblHeuresMois;
    @FXML
    private Label lblAnneeScolaire;
    
    @FXML private Button menuUtilisateur;
    @FXML private Button menuEleve;
    @FXML private Button menuNote;
    private Utilisateur user;
    


    // ===== TABLE RECENTS =====
    @FXML private TableView<RecentEvent> tableRecents;

    // ⚠️ PAS DE colEleve (car pas dans le FXML)
    @FXML private TableColumn<RecentEvent, String> colClasse;
    @FXML private TableColumn<RecentEvent, String> colEnseignant;
    @FXML private TableColumn<RecentEvent, String> colMatiere;
    @FXML private TableColumn<RecentEvent, LocalDate> colDate;
    @FXML private BarChart<String, Number> barHeuresProf;
    @FXML private CategoryAxis xAxisProf;
    @FXML private NumberAxis yAxisProf;
    @FXML
    private BarChart<String, Number> barChartProf;
    @FXML
    private BarChart<String, Number> barChartClasse;




    private ObservableList<RecentEvent> recents = FXCollections.observableArrayList();

    // ================= INITIALIZE =================
    @FXML
    public void initialize() {

        // 🔹 Afficher utilisateur connecté
        Utilisateur user = ContexteApplication.getUtilisateurConnecte();

        if (user != null) {
            lblUtilisateur.setText("Bienvenue " + user.getUsername());
        } else {
            lblUtilisateur.setText("Utilisateur inconnu");
        }

        // 🔹 Afficher année active
        String annee = ContexteApplication.getAnneeScolaire();

        if (annee != null) {
            lblAnneeScolaire.setText("Année : " + annee);
        } else {
            lblAnneeScolaire.setText("Aucune année active");
        }

        loadStats();
        loadDiagrammeHeuresParProf();
        loadDiagrammeHeuresParClasse();
    } 
    private void loadDiagrammeHeuresParProf() {

        String sql = """
            SELECT e.enseignant, SUM(e.duree) AS total
            FROM emargement e JOIN anneescolaire a ON e.annee_id = a.id
            WHERE a.active = 1
            GROUP BY enseignant
        """;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Heures par professeur");

        try (Connection conn = Database.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String prof = rs.getString("enseignant");
                int minutes = rs.getInt("total");
                double heures = minutes / 60.0;

                series.getData().add(new XYChart.Data<>(prof, heures));
            }

            barChartProf.getData().clear();
            barChartProf.getData().add(series);
           
            // 🎨 couleur après affichage
            for (XYChart.Data<String, Number> data : series.getData()) {
                data.getNode().setStyle("-fx-bar-fill: #3498db;"); // bleu
            }




        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur diagramme prof : " + e.getMessage());
        }
    }

    
       private void loadDiagrammeHeuresParClasse() {

        barChartClasse.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Heures par classe");

        String sql = "SELECT classe, SUM(duree) AS total FROM emargement e JOIN anneescolaire a ON e.annee_id = a.id\r\n"
        		+ "            WHERE a.active = 1 GROUP BY classe";

        try (Connection conn = Database.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(
                        rs.getString("classe"),
                        rs.getInt("total")
                ));
            }

            barChartClasse.getData().add(series);
            
            String[] colors = {"#1abc9c", "#3498db", "#9b59b6", "#e67e22", "#e74c3c"};

            int i = 0;
            for (XYChart.Data<String, Number> data : series.getData()) {
                String color = colors[i % colors.length];
                data.getNode().setStyle("-fx-bar-fill: " + color + ";");
                i++;
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
        
 // 🔹 Navigation entre les pages
    @FXML
    void openEmargement(ActionEvent event) {
    	changerScene("EmargementView.fxml", "Gestion des Emargements",event);

    }
    @FXML private void retourAccueil(ActionEvent event) {changerScene("DashboardView.fxml", "Accueil",event);  }
    @FXML private void openClasse(ActionEvent event) { changerScene("ClasseView.fxml", "Gestion des Classes",event); }
    @FXML private void openEleve(ActionEvent event) { changerScene("ElevesView.fxml", "Gestion des Élèves",event); }
    @FXML private void openMatiere(ActionEvent event) { changerScene("Matiere.fxml", "Gestion des Matières",event); }
    @FXML private void openNote(ActionEvent event) { changerScene("NoteView.fxml", "Gestion des Notes",event); }
    @FXML private void openEnseignant(ActionEvent event) { changerScene("EnseignantView.fxml", "Gestion des Enseignants",event); }
    @FXML private void openTransaction(ActionEvent event) { changerScene("PaiementEnseignant.fxml", "Gestion des Transactions",event); }
    @FXML private void openBulletin(ActionEvent event) { changerScene("BulletinView.fxml", "Gestion des Bulletins",event); }
    @FXML private void openEmploidutemps(ActionEvent event) { changerScene("EmploiDutempsGrilleView.fxml", "Gestion des Emplois du Temps",event); }
    @FXML private void openComptabilite(ActionEvent event) { changerScene("EnseignementView.fxml", "Gestion des Cours",event); }

    
    private void cacher(Node node) {
        node.setVisible(false);
        node.setManaged(false);
    }


    
    
    // 🔹 Navigation générique
    private void changerScene(String fxml, String titre, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 650));
            stage.setTitle(titre);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ================= STATS =================
 // ================= STATS PAR ANNEE ACTIVE =================
    private void loadStats() {

        Integer anneeId = ContexteApplication.getAnneeId();


        String sqlEleves = "SELECT COUNT(*) FROM inscription WHERE annee_id = ?";
     // 🔹 Enseignants (depuis habilitation)
        String sqlEnseignants = """
                SELECT COUNT(DISTINCT enseignant_id)
                FROM habilitation
                WHERE annee_id = ?
                """;

        String sqlClasses = """
                SELECT COUNT(DISTINCT classe_id)
                FROM enseignement
                WHERE annee_id = ?
                """;
        String sqlMatieres = """
                SELECT COUNT(DISTINCT matiere_id)
                FROM enseignement
                WHERE annee_id = ?
                """;
        String sqlCours = """
                SELECT COUNT(*)
                FROM enseignement
                WHERE annee_id = ?
                """;
        String sqlHeuresMois = """
                SELECT SUM(duree)
                FROM emargement
                WHERE annee_id = ?
                AND strftime('%m', date_heure) = strftime('%m', 'now')
                AND strftime('%Y', date_heure) = strftime('%Y', 'now')
                """;

        try (Connection conn = Database.connect()) {

            // 🔹 Élèves
            try (PreparedStatement ps = conn.prepareStatement(sqlEleves)) {
                ps.setInt(1, anneeId);
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                    lblTotalEleves.setText(String.valueOf(rs.getInt(1)));
            }
               
            // 🔹 Enseignants
            try (PreparedStatement ps = conn.prepareStatement(sqlEnseignants)) {
                ps.setInt(1, anneeId);
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                    lblTotalEnseignants.setText(String.valueOf(rs.getInt(1)));
            }

            // 🔹 Classes
            try (PreparedStatement ps = conn.prepareStatement(sqlClasses)) {
                ps.setInt(1, anneeId);
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                    lblTotalClasses.setText(String.valueOf(rs.getInt(1)));
            }

            // 🔹 Matières
            try (PreparedStatement ps = conn.prepareStatement(sqlMatieres)) {
                ps.setInt(1, anneeId);
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                    lblTotalMatieres.setText(String.valueOf(rs.getInt(1)));
            }

            // 🔹 Total cours
            try (PreparedStatement ps = conn.prepareStatement(sqlCours)) {
                ps.setInt(1, anneeId);
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                    lblTotalCours.setText(String.valueOf(rs.getInt(1)));
            }

            // 🔹 Heures ce mois
            try (PreparedStatement ps = conn.prepareStatement(sqlHeuresMois)) {
                ps.setInt(1, anneeId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int minutes = rs.getInt(1);
                    double heures = minutes / 60.0;
                    lblHeuresMois.setText(String.format("%.1f h", heures));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur chargement stats : " + e.getMessage());
        }
    }
    public void refreshDashboard() {
        loadStats();
        loadDiagrammeHeuresParProf();
        loadDiagrammeHeuresParClasse();
    }
    
    // ================= RECENTS =================
    private void loadRecents() {
        recents.clear();

        String sql = """
            SELECT c.nom AS classe, en.nom AS enseignant, m.nom AS matiere, i.date_inscription AS date
            FROM inscription i
            JOIN classe c ON i.classe_id = c.id
            LEFT JOIN enseignement enst ON c.nom = enst.classe
            LEFT JOIN enseignant en ON enst.enseignant = en.nom
            LEFT JOIN matiere m ON enst.matiere = m.nom
            ORDER BY i.date_inscription DESC
            LIMIT 20
        """;

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                RecentEvent event = new RecentEvent(
                        rs.getString("classe"),
                        rs.getString("enseignant") != null ? rs.getString("enseignant") : "-",
                        rs.getString("matiere") != null ? rs.getString("matiere") : "-",
                        rs.getDate("date").toLocalDate()
                );
                recents.add(event);
            }

            tableRecents.setItems(recents);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur chargement récents : " + e.getMessage());
        }
    }

    // ================= NAVIGATION =================
    @FXML private void openAccueil() { loadScene("DashboardView.fxml", "Tableau de Bord"); }
    @FXML private void openEmploiDuTemps() { loadScene("EmploiDutempsView.fxml", "Emploi du Temps"); }
    @FXML private void openPaiement() { loadScene("PaiementEnseignant.fxml", "Paiements"); }
    @FXML private void openParametre() { loadScene("ParametreView.fxml", "Paramètres"); }
    @FXML private void openAnneescolaire() { loadScene("AnneescolaireView.fxml", "Annee scolaire"); }
    @FXML private void openUtilisateur() { loadScene("UtilisateurView.fxml", "Utilisateurs"); }
    @FXML private void openHabilitation() { loadScene("HabilitationView.fxml", "Habilitation"); }

    private void loadScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) lblUtilisateur.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur ouverture page : " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(msg);
        alert.showAndWait();
    }
  
   
    

    
    // ================= MODEL TABLE =================
    public static class RecentEvent {
        private final String classe;
        private final String enseignant;
        private final String matiere;
        private final LocalDate date;

        public RecentEvent(String classe, String enseignant, String matiere, LocalDate date) {
            this.classe = classe;
            this.enseignant = enseignant;
            this.matiere = matiere;
            this.date = date;
        }

        public String getClasse() { return classe; }
        public String getEnseignant() { return enseignant; }
        public String getMatiere() { return matiere; }
        public LocalDate getDate() { return date; }
    }
    
    @FXML
    void handleRestore(ActionEvent event){

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Restaurer la base de données ?");
        confirm.setContentText("Les données actuelles seront remplacées.");

        if(confirm.showAndWait().get() == ButtonType.OK){

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choisir un fichier de sauvegarde");

            File file = chooser.showOpenDialog(lblUtilisateur.getScene().getWindow());

            if(file != null){

                BackupService.restoreDatabase(file.getAbsolutePath());

                showAlert("Base restaurée. Redémarrez l'application.");
            }
        }
    }
    
    @FXML
    private void handleLogout() {
        // tableau pour contourner la limitation des variables finales
        final boolean[] backupLocalOk = {false};
        final boolean[] backupUsbOk = {false};

        new Thread(() -> {
            // 1️⃣ Sauvegarde locale
            try {
                BackupService.backupDatabase();
                backupLocalOk[0] = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 2️⃣ Sauvegarde sur USB
            try {
                BackupService.sauvegardeSurUSB();
                backupUsbOk[0] = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 3️⃣ Passer à l’UI thread
            Platform.runLater(() -> {
                // Notifications
                if (backupLocalOk[0] && backupUsbOk[0]) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sauvegarde");
                    alert.setHeaderText(null);
                    alert.setContentText("Sauvegardes locales et USB réussies !");
                    alert.showAndWait();
                } else if (backupLocalOk[0]) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Sauvegarde");
                    alert.setHeaderText(null);
                    alert.setContentText("Sauvegarde locale réussie, mais échec de la sauvegarde USB.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Sauvegarde");
                    alert.setHeaderText(null);
                    alert.setContentText("Échec de la sauvegarde !");
                    alert.showAndWait();
                }

                // 4️⃣ Supprimer session
                ContexteApplication.reset();

                // 5️⃣ Charger vue login
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
                    Parent root = loader.load();

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root, 1283, 657));
                    stage.setTitle("Connexion");
                    stage.show();

                    // 6️⃣ Fermer dashboard actuel
                    Stage current = (Stage) lblUtilisateur.getScene().getWindow();
                    current.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }
     
   }

    
    
   