package it.emacompany.cookingwiki;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ProfileMenuController implements Initializable{
    
    @FXML private Label userName;
    @FXML HBox checkList;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        userName.setText(SessionData.getInstance().getUserName());
        checkList.setManaged(false);
    }
    
    @FXML
    private void goRecipesLists() throws IOException {
        SessionData.getInstance().setTypeS(SearchType.PREF);
        CookingWiki.setRoot("search");        
    }
    
    @FXML
    private void goSelfMade() throws IOException {
        SessionData.getInstance().setTypeS(SearchType.SELF);
        CookingWiki.setRoot("search");        
    }
       
    @FXML
    private void closeSession() throws IOException {
        CookingWiki.setRoot("login");        
    }
    
    @FXML
    private void nuovaRicetta() throws IOException{
        CookingWiki.setRoot("upload");        
    }
}
