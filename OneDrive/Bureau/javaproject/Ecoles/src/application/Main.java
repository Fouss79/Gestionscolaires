package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

	
    @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	CreateTable.createTables();
       
       
      
    
    	BackupService.backupDatabase();
    	BackupService.sauvegardeSurUSB();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
        AnchorPane root = loader.load();
    
     

        Scene scene = new Scene(root, 1283, 657);
        primaryStage.setTitle("Gestion des Notes");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {

            BackupService.backupDatabase();

            System.out.println("Sauvegarde effectuée avant fermeture");

        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
