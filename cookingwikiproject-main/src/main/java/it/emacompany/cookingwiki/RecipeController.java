package it.emacompany.cookingwiki;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeController implements Initializable{
    
    private static final Logger logger = LogManager.getLogger(RecipeController.class);
    
    // Velocita dello scroll.
    private final double SPEED = 0.001;

    @FXML    private BorderPane borderPane;
    @FXML    private GridPane rootPane;
    @FXML    private ScrollPane scrollPane;
    
    @FXML    private Label titolo;
    
    @FXML    private Label difficolta;
    @FXML    private Label preparazione;
    @FXML    private Label cottura;
    @FXML    private Label dosi;
    @FXML    private Label costo;
    
    @FXML    private Label presentazione;
    @FXML    private VBox ingredientiBox;
    @FXML    private VBox passaggiBox;
    
    @FXML    private Label conservazione;
    @FXML    private Label suggerimenti;
    @FXML    private Label tags;
    
    
    
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
        
        logger.trace("Imposto velocita scroll...");
        // Impostazione velocita dello scroll.
        scrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
            double deltaX = scrollEvent.getDeltaX() * SPEED;
            scrollPane.setHvalue(scrollPane.getHvalue() - deltaX);
        });
        logger.trace(" Impostata.");
        
        try {
            // Inizializzare la pagina con gli elementi della ricetta.
            visualizzaRicetta(SessionData.getInstance().getRecipe(), SessionData.getInstance().getAutore());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void visualizzaRicetta(String titolo, String autore) throws IOException{
        
        Messaggio m = new Messaggio(retranslate(titolo), autore);
        Gson mson = new Gson();
        String mser = mson.toJson(m);
        
        // Recupero info dal server.
        logger.debug("Connessione al servizio...");
        URL url = new URL("http://localhost:8080/CookingWiki/Recipes/Get");
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        
        // Preparazione dei dati
        String param = "mser=" + mser;
        byte[] postData = param.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        con.setInstanceFollowRedirects( false );
        con.setRequestMethod( "POST" );
        con.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty( "charset", "utf-8");
        con.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        con.setUseCaches( false );
        
        // Invio dei dati
        DataOutputStream out = new DataOutputStream( con.getOutputStream());
        out.write( postData );
        logger.debug("Dati inviati.");

        logger.trace("Attesa risposta...");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String response;
        StringBuffer content = new StringBuffer();
        while ((response = in.readLine()) != null) {
            content.append(response);
        }
        in.close();
        
        String result = content.toString();
        
        logger.debug("Risposta ricevuta: " + result);       
        
        
        // Setto gli elementi della pagina
        setUp(result);
    }
    
    private String translate(String s){
        return s.replaceAll("''", "'").replaceAll("a'", "à").replaceAll("e'", "è").replaceAll("o'", "ò").replaceAll("i'", "ì").replaceAll("u'", "ù");
    }
    
    private String retranslate(String s){
        return s.replaceAll("'", "''").replaceAll("à", "a''").replaceAll("è", "e''").replaceAll("ò", "o''").replaceAll("ì", "i''").replaceAll("ù", "u''");
    }
    
    // Inizializza gli elementi della pagina
    private void setUp(String result){
        Gson gson = new Gson();
        Messaggio m = gson.fromJson(result, Messaggio.class);
        
        if(m.header.equals("FAIL")){
            titolo.setText("Spiacenti, questa ricetta non è disponibile :(");
            return;
        }
        
        Gson rson = new Gson();
        InfoRicetta ir = rson.fromJson(m.body, InfoRicetta.class);
        
        titolo.setText(translate(ir.titolo));
        difficolta.setText(ir.difficolta);
        preparazione.setText(translate(ir.preparazione));
        cottura.setText(translate(ir.cottura));
        dosi.setText(translate(ir.numeroDosi));
        costo.setText(translate(ir.costo));
        presentazione.setText(translate(ir.presentazione));
        conservazione.setText(translate(ir.conservazione));
        suggerimenti.setText(translate(ir.suggerimenti));
        tags.setText(translate(ir.tags.toString()));
        
        // Ingredienti
        for(int i = 0; i < ir.ingredienti.size(); i++){
            Ingrediente ing = ir.ingredienti.get(i);
            FxmlLoader fxItem = new FxmlLoader();
            Parent item = fxItem.getPane("ingredientItem");
            HBox ingrediente = (HBox) item;
            ((Label)ingrediente.getChildren().get(1)).setText(translate(ing.nome));
            ((Label)ingrediente.getChildren().get(3)).setText(translate(ing.quantita));
            ingredientiBox.getChildren().add(ingrediente);
        }
        
        // Steps
        for(int i = 0; i < ir.passi.size(); i++){
            String st = ir.passi.get(i);
            FxmlLoader fxItem = new FxmlLoader();
            Parent item = fxItem.getPane("stepItem");
            VBox step = (VBox) item;
            ((Text)((HBox)step.getChildren().get(1)).getChildren().get(0)).setText(translate(st));
            ((Label)((VBox)step.getChildren().get(0)).getChildren().get(0)).setText(Integer.toString(i + 1));
            passaggiBox.getChildren().add(step);
        }
        
        logger.debug("Pagina inizializzata!");
    }
}