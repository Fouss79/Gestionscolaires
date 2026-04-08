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
import java.time.LocalDate;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PaiementEleveController {

    @FXML private ComboBox<String> cbEleve;
    @FXML private ComboBox<String> cbFrais;
    @FXML private ComboBox<String> cbMode;
    @FXML private ComboBox<String> comboAnnee;

    @FXML private TextField txtMontant;
    @FXML private TextField txtTotal;
    @FXML private TextField txtPaye;
    @FXML private TextField txtReste;

    @FXML private TableView<PaiementEleve> tablePaiement;
    @FXML private TableColumn<PaiementEleve,String> colEleve;
    @FXML private TableColumn<PaiementEleve,String> colFrais;
    @FXML private TableColumn<PaiementEleve,Double> colMontant;
    @FXML private TableColumn<PaiementEleve,String> colDate;

    @FXML private TableView<TransactionEleve> tableTransaction;
    @FXML private TableColumn<TransactionEleve,Integer> colTransId;
    @FXML private TableColumn<TransactionEleve,String> colTransEleve;
    @FXML private TableColumn<TransactionEleve,Double> colTransMontant;
    @FXML private TableColumn<TransactionEleve,String> colTransMode;
    @FXML private TableColumn<TransactionEleve,String> colTransDate;

    private ObservableList<PaiementEleve> liste = FXCollections.observableArrayList();
    private int anneeActiveId = -1;

    @FXML
    private void initialize(){

        // --- Initialisation colonnes ---
        colEleve.setCellValueFactory(new PropertyValueFactory<>("eleve"));
        colFrais.setCellValueFactory(new PropertyValueFactory<>("frais"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        colTransId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTransEleve.setCellValueFactory(new PropertyValueFactory<>("eleve"));
        colTransMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colTransMode.setCellValueFactory(new PropertyValueFactory<>("mode"));
        colTransDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        cbMode.setItems(FXCollections.observableArrayList("Cash","Orange Money","Wave","Virement"));

        // --- Charger les élèves ---
        chargerEleves();
        cbEleve.setOnAction(e -> {
            if(cbEleve.getValue()==null) return;
            int eleveId = Integer.parseInt(cbEleve.getValue().split(" - ")[0]);
            chargerFraisParEleve(eleveId);
        });

        // --- Charger années scolaires ---
        chargerAnnees();

        // --- Listener sur changement d'année ---
        comboAnnee.setOnAction(e -> {
            if(comboAnnee.getValue() != null) {
                anneeActiveId = Integer.parseInt(comboAnnee.getValue().split(" - ")[0]);
                chargerPaiements();
                chargerTransactions();
            }
        });

        // --- Listener sur txtMontant ---
        txtMontant.textProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal.isEmpty()) return;
            try{
                double valeur = Double.parseDouble(newVal);
                double reste = Double.parseDouble(txtReste.getText());
                if(valeur > reste) txtMontant.setText(String.valueOf(reste));
            }catch(NumberFormatException e){
                txtMontant.setText(oldVal);
            }
        });

        // --- Charger paiements et transactions ---
        chargerPaiements();
        chargerTransactions();
    }

    // -------------------- ANNEES SCOLAIRES --------------------
    private void chargerAnnees() {

        comboAnnee.getItems().clear();
        String sql = "SELECT id, libelle, active FROM anneescolaire ORDER BY libelle DESC";

        try(Connection conn = Database.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)){

            String anneeActiveItem = null;

            while(rs.next()){
                String item = rs.getInt("id")+" - "+rs.getString("libelle");
                comboAnnee.getItems().add(item);
                if(rs.getInt("active") == 1){
                    anneeActiveItem = item;
                    anneeActiveId = rs.getInt("id");
                }
            }

            if(anneeActiveItem != null){
                comboAnnee.setValue(anneeActiveItem);
            }

        }catch(Exception e){e.printStackTrace();}
    }

    // -------------------- ELEVE ET FRAIS --------------------
    private void chargerEleves(){
        String sql = "SELECT id, nom, prenom FROM eleve";
        try(Connection conn = Database.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            cbEleve.getItems().clear();
            while(rs.next()){
                cbEleve.getItems().add(
                        rs.getInt("id")+" - "+rs.getString("nom")+" "+rs.getString("prenom")
                );
            }
        }catch(Exception e){e.printStackTrace();}
    }

    private void chargerFraisParEleve(int eleveId){
        cbFrais.getItems().clear();
        String sql = """
            SELECT f.id, m.nom
            FROM frais f
            JOIN motif m ON f.motif_id = m.id
            JOIN niveau n ON f.niveau_id = n.id
            JOIN classe c ON c.niveau_id = n.id
            JOIN inscription i ON i.classe_id = c.id
            WHERE i.eleve_id = ?
            """;
        try(Connection conn = Database.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eleveId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                cbFrais.getItems().add(rs.getInt("id")+" - "+rs.getString("nom"));
            }
        }catch(Exception e){e.printStackTrace();}
    }

    // -------------------- SITUATION PAIEMENT --------------------
    @FXML
    private void chargerSituation(){
        if(cbEleve.getValue()==null || cbFrais.getValue()==null) return;

        int eleveId = Integer.parseInt(cbEleve.getValue().split(" - ")[0]);
        int fraisId = Integer.parseInt(cbFrais.getValue().split(" - ")[0]);

        try(Connection conn = Database.connect()){
            // Total du frais
            PreparedStatement psTotal = conn.prepareStatement("SELECT montant FROM frais WHERE id=?");
            psTotal.setInt(1,fraisId);
            ResultSet rsTotal = psTotal.executeQuery();
            double total = rsTotal.next() ? rsTotal.getDouble("montant") : 0;
            txtTotal.setText(String.valueOf(total));

            // Total déjà payé pour l'année active
            PreparedStatement psPaye = conn.prepareStatement(
                "SELECT SUM(montant_paye) as total FROM paiements WHERE eleve_id=? AND frais_id=? AND annee_id=?"
            );
            psPaye.setInt(1, eleveId);
            psPaye.setInt(2, fraisId);
            psPaye.setInt(3, anneeActiveId);
            ResultSet rsPaye = psPaye.executeQuery();
            double paye = rsPaye.next() ? rsPaye.getDouble("total") : 0;
            txtPaye.setText(String.valueOf(paye));

            double reste = total - paye;
            txtReste.setText(String.valueOf(reste));
            txtMontant.setDisable(reste <= 0);

        }catch(Exception e){e.printStackTrace();}
    }

    // -------------------- PAIEMENTS --------------------
    private void chargerPaiements(){
        if(anneeActiveId == -1) return;
        liste.clear();

        String sql = """
            SELECT e.nom || ' ' || e.prenom AS eleve,
                   m.nom AS frais,
                   p.montant_paye,
                   p.date_paiement
            FROM paiements p
            JOIN eleve e ON p.eleve_id = e.id
            JOIN frais f ON p.frais_id = f.id
            JOIN motif m ON f.motif_id = m.id
            JOIN anneescolaire a ON p.annee_id = a.id
            WHERE a.id = ?
            """;

        try(Connection conn = Database.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, anneeActiveId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                liste.add(new PaiementEleve(
                        rs.getString("eleve"),
                        rs.getString("frais"),
                        rs.getDouble("montant_paye"),
                        rs.getString("date_paiement")
                ));
            }

            tablePaiement.setItems(liste);

        }catch(Exception e){e.printStackTrace();}
    }

    // -------------------- TRANSACTIONS --------------------
    private void chargerTransactions(){
        if(anneeActiveId == -1) return;

        ObservableList<TransactionEleve> list = FXCollections.observableArrayList();

        String sql = """
            SELECT t.id,
                   e.nom || ' ' || e.prenom AS eleve,
                   t.montant,
                   t.mode_paiement,
                   t.date_transaction
            FROM transaction_eleve t
            JOIN paiements p ON t.paiement_id = p.id
            JOIN eleve e ON t.eleve_id = e.id
            WHERE p.annee_id = ?
            ORDER BY t.date_transaction DESC
            """;

        try(Connection conn = Database.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, anneeActiveId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                list.add(new TransactionEleve(
                        rs.getInt("id"),
                        rs.getString("eleve"),
                        rs.getDouble("montant"),
                        rs.getString("mode_paiement"),
                        rs.getString("date_transaction")
                ));
            }

            tableTransaction.setItems(list);

        }catch(Exception e){e.printStackTrace();}
    }

    // -------------------- AJOUTER PAIEMENT --------------------
    @FXML
    private void ajouterPaiement(){

        if(comboAnnee.getValue()==null || cbEleve.getValue()==null || cbFrais.getValue()==null || cbMode.getValue()==null)
            return;

        int eleveId = Integer.parseInt(cbEleve.getValue().split(" - ")[0]);
        int fraisId = Integer.parseInt(cbFrais.getValue().split(" - ")[0]);
        String eleveNom = cbEleve.getValue().split(" - ")[1];

        double montant = Double.parseDouble(txtMontant.getText());
        String mode = cbMode.getValue();
        double total = Double.parseDouble(txtTotal.getText());
        double paye = Double.parseDouble(txtPaye.getText());

        String checkSql = "SELECT id, montant_paye FROM paiements WHERE eleve_id=? AND frais_id=? AND annee_id=?";
        String insertSql = "INSERT INTO paiements(eleve_id,frais_id,montant_paye,mode_paiement,annee_id) VALUES(?,?,?,?,?)";
        String updateSql = "UPDATE paiements SET montant_paye = montant_paye + ?, mode_paiement=? WHERE id=?";

        try(Connection conn = Database.connect()){

            conn.setAutoCommit(false);

            int transactionId = -1;
            int paiementId = -1;

            PreparedStatement check = conn.prepareStatement(checkSql);
            check.setInt(1, eleveId);
            check.setInt(2, fraisId);
            check.setInt(3, anneeActiveId);

            ResultSet rsCheck = check.executeQuery();

            if(rsCheck.next()){
                // paiement existe → UPDATE
                paiementId = rsCheck.getInt("id");

                PreparedStatement psUpdate = conn.prepareStatement(updateSql);
                psUpdate.setDouble(1, montant);
                psUpdate.setString(2, mode);
                psUpdate.setInt(3, paiementId);
                psUpdate.executeUpdate();

            }else{
                // paiement n'existe pas → INSERT
                PreparedStatement psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

                psInsert.setInt(1, eleveId);
                psInsert.setInt(2, fraisId);
                psInsert.setDouble(3, montant);
                psInsert.setString(4, mode);
                psInsert.setInt(5, anneeActiveId);

                psInsert.executeUpdate();

                ResultSet rs = psInsert.getGeneratedKeys();
                if(rs.next()){
                    paiementId = rs.getInt(1);
                }
            }

            // ajouter transaction
            transactionId = ajouterTransaction(conn, paiementId, montant, eleveId, mode);

            conn.commit();

            new Alert(Alert.AlertType.INFORMATION,"Paiement enregistré ou mis à jour").show();

            chargerSituation();
            chargerPaiements();
            chargerTransactions();

            genererRecu(transactionId, eleveNom, montant, LocalDate.now(), total, paye + montant, mode);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private int ajouterTransaction(Connection conn, int paiementId, double montant, int eleveId, String mode) throws SQLException {
        String sqlTr = "INSERT INTO transaction_eleve(paiement_id, eleve_id, montant, date_transaction, mode_paiement) VALUES (?,?,?,?,?)";

        PreparedStatement ps = conn.prepareStatement(sqlTr, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, paiementId);
        ps.setInt(2, eleveId);
        ps.setDouble(3, montant);
        ps.setString(4, LocalDate.now().toString());
        ps.setString(5, mode);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        return rs.next() ? rs.getInt(1) : -1;
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
         


    // -------------------- GENERER NUMERO ET RECU --------------------
    private String genererNumeroRecu(int transactionId){
        int annee = LocalDate.now().getYear();
        return String.format("REC-%d-%04d", annee, transactionId);
    }

    private void genererRecu(int transactionId, String eleveNom, double montant, LocalDate datePaiement, double montantTotal, double montantDejaPaye, String mode){
        try {
            String fileName = "Recu_REC-" + transactionId + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new java.io.FileOutputStream(fileName));
            document.open();

            Font titreFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font smallFont = new Font(Font.FontFamily.HELVETICA, 10);

            Paragraph ecole = new Paragraph("LYCÉE PRIVÉ EL MOCTAR KONATÉ", titreFont);
            ecole.setAlignment(Element.ALIGN_CENTER);
            document.add(ecole);

            document.add(new Paragraph("Bamako - Mali", smallFont));
            document.add(new Paragraph("Téléphone : +223 79 70 70 10", smallFont));
            document.add(new Paragraph("Email : contact@elmoctar.com", smallFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("------------------------------------------------------------"));
            document.add(new Paragraph(" "));

            Paragraph titre = new Paragraph("FACTURE", headerFont);
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);
            document.add(new Paragraph("Référence : " + genererNumeroRecu(transactionId), normalFont));
            document.add(new Paragraph("Date : " + datePaiement, normalFont));
            document.add(new Paragraph("Année scolaire : " + comboAnnee.getValue(), normalFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            double montantRestant = Math.max(0, montantTotal - montantDejaPaye);
            String statut = montantRestant == 0 ? "SOLDÉ" : "EN COURS";

            table.addCell("Eleve"); table.addCell(eleveNom);
            table.addCell("Montant total à payer"); table.addCell(String.format("%,.0f FCFA", montantTotal));
            table.addCell("Montant déjà payé"); table.addCell(String.format("%,.0f FCFA", montantDejaPaye));
            table.addCell("Paiement effectué aujourd'hui"); table.addCell(String.format("%,.0f FCFA", montant));
            table.addCell("Montant restant"); table.addCell(String.format("%,.0f FCFA", montantRestant));
            table.addCell("Statut"); table.addCell(statut);
            table.addCell("Mode de paiement"); table.addCell(mode);
            table.addCell("Motif"); table.addCell(cbFrais.getValue());

            document.add(table);

            BarcodeQRCode qrCode = new BarcodeQRCode(
                    "RECU:" + genererNumeroRecu(transactionId) +
                    "|ELEVE:" + eleveNom +
                    "|MONTANT:" + montant +
                    "|DATE:" + datePaiement, 150, 150, null
            );

            Image qrImage = qrCode.getImage();
            qrImage.setAlignment(Element.ALIGN_CENTER);
            document.add(new Paragraph(" "));
            document.add(qrImage);

            document.add(new Paragraph("Signature du responsable :", normalFont));
            document.add(new Paragraph("_______________________________"));
            document.add(new Paragraph("Cachet officiel de l'établissement"));
            document.add(new Paragraph("Ce document tient lieu de justificatif officiel.", smallFont));

            document.close();

            new Alert(Alert.AlertType.INFORMATION, "Reçu généré : " + fileName).show();

        } catch(Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la génération du reçu !").show();
        }
    }

    @FXML
    private void imprimerRecu(){
        new Alert(Alert.AlertType.INFORMATION, "Fonction impression reçu").show();
    }
}