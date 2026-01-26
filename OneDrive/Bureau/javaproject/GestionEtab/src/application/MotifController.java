package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class MotifController {

    @FXML private TableView<Motif> tableMotif;
    @FXML private TableColumn<Motif, Integer> colId;
    @FXML private TableColumn<Motif, String> colNom;
    @FXML private TableColumn<Motif, String> colType;
    @FXML private TableColumn<Motif, String> colStatut;
    @FXML private TableColumn<Motif, String> colMensuel;


    @FXML private TextField txtNom;
    @FXML private ComboBox<Type> comboType;
    @FXML private ComboBox<Statut> comboStatut;
    @FXML private CheckBox chkMensuel;


    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;

    private ObservableList<Motif> motifs = FXCollections.observableArrayList();
    private ObservableList<Type> types = FXCollections.observableArrayList();
    private ObservableList<Statut> statuts = FXCollections.observableArrayList();

    // JDBC
    private final String URL = "jdbc:mysql://localhost:3306/gestionecole";
    private final String USER = "root";
    private final String PASSWORD = "";

    @FXML
    public void initialize() {
        // Colonnes TableView
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colNom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        colType.setCellValueFactory(data -> {
            Type type = types.stream().filter(t -> t.getId() == data.getValue().getTypeId()).findFirst().orElse(null);
            return new javafx.beans.property.SimpleStringProperty(type != null ? type.getNom() : "");
        });
        colStatut.setCellValueFactory(data -> {
            Statut statut = statuts.stream().filter(s -> s.getId() == data.getValue().getStatutId()).findFirst().orElse(null);
            return new javafx.beans.property.SimpleStringProperty(statut != null ? statut.getNom() : "");
        });
        colMensuel.setCellValueFactory(data -> 
        new javafx.beans.property.SimpleStringProperty(data.getValue().isMensuel() ? "Oui" : "Non")
    );


        // Charger données
        chargerTypes();
        chargerStatuts();
        chargerMotifs();

        // Setup ComboBox Type
        comboType.setItems(types);
        comboType.setConverter(new javafx.util.StringConverter<Type>() {
            @Override
            public String toString(Type type) { return type != null ? type.getNom() : ""; }
            @Override
            public Type fromString(String string) { return types.stream().filter(t -> t.getNom().equals(string)).findFirst().orElse(null); }
        });

        // Setup ComboBox Statut
        comboStatut.setItems(statuts);
        comboStatut.setConverter(new javafx.util.StringConverter<Statut>() {
            @Override
            public String toString(Statut statut) { return statut != null ? statut.getNom() : ""; }
            @Override
            public Statut fromString(String string) { return statuts.stream().filter(s -> s.getNom().equals(string)).findFirst().orElse(null); }
        });

        tableMotif.setItems(motifs);

        tableMotif.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtNom.setText(newSel.getNom());
                comboType.setValue(types.stream().filter(t -> t.getId() == newSel.getTypeId()).findFirst().orElse(null));
                comboStatut.setValue(statuts.stream().filter(s -> s.getId() == newSel.getStatutId()).findFirst().orElse(null));
                chkMensuel.setSelected(newSel.isMensuel());
            }
        });

    }

    // --- Charger les types ---
    private void chargerTypes() {
        types.clear();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM type");
            while (rs.next()) {
                Type t = new Type();
                t.setId(rs.getInt("id"));
                t.setNom(rs.getString("nom"));
                types.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Charger les statuts ---
    private void chargerStatuts() {
        statuts.clear();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM statut");
            while (rs.next()) {
                Statut s = new Statut();
                s.setId(rs.getInt("id"));
                s.setNom(rs.getString("nom"));
                statuts.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Charger les motifs ---
    private void chargerMotifs() {
        motifs.clear();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
        	String sql = "SELECT id, nom, type_id, statut_id, mensuel FROM motif";
        	PreparedStatement stmt = conn.prepareStatement(sql);
        	ResultSet rs = stmt.executeQuery();
        	while (rs.next()) {
        	    Motif m = new Motif();
        	    m.setId(rs.getInt("id"));
        	    m.setNom(rs.getString("nom"));
        	    m.setTypeId(rs.getInt("type_id"));
        	    int statutId = rs.getObject("statut_id") != null ? rs.getInt("statut_id") : 0;
        	    m.setStatutId(statutId);
        	    m.setMensuel(rs.getBoolean("mensuel")); // <-- nouveau champ
        	    motifs.add(m);
        	}

                
                for(Motif mm : motifs) {
                	System.out.println(mm.getStatutId());
                }
            
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Ajouter un motif ---
    @FXML
    private void ajouterMotif(ActionEvent event) {
        String nom = txtNom.getText();
        Type type = comboType.getValue();
        Statut statut = comboStatut.getValue();
        if (type == null) return;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
        	String sql = "INSERT INTO motif (nom, type_id, statut_id, mensuel) VALUES (?, ?, ?, ?)";
        	PreparedStatement stmt = conn.prepareStatement(sql);
        	stmt.setString(1, nom);
        	stmt.setInt(2, type.getId());
        	if (statut != null) stmt.setInt(3, statut.getId());
        	else stmt.setNull(3, java.sql.Types.INTEGER);
        	stmt.setBoolean(4, chkMensuel.isSelected());
        	stmt.executeUpdate();

        } catch (SQLException e) { e.printStackTrace(); }

        chargerMotifs();
        viderChamps();
    }

    // --- Modifier un motif ---
    @FXML
    private void modifierMotif(ActionEvent event) {
        Motif m = tableMotif.getSelectionModel().getSelectedItem();
        if (m == null) return;

        String nom = txtNom.getText();
        Type type = comboType.getValue();
        Statut statut = comboStatut.getValue();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
        	String sql = "UPDATE motif SET nom = ?, type_id = ?, statut_id = ?, mensuel = ? WHERE id = ?";
        	PreparedStatement stmt = conn.prepareStatement(sql);
        	stmt.setString(1, nom);
        	stmt.setInt(2, type.getId());
        	if (statut != null) stmt.setInt(3, statut.getId());
        	else stmt.setNull(3, java.sql.Types.INTEGER);
        	stmt.setBoolean(4, chkMensuel.isSelected());
        	stmt.setInt(5, m.getId());
        	stmt.executeUpdate();

        } catch (SQLException e) { e.printStackTrace(); }

        chargerMotifs();
        viderChamps();
    }

    // --- Supprimer un motif ---
    @FXML
    private void supprimerMotif(ActionEvent event) {
        Motif m = tableMotif.getSelectionModel().getSelectedItem();
        if (m == null) return;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "DELETE FROM motif WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, m.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }

        chargerMotifs();
        viderChamps();
    }

    private void viderChamps() {
        txtNom.clear();
        comboType.setValue(null);
        comboStatut.setValue(null);
    }
}
