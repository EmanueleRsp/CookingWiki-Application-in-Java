package it.emacompany.cookingwiki;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MenuController{
    
    private static final Logger logger = LogManager.getLogger(MenuController.class);
    
    @FXML
    private void backHome() throws IOException {
        CookingWiki.setRoot("home");
    }
    
    
    @FXML
    private void searchByCategories(MouseEvent event) throws IOException{
        Label target = ((Label) event.getSource());
        SessionData.getInstance().setTypeS(SearchType.TAGS);
        SessionData.getInstance().setRicerca(target.getText());
        CookingWiki.setRoot("search");
    }
    
    
    // Carica la pagina search, che cerca le ultime ricette inserite.
    @FXML
    private void searchLastRecipes() throws IOException{
        SessionData.getInstance().setTypeS(SearchType.LAST_RECIPE);
        CookingWiki.setRoot("search");
    }
    
}
