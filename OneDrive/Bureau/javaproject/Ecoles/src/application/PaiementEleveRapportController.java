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

public class PaiementEleveRapportController {

    @FXML private TableView<PaiementEleveRapport> tablePaiement;
    
    @FXML private TableColumn<PaiementEleveRapport,String> colClasse;
    @FXML private TableColumn<PaiementEleveRapport,String> colNom;
    @FXML private TableColumn<PaiementEleveRapport,String> colPrenom;
    @FXML private TableColumn<PaiementEleveRapport,String> colFrais;
    @FXML private TableColumn<PaiementEleveRapport,Double> colTotal;
    @FXML private TableColumn<PaiementEleveRapport,Double> colPaye;
    @FXML private TableColumn<PaiementEleveRapport,Double> colReste;
    @FXML private TableColumn<PaiementEleveRapport,String> colStatut;

    @FXML private ComboBox<String> comboAnnee;
    @FXML private ComboBox<String> comboFrais;
    @FXML private ComboBox<String> comboStatut;
    @FXML private ComboBox<String> comboClasse;

    @FXML private TextField txtRecherche;

    @FXML private Label lblTotalPaye;
    @FXML private Label lblReste;
    @FXML private Label lblNombre;

    private ObservableList<PaiementEleveRapport> liste = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        
    	colClasse.setCellValueFactory(new PropertyValueFactory<>("classe"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colFrais.setCellValueFactory(new PropertyValueFactory<>("frais"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colPaye.setCellValueFactory(new PropertyValueFactory<>("paye"));
        colReste.setCellValueFactory(new PropertyValueFactory<>("reste"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        comboStatut.getItems().addAll("Tous","Soldé","Non soldé");
        comboStatut.setValue("Tous");

        chargerAnnees();
        chargerFrais();
        chargerPaiements();
        chargerClasses();
        comboClasse.setOnAction(e -> filtrer());
        
        comboAnnee.setOnAction(e -> {
            chargerPaiements();
            filtrer();
        });
        comboFrais.setOnAction(e->filtrer());
        comboStatut.setOnAction(e->filtrer());

        txtRecherche.textProperty().addListener((obs,o,n)->filtrer());
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
         

    
    

    private void chargerAnnees(){

        comboAnnee.getItems().clear();

        try(Connection conn = Database.connect()){

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT id, libelle, active FROM anneescolaire ORDER BY libelle DESC");

            String active = null;

            while(rs.next()){

                String item = rs.getInt("id")+" - "+rs.getString("libelle");
                comboAnnee.getItems().add(item);

                if(rs.getInt("active")==1){
                    active = item;
                }
            }

            if(active!=null)
                comboAnnee.setValue(active);

        }catch(Exception e){
            e.printStackTrace();
        }
    }     
    private void chargerClasses(){

        comboClasse.getItems().add("Toutes");

        try(Connection conn = Database.connect()){
        	
        	

            String sql = "SELECT \r\n"
            		+ "n.nom || ' ' || IFNULL(s.nom,'') || ' ' || IFNULL(g.nom,'') AS classe\r\n"
            		+ "FROM classe c\r\n"
            		+ "JOIN niveau n ON c.niveau_id = n.id\r\n"
            		+ "LEFT JOIN serie s ON c.serie_id = s.id\r\n"
            		+ "LEFT JOIN groupe g ON c.groupe_id = g.id\r\n"
            		+ "ORDER BY n.nom";

            ResultSet rs = conn.createStatement().executeQuery(sql);

            while(rs.next()){
                comboClasse.getItems().add(rs.getString("classe"));
            }

            comboClasse.setValue("Toutes");

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void chargerFrais(){

        comboFrais.getItems().clear();
        comboFrais.getItems().add("Tous");

        try(Connection conn = Database.connect()){

            String sql = """
                    SELECT DISTINCT m.nom
                    FROM motif m
                    JOIN frais f ON f.motif_id = m.id
                    ORDER BY m.nom
                    """;

            ResultSet rs = conn.createStatement().executeQuery(sql);

            while(rs.next()){
                comboFrais.getItems().add(rs.getString("nom"));
            }

            comboFrais.setValue("Tous");

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void chargerPaiements(){

        if(comboAnnee.getValue()==null)
            return;

        int anneeId = Integer.parseInt(comboAnnee.getValue().split(" - ")[0]);

        liste.clear();

        String sql = """
    SELECT 
    e.nom,
    e.prenom,
    n.nom AS niveau,
    s.nom AS serie,
    g.nom AS groupe,
    m.nom AS frais,
    f.montant AS total,
    COALESCE(SUM(p.montant_paye),0) AS paye
    FROM eleve e
    JOIN inscription i ON i.eleve_id = e.id
    JOIN classe c ON i.classe_id = c.id
    JOIN niveau n ON c.niveau_id = n.id
    LEFT JOIN serie s ON c.serie_id = s.id
    LEFT JOIN groupe g ON c.groupe_id = g.id
    JOIN frais f ON f.niveau_id = n.id
    JOIN motif m ON f.motif_id = m.id
    LEFT JOIN paiements p 
         ON p.eleve_id = e.id 
         AND p.frais_id = f.id
         AND p.annee_id = ?
    GROUP BY e.id,f.id
    """;

        try(Connection conn = Database.connect();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, anneeId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                double total = rs.getDouble("total");
                double paye = rs.getDouble("paye");
                double reste = total - paye;

                String classe =
                        rs.getString("niveau")+" "+
                        (rs.getString("serie")==null?"":rs.getString("serie"))+" "+
                        (rs.getString("groupe")==null?"":rs.getString("groupe"));

                String statut = reste==0 ? "Soldé" : "Non soldé";

                liste.add(new PaiementEleveRapport(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        classe,
                        rs.getString("frais"),
                        total,
                        paye,
                        reste,
                        statut
                ));
            }

            tablePaiement.setItems(liste);
            filtrer();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void filtrer(){

        String statut = comboStatut.getValue();
        String classe = comboClasse.getValue();
        String frais = comboFrais.getValue();
        String recherche = txtRecherche.getText().toLowerCase();

        ObservableList<PaiementEleveRapport> filtre = FXCollections.observableArrayList();

        for(PaiementEleveRapport p : liste){
            
        	boolean matchClasse = "Toutes".equals(classe) || 
                    p.getClasse().equalsIgnoreCase(classe);
            boolean matchStatut = statut.equals("Tous") || p.getStatut().equalsIgnoreCase(statut);
            boolean matchFrais = frais.equals("Tous") || p.getFrais().equalsIgnoreCase(frais);
            boolean matchRecherche = recherche.isEmpty() ||
                    p.getNom().toLowerCase().contains(recherche) ||
                    p.getPrenom().toLowerCase().contains(recherche);

            if(matchStatut && matchFrais && matchRecherche && matchClasse){
                filtre.add(p);
            }
        }

        tablePaiement.setItems(filtre);
        stats(filtre);
    }

    private void stats(ObservableList<PaiementEleveRapport> list){

        double totalPaye=0;
        double reste=0;
        int nb=0;

        for(PaiementEleveRapport p:list){

            totalPaye+=p.getPaye();
            reste+=p.getReste();

            if(p.getPaye()>0)
                nb++;
        }

        lblTotalPaye.setText("Total payé : "+String.format("%,.0f",totalPaye)+" FCFA");
        lblReste.setText("Reste à payer : "+String.format("%,.0f",reste)+" FCFA");
        lblNombre.setText("Nombre élèves payés : "+nb);
    }
}