package it.emacompany.cookingwiki;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.Image;

/**
 * JavaFX CookingWiki
 */
public class CookingWiki extends Application {

    private static Scene scene;
    
    @Override
    public void start(Stage stage) throws IOException {
        
        scene = new Scene(loadFXML("login"), 1000, 700);
        stage.getIcons().add(new Image("it/emacompany/cookingwiki/img/Logo_img.png"));
        stage.setTitle("CookingWiki");
        stage.setMinHeight(400);
        stage.setMinWidth(640);
        
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();   
        
    }
        
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CookingWiki.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}