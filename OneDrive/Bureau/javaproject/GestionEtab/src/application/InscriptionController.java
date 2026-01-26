package application;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDate;

public class InscriptionController {

    @FXML private ComboBox<String> cbEleve;
    @FXML private ComboBox<String> cbClasse;
    @FXML private ComboBox<String> cbAnnee;
    @FXML private TextField txtMatricule;
    @FXML private ComboBox<String> cbClasseFiltre;
    @FXML private ComboBox<String> cbAnneeFiltre;

    @FXML private TableView<Inscrit> tableInscriptions;
    @FXML private TableColumn<Inscrit,String> colMatricule;
    @FXML private TableColumn<Inscrit,String> colNom;
    @FXML private TableColumn<Inscrit,String> colPrenom;
    @FXML private TableColumn<Inscrit,String> colSexe;
    @FXML private TableColumn<Inscrit,String> colClasse;
    @FXML private TableColumn<Inscrit,String> colAnnee;

    
    @FXML private Label lblStats;

    private ObservableList<Inscrit> liste = FXCollections.observableArrayList();
 
    
    
    // ================== INITIALIZE ==================
    @FXML
    public void initialize() {
        loadEleves();
        loadClasses();
        loadAnnees();

        // 🔹 Initialiser les colonnes
        colMatricule.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMatricule()));
        colNom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNom()));
        colPrenom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPrenom()));
        colSexe.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSexe()));
        colClasse.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getClasse()));
        colAnnee.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAnnee()));

        // 🔹 Charger filtres
        loadClasseFiltre();
        loadAnneeFiltre();
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

    // ================== JDBC ==================
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/gestionecole",
                "root",
                ""
        );
    }

    // ================== LOAD DATA ==================

    private void loadEleves() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT id, nom, prenom FROM eleve";

        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(rs.getLong("id") + " - " +
                        rs.getString("nom") + " " +
                        rs.getString("prenom"));
            }
            cbEleve.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private Long getEleveIdParMatricule(String matricule) {

        String sql = "SELECT id FROM eleve WHERE numero_matricule = ?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, matricule);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getLong("id");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void loadClasses() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT id, nom FROM classe";

        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(rs.getInt("id") + " - " + rs.getString("nom"));
            }
            cbClasse.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAnnees() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT id, libelle FROM anneescolaire";

        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(rs.getInt("id") + " - " + rs.getString("libelle"));
            }
            cbAnnee.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void chargerInscriptions() {

        if (cbClasseFiltre.getValue() == null || cbAnneeFiltre.getValue() == null) {
            showAlert("Erreur", "Choisissez classe et année !");
            return;
        }

        int classeId = Integer.parseInt(cbClasseFiltre.getValue().split(" - ")[0]);
        int anneeId  = Integer.parseInt(cbAnneeFiltre.getValue().split(" - ")[0]);

        liste.clear();

        String sql = """
        	    SELECT e.numero_matricule, e.nom, e.prenom, e.sexe, c.nom AS classe, a.libelle AS annee
        	    FROM inscription i
        	    JOIN eleve e ON i.eleve_id = e.id
        	    JOIN classe c ON i.classe_id = c.id
        	    JOIN anneescolaire a ON i.annee_id = a.id
        	    WHERE i.classe_id = ? AND i.annee_id = ?
        	""";

        	liste.clear();
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

        	        liste.add(new Inscrit(
        	                rs.getString("numero_matricule"),
        	                rs.getString("nom"),
        	                rs.getString("prenom"),
        	                sexe,
        	                rs.getString("classe"),
        	                rs.getString("annee") // <-- ici
        	        ));
        	    }

        	    tableInscriptions.setItems(liste);
        	    lblStats.setText("Effectif: " + liste.size() +
        	            " | Garçons: " + garcons +
        	            " | Filles: " + filles);

        	} catch (Exception e) {
        	    e.printStackTrace();
        	    showAlert("Erreur", "Erreur chargement inscriptions");
        	}
    }
       
    @FXML
    private void imprimerPDF() {

            }

     
    // ================== INSCRIPTION ==================

    @FXML
    private void inscrireEleve() {

        if (cbClasse.getValue() == null || cbAnnee.getValue() == null) {
            showAlert("Erreur", "Classe et année obligatoires !");
            return;
        }

        Long eleveId;

        // 👉 PRIORITÉ AU MATRICULE
        if (!txtMatricule.getText().isEmpty()) {

            eleveId = getEleveIdParMatricule(txtMatricule.getText().trim());

            if (eleveId == null) {
                showAlert("Erreur", "Aucun élève trouvé avec ce matricule !");
                return;
            }

        } else if (cbEleve.getValue() != null) {

            eleveId = Long.parseLong(cbEleve.getValue().split(" - ")[0]);

        } else {
            showAlert("Erreur", "Entrez le matricule ou choisissez un élève !");
            return;
        }

        int classeId = Integer.parseInt(cbClasse.getValue().split(" - ")[0]);
        int anneeId  = Integer.parseInt(cbAnnee.getValue().split(" - ")[0]);

        String sql = """
            INSERT INTO inscription (eleve_id, classe_id, annee_id, date_inscription)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, eleveId);
            ps.setInt(2, classeId);
            ps.setInt(3, anneeId);
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();

            showAlert("Succès", "Élève inscrit avec succès !");
            clearForm();

        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert("Attention", "Cet élève est déjà inscrit pour cette année !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'inscription !");
        }
    }
    // ================== UTILS ==================

    @FXML
    private void clearForm() {
        cbEleve.setValue(null);
        cbClasse.setValue(null);
        cbAnnee.setValue(null);
    }

    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

