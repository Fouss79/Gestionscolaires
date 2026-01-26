package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EmargementController {

    // JDBC
    private final String URL = "jdbc:mysql://localhost:3306/GestionEcole";
    private final String USER = "root";
    private final String PASSWORD = "";
     
    @FXML
    private Button btnBulletin;

    @FXML
    private Button btnClasse;

    @FXML
    private Button btnComptabilite;

    @FXML
    private Button btnEleve;

    @FXML
    private Button btnEmarger;

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

    
    
    
    // Composants JavaFX
    @FXML private ComboBox<String> cbEnseignant;
    @FXML private ComboBox<String> cbJour;

    
    @FXML private DatePicker dpDate;

    @FXML private TableView<EmploiDutemps> tableEmploi;
    @FXML private TableColumn<EmploiDutemps, String> colJour;
    @FXML private TableColumn<EmploiDutemps, String> colDebut;
    @FXML private TableColumn<EmploiDutemps, String> colProfesseurId;
    @FXML private TableColumn<EmploiDutemps, String> colProfesseur;
    @FXML private TableColumn<EmploiDutemps, String> colClasse;
    @FXML private TableColumn<EmploiDutemps, String> colMatiere;

    @FXML private TableView<Emargement> tableEmargement;
    @FXML private TableColumn<Emargement, String> colEmargJour;
    @FXML private TableColumn<Emargement, Integer> colEmargId;
    @FXML private TableColumn<Emargement, String> colEmargEnseignant;
    @FXML private TableColumn<Emargement, String> colEmargClasse;
    @FXML private TableColumn<Emargement, String> colEmargMatiere;
    @FXML private TableColumn<Emargement, String> colEmargDate;
    @FXML private TableColumn<Emargement, String> colEmargDuree;
    @FXML private TableColumn<Emargement, Boolean> colEmargPresent;

   
    private ObservableList<EmploiDutemps> emplois = FXCollections.observableArrayList();
    private ObservableList<Emargement> emargementsDuJour = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
    	
        // Charger les enseignants
        loadEnseignants();
     // Remplir le ComboBox avec les jours de la semaine
        cbJour.setItems(FXCollections.observableArrayList(null,
            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"
        ));

        // Valeur par défaut = jour actuel
        cbJour.setValue(LocalDate.now().getDayOfWeek().toString());

        // Quand on change de jour → charger l’emploi du temps
        cbJour.setOnAction(e -> {
            String jourChoisi = cbJour.getValue();
            if (jourChoisi != null) {
                loadEmploiDuTempsParJour(jourChoisi);
                loadEmargementsDuJourParjour(jourChoisi, dpDate.getValue());
            }
        });

        // Valeur par défaut pour la date : aujourd’hui
        dpDate.setValue(LocalDate.now());

        // Configurer colonnes Table Emploi
        colJour.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getJour().toString()));
        colClasse.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getClasse()));
        colMatiere.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMatiere()));
        colProfesseur.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getProfesseur()));
        
        tableEmploi.setItems(emplois);

        // Configurer colonnes Table Emargement
        colEmargId.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
        colEmargJour.setCellValueFactory(cell -> cell.getValue().jourProperty());
        colEmargEnseignant.setCellValueFactory(cell -> cell.getValue().enseignantProperty());

        colEmargClasse.setCellValueFactory(cell -> cell.getValue().classeProperty());
        colEmargMatiere.setCellValueFactory(cell -> cell.getValue().matiereProperty());
        colEmargDate.setCellValueFactory(cell -> cell.getValue().dateProperty());
        colEmargDuree.setCellValueFactory(cell -> cell.getValue().dureeLisibleProperty());
        colEmargPresent.setCellValueFactory(cell -> cell.getValue().presentProperty());
        tableEmargement.setItems(emargementsDuJour);

        // Quand un enseignant est sélectionné
        cbEnseignant.setOnAction(e -> {
        	System.out.println("oui");
                String enseignantId = cbEnseignant.getValue().split(" - ")[0];
                if(enseignantId != null) {
                loadEmploiDuTemps();
                loadEmargementsDuJour(enseignantId, dpDate.getValue());
                
                }
            
        });

        // Quand la date change
        dpDate.setOnAction(e -> {
      
            if (cbEnseignant.getValue() != null) {
                String enseignantId = cbEnseignant.getValue().split(" - ")[0];
                loadEmargementsDuJour(enseignantId, dpDate.getValue());
            }
        });
    }

    private void loadEnseignants() {
    	
        String sql = "SELECT id, nom FROM enseignants";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                cbEnseignant.getItems().add(rs.getInt("id") + " - " + rs.getString("nom"));
                
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadEmploiDuTemps() {
        emplois.clear();
        String sql = "SELECT * FROM emploi_du_temps WHERE professeur = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String enseignantNom = cbEnseignant.getValue();
            

            ps.setString(1, enseignantNom);
        

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EmploiDutemps e = new EmploiDutemps(
                        rs.getInt("id"),
                        DayOfWeek.valueOf(rs.getString("jour").toUpperCase()),
                        rs.getString("heure_debut"),
                        rs.getString("heure_fin"),
                        rs.getString("matiere"),
                   
                        rs.getString("professeur"),
                        rs.getString("classe")
                );
                emplois.add(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void openDeconnexion(ActionEvent event) {
        try {
            changerScene(event, "LoginView.fxml", "Connexion");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la déconnexion.");
        }
    }

       
    private void showError(String string) {
		// TODO Auto-generated method stub
		
	}

	private void loadEmploiDuTempsParJour(String jour) {
        emplois.clear();
        String sql = "SELECT * FROM emploi_du_temps WHERE jour = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, jour);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EmploiDutemps e = new EmploiDutemps(
                        rs.getInt("id"),
                        DayOfWeek.valueOf(rs.getString("jour").toUpperCase()),
                        rs.getString("heure_debut"),
                        rs.getString("heure_fin"),
                        rs.getString("matiere"),
   
                        rs.getString("professeur"),
                        rs.getString("classe")
                );
                emplois.add(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    

       private void loadEmargementsDuJour(String enseignantId, LocalDate date) {
    	 
        emargementsDuJour.clear();
        String sql = "SELECT * FROM emargement WHERE enseignant_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, enseignantId);
            // la date choisie
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Emargement e = new Emargement(
                        rs.getInt("id"),
                        rs.getString("jour"),
                        rs.getString("enseignant_id"),
                        rs.getString("enseignant"),
                        rs.getString("classe"),
                        rs.getString("matiere"),
                        rs.getTimestamp("date_heure").toLocalDateTime(),
                        rs.getInt("duree"),
                        rs.getBoolean("present")
                );
                emargementsDuJour.add(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
      
       private void loadEmargementsDuJourParjour(String jour, LocalDate date) {
           emargementsDuJour.clear();
           String sql = "SELECT * FROM emargement WHERE jour = ?";
           try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)) {

               ps.setString(1, jour);
               // la date choisie
               ResultSet rs = ps.executeQuery();
               while (rs.next()) {
                   Emargement e = new Emargement(
                           rs.getInt("id"),
                           rs.getString("jour"),
                           rs.getString("enseignant_id"),
                           rs.getString("enseignant"),
                           rs.getString("classe"),
                           rs.getString("matiere"),
                           rs.getTimestamp("date_heure").toLocalDateTime(),
                           rs.getInt("duree"),
                           rs.getBoolean("present")
                   );
                   emargementsDuJour.add(e);
               }

           } catch (SQLException e) {
               e.printStackTrace();
           }
       }
       
       private void changerScene(ActionEvent event, String fxml, String titre) {
           try {
               FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
               Parent root = loader.load();
               Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
               stage.setScene(new Scene(root, 1283, 657));
               stage.setTitle(titre);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
       @FXML
       void openEmargement(ActionEvent event) {
       	changerScene(event,"EmargementView.fxml", "Gestion des Emargements");

       }
       @FXML private void retourAccueil() { System.out.println("Retour au Tableau de Bord"); }
       @FXML private void openClasse(ActionEvent event) { changerScene(event,"ClasseView.fxml", "Gestion des Classes"); }
       @FXML private void openEleve(ActionEvent event) { changerScene(event,"EleveForm.fxml", "Gestion des Élèves"); }
       @FXML private void openMatiere(ActionEvent event) { changerScene(event,"Matiere.fxml", "Gestion des Matières"); }
       @FXML private void openNote(ActionEvent event) { changerScene(event,"NoteView.fxml", "Gestion des Notes"); }
       @FXML private void openEnseignant(ActionEvent event) { changerScene(event,"EnseignantView.fxml", "Gestion des Enseignants"); }
       @FXML private void openTransaction(ActionEvent event) { changerScene(event,"PaiementEnseignant.fxml", "Gestion des Transactions"); }
       @FXML private void openBulletin(ActionEvent event) { changerScene(event,"BulletinView.fxml", "Gestion des Bulletins"); }
       @FXML private void openEmploidutemps(ActionEvent event) { changerScene(event,"EmploiDutempsView.fxml", "Gestion des Emplois du Temps"); }
       @FXML private void openComptabilite(ActionEvent event) { changerScene(event,"EnseignementView.fxml", "Gestion des Cours"); }

      
    

       @FXML
       private void emarger() {
           EmploiDutemps selected = tableEmploi.getSelectionModel().getSelectedItem();
           if (selected == null) {
               new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une séance !").show();
               return;
           }

           LocalDate selectedDate = dpDate.getValue();
           if (selectedDate == null) {
               new Alert(Alert.AlertType.WARNING, "Veuillez choisir une date !").show();
               return;
           }

           // Durée en minutes
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
           LocalTime debut = LocalTime.parse(selected.getHeureDebut(), formatter);
           LocalTime fin = LocalTime.parse(selected.getHeureFin(), formatter);
           int duree = (int) Duration.between(debut, fin).toMinutes();

           // 🔥 Déterminer enseignant + jour
           String ensId;
           String ensNom;
           String jour = selected.getJour().toString();

         if (selected.getProfesseur().contains(" - ")) {
               String[] parts = selected.getProfesseur().split(" - ");
               ensId = parts[0].trim();
               ensNom = parts[1].trim();
           } else {
               ensId = "";
               ensNom = selected.getProfesseur();
           }

           try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

               // --- Vérification du doublon ---
               String checkSql = """
                   SELECT COUNT(*) FROM emargement
                   WHERE enseignant_id = ? AND classe = ? AND matiere = ? AND DATE(date_heure) = ?
               """;
               try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                   checkPs.setString(1, ensId);
                   checkPs.setString(2, selected.getClasse());
                   checkPs.setString(3, selected.getMatiere());
                   checkPs.setDate(4, Date.valueOf(selectedDate));

                   ResultSet rs = checkPs.executeQuery();
                   rs.next();
                   if (rs.getInt(1) > 0) {
                	   
                       new Alert(Alert.AlertType.WARNING,
                               "Cette séance a déjà été émargée pour cette date !").show();
                      
                       return;
                   }
                  
               }

               // --- Insertion de l'émargement ---
               String insertSql = """
                   INSERT INTO emargement 
                   (enseignant_id, jour, enseignant, classe, matiere, date_heure, duree, present)
                   VALUES (?, ?, ?, ?, ?, ?, ?, ?)
               """;

               try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                   // Si l'ID est vide → on met NULL
                   if (ensId == null || ensId.isEmpty()) {
                       ps.setNull(1, java.sql.Types.INTEGER);
                   } else {
                       ps.setInt(1, Integer.parseInt(ensId));
                   }

                   ps.setString(2, jour);
                   ps.setString(3, ensNom);
                   ps.setString(4, selected.getClasse());
                   ps.setString(5, selected.getMatiere());
                   ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.of(selectedDate, LocalTime.now())));
                   ps.setInt(7, duree);
                   ps.setBoolean(8, true);

                   ps.executeUpdate();
                   new Alert(Alert.AlertType.INFORMATION, "Présence enregistrée !").show();

                   if (!ensId.isEmpty()) {
                       loadEmargementsDuJour(ensId, selectedDate);
                   }
               }

           } catch (SQLException e) {
               e.printStackTrace();
               new Alert(Alert.AlertType.ERROR, "Erreur lors de l'émargement !").show();
           }
       }
}
