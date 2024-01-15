package it.emacompany.cookingwiki;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HomeController implements Initializable{
    
    private static final Logger logger = LogManager.getLogger(HomeController.class);
    
    // Velocità dello scroll.
    private final double SPEED = 0.004;

    @FXML    private BorderPane borderPane;
    @FXML    private GridPane rootPane;
    @FXML    private ScrollPane scrollPane;
    @FXML    private ScrollPane recipeScroll;
    @FXML    private VBox contenitore;
    
    @FXML    private Label titolo1;
    @FXML    private Label presentazione1;
    @FXML    private Label ingredienti1;
    @FXML    private Label autore1;
    @FXML    private Label titolo2;
    @FXML    private Label presentazione2;
    @FXML    private Label ingredienti2;
    @FXML    private Label autore2;
    @FXML    private Label titolo3;
    @FXML    private Label presentazione3;
    @FXML    private Label ingredienti3;
    @FXML    private Label autore3;
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        
        logger.trace("Carico toolbar...");
        // Caricamento della toolbar.
        FxmlLoader obj = new FxmlLoader();
        Parent view = obj.getPane("toolbar");
        GridPane toolbar = (GridPane) view;
        borderPane = (BorderPane) rootPane.getChildren().get(0);
        borderPane.setTop(toolbar);
        logger.trace(" Caricata.");
        
        logger.trace("Imposto velocità scroll...");
        // Impostazione velocità dello scroll.
        scrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
            double deltaX = scrollEvent.getDeltaX() * SPEED;
            scrollPane.setHvalue(scrollPane.getHvalue() - deltaX);
        });
        recipeScroll.getContent().setOnScroll(scrollEvent -> {
            scrollEvent.consume();
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
            double deltaX = scrollEvent.getDeltaX() * SPEED;
            scrollPane.setHvalue(scrollPane.getHvalue() - deltaX);
        });
        logger.trace(" Impostate.");
        
        try {
            // Caricamento ultime ricette.
            inizializzaUltimeRicette();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    private String translate(String s){
        return s.replaceAll("''", "'").replaceAll("a'", "à").replaceAll("e'", "è").replaceAll("o'", "ò").replaceAll("i'", "ì").replaceAll("u'", "ù");
    }
    
    private void inizializzaUltimeRicette() throws IOException{
        
        // Recupero info dal server.
        logger.debug("Connessione al servizio...");
        URL url = new URL("http://localhost:8080/CookingWiki/Access/Home");
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        logger.trace("Attesa risposta...");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String response;
        StringBuffer content = new StringBuffer();
        while ((response = in.readLine()) != null) {
            content.append(response);
        }
        in.close();
        
        logger.trace("Risposta ricevuta: " + content);
        
        Gson sson = new Gson();
        Messaggio m = sson.fromJson(content.toString(), Messaggio.class);
        
        // Setto gli elementi della pagina
        Gson gson = new Gson();
        JsonArray ricette = gson.fromJson(m.body, JsonElement.class).getAsJsonArray();
        
        if(Integer.parseInt(m.header) == 0){
            logger.debug("Pagina inizializzata");
            return;
        }
        
        JsonObject r1 = ricette.get(0).getAsJsonObject();
        titolo1.setText(translate(r1.get("nome").getAsString()));
        presentazione1.setText(translate(r1.get("presentazione").getAsString()));
        autore1.setText(translate(r1.get("autore").getAsString()));
        ingredienti1.setText("Difficoltà: " + r1.get("difficolta").getAsString() + 
                             "\nPreparazione: " + r1.get("preparazione").getAsString()+ " minuti" +
                             "\nCottura: " + r1.get("cottura").getAsString()+ " minuti" +
                             "\nDosi: " + r1.get("dosi").getAsString() +
                             "\nCosto: " + r1.get("costo").getAsString());
        
        if(Integer.parseInt(m.header) == 1){
            logger.debug("Pagina inizializzata");
            return;
        }
        
        JsonObject r2 = ricette.get(1).getAsJsonObject();
        titolo2.setText(translate(r2.get("nome").getAsString()));
        presentazione2.setText(translate(r2.get("presentazione").getAsString()));
        autore2.setText(translate(r2.get("autore").getAsString()));
        ingredienti2.setText("Difficoltà: " + r2.get("difficolta").getAsString() + 
                             "\nPreparazione: " + r2.get("preparazione").getAsString()+ " minuti" +
                             "\nCottura: " + r2.get("cottura").getAsString()+ " minuti" +
                             "\nDosi: " + r2.get("dosi").getAsString() +
                             "\nCosto: " + r2.get("costo").getAsString());
        
        if(Integer.parseInt(m.header) == 2){
            logger.debug("Pagina inizializzata");
            return;
        }
        
        JsonObject r3 = ricette.get(2).getAsJsonObject();
        titolo3.setText(translate(r3.get("nome").getAsString()));
        presentazione3.setText(translate(r3.get("presentazione").getAsString()));
        autore3.setText(translate(r3.get("autore").getAsString()));
        ingredienti3.setText("Difficoltà: " + r3.get("difficolta").getAsString() + 
                             "\nPreparazione: " + r3.get("preparazione").getAsString()+ " minuti" +
                             "\nCottura: " + r3.get("cottura").getAsString()+ " minuti" +
                             "\nDosi: " + r3.get("dosi").getAsString() +
                             "\nCosto: " + r3.get("costo").getAsString());
        
        logger.debug("Pagina inizializzata");
    }
    
    @FXML
    private void nextTris() throws IOException {
        if(recipeScroll.getHvalue() < 0.5)
            recipeScroll.setHvalue(0.5);
        else if(recipeScroll.getHvalue() >= 0.5)
            recipeScroll.setHvalue(1);
    }
    
    @FXML
    private void previousTris() throws IOException {
        if(recipeScroll.getHvalue() > 0.5)
            recipeScroll.setHvalue(0.5);
        else if(recipeScroll.getHvalue() <= 0.5)
            recipeScroll.setHvalue(0);
    }
    
    @FXML
    private void searchByCategory(MouseEvent event) throws IOException{
        
        contenitore.setDisable(true);
        
        Label target = ((Label) event.getSource());
        String category = target.getText();
        SessionData.getInstance().setTypeS(SearchType.TAGS);
        SessionData.getInstance().setRicerca(category);
        CookingWiki.setRoot("search");
    }
    
    // Visualizza la ricetta cliccata
    @FXML
    private void visualizzaRicetta(MouseEvent event) throws IOException{
        
        contenitore.setDisable(true);
        
        Label titolo = (Label)((AnchorPane)((AnchorPane) event.getSource()).getChildren().get(0)).getChildren().get(0);
        Label autore = (Label)((HBox)((AnchorPane)((AnchorPane) event.getSource()).getChildren().get(0)).getChildren().get(2)).getChildren().get(1);
        visualizzaR(titolo.getText(), autore.getText());
    }
    
    private void visualizzaR(String text, String autore) throws IOException{
        logger.debug(text);
        logger.debug(autore);
        
        SessionData.getInstance().setRecipe(text);
        SessionData.getInstance().setAutore(autore);
        CookingWiki.setRoot("recipe");
    }
       
}