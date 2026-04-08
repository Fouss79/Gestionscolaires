package application;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;

public class PaiementController {

    @FXML
    private TableView<Paiement> tablePaiement;

    @FXML
    private TableColumn<Paiement, String> colNom;
    @FXML
    private TableColumn<Paiement, String> colPrenom;
    @FXML
    private TableColumn<Paiement, String> colMotif;
    @FXML
    private TableColumn<Paiement, String> colMois;
    @FXML
    private TableColumn<Paiement, Double> colTotal;
    @FXML
    private TableColumn<Paiement, Double> colPaye;
    @FXML
    private TableColumn<Paiement, Double> colReste;
    @FXML
    private TableColumn<Paiement, String> colStatut;
    @FXML
    private TableColumn<Paiement, Void> colVoir;
    @FXML
    private TableColumn<Paiement, Void> colRecu;

    @FXML private ComboBox<String> comboAnnee;
    @FXML private ComboBox<String> comboMois;
    @FXML private ComboBox<String> comboStatut;
    @FXML private TextField txtRecherche;

    @FXML private Label lblTotalPaye;
    @FXML private Label lblReste;
    @FXML private Label lblNombre;

    private ObservableList<Paiement> liste = FXCollections.observableArrayList();

    public void initialize() {
        // Initialisation colonnes
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colMois.setCellValueFactory(new PropertyValueFactory<>("mois"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colPaye.setCellValueFactory(new PropertyValueFactory<>("montantPaye"));
        colReste.setCellValueFactory(new PropertyValueFactory<>("reste"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Statut coloré
        colStatut.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) { setText(null); setStyle(""); }
                else {
                    setText(statut);
                    setStyle(statut.equals("Soldé") ? "-fx-text-fill: green; -fx-font-weight:bold;" :
                                                      "-fx-text-fill: red; -fx-font-weight:bold;");
                }
            }
        });

        // Bouton "Voir"
        colVoir.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Voir");
            {
                btn.setOnAction(e -> {
                    Paiement p = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Détail paiement");
                    alert.setContentText(
                        "Enseignant : " + p.getNom() + " " + p.getPrenom() +
                        "\nMotif : " + p.getMotif() +
                        "\nMois : " + p.getMois() +
                        "\nAnnée : " + p.getAnnee() +
                        "\nTotal : " + p.getMontantTotal() +
                        "\nPayé : " + p.getMontantPaye() +
                        "\nReste : " + p.getReste() +
                        "\nStatut : " + p.getStatut()
                    );
                    alert.showAndWait();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Bouton "PDF"
        colRecu.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("PDF");
            {
                btn.setOnAction(e -> {
                    Paiement p = getTableView().getItems().get(getIndex());
                    System.out.println("Impression reçu pour : " + p.getNom() + " " + p.getPrenom());
                    // TODO: générer PDF
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Combos
        comboStatut.getItems().addAll("Tous", "Soldé", "Non soldé");
        comboStatut.setValue("Tous");

        comboMois.getItems().addAll(
            "Tous","Janvier","Février","Mars","Avril","Mai","Juin",
            "Juillet","Août","Septembre","Octobre","Novembre","Décembre"
        );
        comboMois.setValue("Tous");

        chargerAnnees();

        // Charger paiements
        chargerPaiements();

        // Listeners filtres
        comboAnnee.setOnAction(e -> filtrerPaiements());
        comboStatut.setOnAction(e -> filtrerPaiements());
        comboMois.setOnAction(e -> filtrerPaiements());
        txtRecherche.textProperty().addListener((obs, oldV, newV) -> filtrerPaiements());
    }
    
    
    @FXML
    private void OpenPaiement(ActionEvent event) {

        loadScene("ComptableView.fxml", "Paiements");

    }
    
       

        @FXML
        void handleRapport() {loadScene("PaiementView.fxml", "Habilitation");

        }
        @FXML
        void handleRapportScolaire() {loadScene("PaiementEleveRapportView.fxml", "Habilitation");

        }

        @FXML
        void openScolarite() {loadScene("PaiementEleveView.fxml", "Scolarite");

        }
      

    
        private void loadScene(String fxml, String title) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
                Parent root = loader.load();
                Stage stage = (Stage) tablePaiement.getScene().getWindow();
                stage.setScene(new Scene(root, 1283, 657));
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
      
        @FXML
        private void handleLogout() {

            try {

                // supprimer session
            	ContexteApplication.reset();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root,1283,657));
                stage.setTitle("Connexion");
                stage.show();

                // fermer dashboard actuel
                Stage current = (Stage) tablePaiement.getScene().getWindow();
                current.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
         
         
       

    private void chargerAnnees() {
        comboAnnee.getItems().clear();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ecole.db")) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT libelle FROM anneescolaire ORDER BY libelle DESC");
            while(rs.next()) comboAnnee.getItems().add(rs.getString("libelle"));
            rs = conn.createStatement().executeQuery("SELECT libelle FROM anneescolaire WHERE active = 1");
            if(rs.next()) comboAnnee.setValue(rs.getString("libelle"));
            else if(!comboAnnee.getItems().isEmpty()) comboAnnee.setValue(comboAnnee.getItems().get(0));
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void chargerPaiements() {
        liste.clear();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ecole.db")) {
            String sql = """
                SELECT p.*, a.libelle AS annee
                FROM paiement p
                JOIN anneescolaire a ON p.annee_id = a.id
            """;
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                double total = rs.getDouble("montant_total");
                double paye = rs.getDouble("montant_paye");
                double reste = total - paye;

                Paiement p = new Paiement(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("motif"),
                    rs.getString("type"),
                    rs.getString("mois"),
                    rs.getString("annee"),
                    total,
                    paye,
                    reste
                   
                );
                liste.add(p);
            }
            tablePaiement.setItems(liste);
            filtrerPaiements();
        } catch(Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void filtrerPaiements() {
        String annee = comboAnnee.getValue();
        String statut = comboStatut.getValue();
        String mois = comboMois.getValue();
        String recherche = txtRecherche.getText().trim().toLowerCase();

        ObservableList<Paiement> filtre = FXCollections.observableArrayList();

        for(Paiement p : liste) {
        	boolean matchAnnee = annee == null || p.getAnnee().equalsIgnoreCase(annee);
            boolean matchStatut = statut.equals("Tous") || p.getStatut().equalsIgnoreCase(statut);
            boolean matchMois = mois.equals("Tous") || p.getMois().equalsIgnoreCase(mois);
            boolean matchRecherche = recherche.isEmpty() || p.getNom().toLowerCase().contains(recherche);

            if(matchAnnee && matchStatut && matchMois && matchRecherche) filtre.add(p);
        }

        tablePaiement.setItems(filtre);
        mettreAJourStatistiques(filtre);
    }

    private void mettreAJourStatistiques(ObservableList<Paiement> listeFiltree) {
        double totalPaye = 0;
        double resteTotal = 0;
        int nbPayes = 0;

        for(Paiement p : listeFiltree) {
            totalPaye += p.getMontantPaye();
            resteTotal += p.getReste();
            if(p.getMontantPaye() > 0) nbPayes++;
        }

        lblTotalPaye.setText("Total payé ce mois : " + String.format("%,.0f", totalPaye) + " FCFA");
        lblReste.setText("Reste à payer : " + String.format("%,.0f", resteTotal) + " FCFA");
        lblNombre.setText("Nombre enseignants payés : " + nbPayes);
    }
}