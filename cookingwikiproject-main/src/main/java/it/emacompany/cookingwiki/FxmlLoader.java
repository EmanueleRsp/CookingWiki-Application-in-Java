package it.emacompany.cookingwiki;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FxmlLoader {
    
    private static final Logger logger = LogManager.getLogger(FxmlLoader.class);

    private Parent view;
    
    public Parent getPane(String fileName){
        try{
            logger.trace("Trying to take the resource file.\n" + CookingWiki.class.getResource(fileName + ".fxml"));
            
            FXMLLoader loadMenu = new FXMLLoader(CookingWiki.class.getResource(fileName + ".fxml"));
            view = loadMenu.load();
            logger.trace("Resource file has been took.");
            
        }catch(Exception e){
            logger.warn(e.getMessage());
        }
        
        return view;
    }
    
}
