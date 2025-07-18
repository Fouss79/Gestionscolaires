package application;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainSceneController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField idField;
    
    @FXML
    private TableColumn<Person, String> idColumn;
    
    @FXML
    private TableView<Person> personTable;

    @FXML
    private TableColumn<Person, String> nameColumn;

    @FXML
    private TableColumn<Person, String> emailColumn;


    private ObservableList<Person> personList = FXCollections.observableArrayList();
	
    protected static  String url = "jdbc:mysql://localhost:3306/agrogroup";
    protected static  String utilisateur = "root";
    protected static  String motDePasse = "";
    
    	
	  
		
	
    
    @FXML
    private void initialize() {
        // Initialisation de la liste avec quelques éléments de test
    	
    	 try (Connection connexion = DriverManager.getConnection(url, utilisateur, motDePasse)){ ;
         String requete= "SELECT  *FROM client";
         
        PreparedStatement selectStatement = connexion.prepareStatement(requete);
           
           ResultSet resultat = selectStatement.executeQuery();
		      while(resultat.next()) {
		    	  String id = resultat.getString("id");
	              String name = resultat.getString("name");
	              String email = resultat.getString("email");
	              
	              
		      
    	
        
      personList.add(new Person(id,name,email));
     
      
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        personTable.setItems(personList);
        for(Person p:personList){
        	System.out.println(p.getName()+" "+p.getEmail());
        
    }}} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}}

    @FXML
    private void create() {
    	String id= null;
        String name = nameField.getText();
        String email = emailField.getText();
        personList.add(new Person(id, name, email));  
       
        	String sql = "INSERT INTO client (id,name, email)VALUE(?,?,?)";
            try (Connection connexion= DriverManager.getConnection(url,utilisateur,motDePasse);
          		  PreparedStatement preparedStatement = connexion.prepareStatement(sql)){
              preparedStatement.setString(1, id);
               preparedStatement.setString(2, name);
               preparedStatement.setString(3, email);
              
              preparedStatement.executeUpdate();
              
              clearFields();
              personTable.refresh();
              System.out.println("Element "+name+" ajouté avec succes");
          
              
      
      } catch (SQLException e1) {
          e1.printStackTrace();
      
		
	}
        	
        	
        }
       
               
    

    @FXML
    private void update() {
        Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) {
            String nam = nameField.getText();
            String mail = emailField.getText();
            String id =  selectedPerson.getId();
            
            
            selectedPerson.setName(nam);
            selectedPerson.setEmail(mail);
            String name=selectedPerson.getName();
            String email=selectedPerson.getEmail();
            
            

            try (Connection connexion = DriverManager.getConnection(url, utilisateur, motDePasse)) {
                String req = "UPDATE client SET name = ?, email = ? WHERE id = ?";
                PreparedStatement updateStatement = connexion.prepareStatement(req);

                updateStatement.setString(1, name);
                updateStatement.setString(2, email);
                updateStatement.setString(3, id);
                

                int nbLM = updateStatement.executeUpdate();
                if (nbLM > 0) {
                    JOptionPane.showMessageDialog(null, "Update successful!");
                    System.out.println("Update successful!");
                } else {
                    System.out.println("No rows updated.");
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            clearFields();
            personTable.refresh();
        } else {
            showAlert("Error", "No person selected for update.");
        }
    }

    @FXML
    private void delete() {
    	 Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
        
        if (selectedPerson != null) {
        	
             String nom=personTable.getSelectionModel().getSelectedItem().getName();
             String mail=personTable.getSelectionModel().getSelectedItem().getEmail();
            personList.remove(selectedPerson);
			 try (Connection connexion = DriverManager.getConnection(url, utilisateur, motDePasse)){
		            String requete= "DELETE FROM client WHERE email=?";
		            try(PreparedStatement preparedStatement=connexion.prepareStatement(requete)){
		                
		                preparedStatement.setString(1,mail);
		           
		                
		                int nbLA = preparedStatement.executeUpdate();
		                if(nbLA>0) {
		                	connexion.close();
		                	showAlert("","L'elemnet supprimer avec succes.");
		                	 //JOptionPane.showMessageDialog(null, "Element"+ nom+" a ete supprimé avec succes");
		                //System.out.println(nbLA+"ligne supprimé");
		                }
		                //else {
		                	//System.out.println("Desolé cet element n'existe pas");}
		                }
		                catch(SQLException e1) {
				            
				                e1.printStackTrace();
				               
				            }
		                
		            }
			 catch(SQLException e1) {
		            System.out.println("bbb");
		                e1.printStackTrace();
		            }
            
            clearFields();
        } else {
        
            showAlert("Error", "No person selected for delete.");
        }
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
