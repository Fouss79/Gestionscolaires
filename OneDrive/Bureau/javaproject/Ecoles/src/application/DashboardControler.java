package application;





import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardControler {

    @FXML private Button menuUtilisateur;
    @FXML private Button menuEleve;
    @FXML private Button menuNote;

    public void chargerMenuSelonRole(Utilisateur user){

        String role = user.getRole().getNom();

        // cacher tout
        menuUtilisateur.setVisible(false);
        menuEleve.setVisible(false);
        menuNote.setVisible(false);

        if(role.equals("ADMIN")){
            menuUtilisateur.setVisible(true);
            menuEleve.setVisible(true);
            menuNote.setVisible(true);
        }

        if(role.equals("PROFESSEUR")){
            menuNote.setVisible(true);
        }

        if(role.equals("SECRETAIRE")){
            menuEleve.setVisible(true);
        }
    }
}
