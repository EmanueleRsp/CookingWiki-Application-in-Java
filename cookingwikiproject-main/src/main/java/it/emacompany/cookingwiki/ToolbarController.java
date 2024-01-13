package it.emacompany.cookingwiki;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ToolbarController implements Initializable{
    
    private boolean openedMenu = false;
    private boolean openedProfileMenu = false;
    
    @FXML GridPane gridPane;
    @FXML TextField input;
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        
    }
    
    
    @FXML
    private void openMenu() throws IOException {
        
        BorderPane target = (BorderPane) gridPane.getParent();
        
        if(openedProfileMenu && !openedMenu){
            openProfileMenu();
        }
        
        if(!openedMenu){
            FxmlLoader obj = new FxmlLoader();
            Parent view = obj.getPane("menu");
            ScrollPane menu = (ScrollPane) view;
            target.setLeft(menu);
        }else{
            target.setLeft(null);
        }
        
        openedMenu = !openedMenu;
    }
    
    @FXML
    private void goHome() throws IOException {
        CookingWiki.setRoot("home");
    }
    
    @FXML
    private void goSearch(ActionEvent event) throws IOException {
        String ricerca = ((TextField)((HBox)((Button)event.getSource()).getParent()).getChildren().get(0)).getText().trim();
        
        if(ricerca.equals("") || !ricerca.matches("[(\\w)(\\s)àèìòù']*") || ricerca.contains("_"))
            return;
        
        SessionData.getInstance().setTypeS(SearchType.NAME);
        SessionData.getInstance().setRicerca(ricerca);
        CookingWiki.setRoot("search");
    }
    
    @FXML
    private void openProfileMenu() throws IOException {
        
        BorderPane target = (BorderPane) gridPane.getParent();
        
        if(openedMenu && !openedProfileMenu){
            openMenu();
        }
        
        if(!openedProfileMenu){
            FxmlLoader obj = new FxmlLoader();
            Parent view = obj.getPane("profileMenu");
            ScrollPane menu = (ScrollPane) view;
            target.setRight(menu);
        }else{
            target.setRight(null);
        }
        
        openedProfileMenu = !openedProfileMenu;
    }
}