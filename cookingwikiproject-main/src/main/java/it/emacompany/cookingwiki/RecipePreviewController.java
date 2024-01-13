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
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipePreviewController implements Initializable{
    
    private static final Logger logger = LogManager.getLogger(RecipePreviewController.class);
    @FXML private Label titolo;
    @FXML private Label autore;
    @FXML private Label presentazione;
    @FXML private VBox mainBox;
    @FXML private HBox hide;
    @FXML private ImageView full;
    @FXML private ImageView empty;
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        hide.setManaged(false);
    }
    
    @FXML
    private void visualizzaRicetta() throws IOException{
        SessionData.getInstance().setAutore(autore.getText());
        SessionData.getInstance().setRecipe(titolo.getText());
        CookingWiki.setRoot("recipe");
    }
    
    @FXML
    private void eliminaRicetta() throws IOException{
            
        VBox parent = (VBox) mainBox.getParent();
        VBox last = (VBox)parent.getChildren().get((parent.getChildren().size() - 1));
        
        if(((Label)last.getChildren().get(1)).getText().equals(titolo.getText()) && ((Label)((HBox)last.getChildren().get(2)).getChildren().get(2)).getText().equals(autore.getText())){
            if(parent.getChildren().size() == 1){
                SessionData.getInstance().setLastAutore(null);
                SessionData.getInstance().setLastName(null);
            }else{
                VBox newLast = (VBox)parent.getChildren().get((parent.getChildren().size() - 2));
                SessionData.getInstance().setLastAutore(((Label)((HBox)newLast.getChildren().get(2)).getChildren().get(2)).getText());
                SessionData.getInstance().setLastName(((Label)newLast.getChildren().get(1)).getText());
            }
        }
            
        for(int i = 0; i < parent.getChildren().size(); i++){
            VBox elem = (VBox) parent.getChildren().get(i);
            if(((Label)elem.getChildren().get(1)).getText().equals(titolo.getText()) && ((Label)((HBox)elem.getChildren().get(2)).getChildren().get(2)).getText().equals(autore.getText())){
                parent.getChildren().remove(i);
                break;
            }
        }
        
        
        InfoRicetta ir = new InfoRicetta(autore.getText(), retranslate(titolo.getText()));
        Gson rson = new Gson();
        String sir = rson.toJson(ir);
        
        Messaggio m = new Messaggio(SessionData.getInstance().getUserName(), sir);
        Gson mson = new Gson();
            String mser = mson.toJson(m);
        
        URL url = new URL("http://localhost:8080/CookingWiki/Recipes/Remove");

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

        String rs = content.toString();

        logger.debug("Risposta ricevuta: " + rs);
        
    }
    
    @FXML
    private void modifyRicettario() throws IOException{
        if(empty.isVisible())
            add();
        else
            remove();
    }
    
    private String retranslate(String s){
        return s.replaceAll("'", "''").replaceAll("à", "a''").replaceAll("è", "e''").replaceAll("ò", "o''").replaceAll("ì", "i''").replaceAll("ù", "u''");
    }
    
    private void add() throws IOException{
        
        InfoRicetta ir = new InfoRicetta(autore.getText(), retranslate(titolo.getText()));
        Gson rson = new Gson();
        String sir = rson.toJson(ir);
        
        Messaggio m = new Messaggio(SessionData.getInstance().getUserName(), sir);
        Gson mson = new Gson();
            String mser = mson.toJson(m);
        
        URL url = new URL("http://localhost:8080/CookingWiki/Favorites/Add");

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

        String rs = content.toString();

        logger.debug("Risposta ricevuta: " + rs);
            
        if(rs.contains("OK")){
            full.setVisible(true);
            full.setManaged(true);
            empty.setManaged(false);
            empty.setVisible(false);
        }
        
    }
    
    private void remove() throws IOException{
        
        if(SessionData.getInstance().getTypeS() == SearchType.PREF ){
            
            VBox parent = (VBox) mainBox.getParent();
            VBox last = (VBox)parent.getChildren().get((parent.getChildren().size() - 1));
            
            if(((Label)last.getChildren().get(1)).getText().equals(titolo.getText()) && ((Label)((HBox)last.getChildren().get(2)).getChildren().get(2)).getText().equals(autore.getText())){
                if(parent.getChildren().size() == 1){
                    SessionData.getInstance().setLastAutore(null);
                    SessionData.getInstance().setLastName(null);
                }else{
                    VBox newLast = (VBox)parent.getChildren().get((parent.getChildren().size() - 2));
                    SessionData.getInstance().setLastAutore(((Label)((HBox)newLast.getChildren().get(2)).getChildren().get(2)).getText());
                    SessionData.getInstance().setLastName(((Label)newLast.getChildren().get(1)).getText());
                }
            }
            
            for(int i = 0; i < parent.getChildren().size(); i++){
                VBox elem = (VBox) parent.getChildren().get(i);
                if(((Label)elem.getChildren().get(1)).getText().equals(titolo.getText()) && ((Label)((HBox)elem.getChildren().get(2)).getChildren().get(2)).getText().equals(autore.getText())){
                    parent.getChildren().remove(i);
                    break;
                }
            }
            
        }
        
        InfoRicetta ir = new InfoRicetta(autore.getText(), retranslate(titolo.getText()));
        Gson rson = new Gson();
        String sir = rson.toJson(ir);
        
        Messaggio m = new Messaggio(SessionData.getInstance().getUserName(), sir);
        Gson mson = new Gson();
            String mser = mson.toJson(m);
        
        URL url = new URL("http://localhost:8080/CookingWiki/Favorites/Remove");

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

        String rs = content.toString();

        logger.debug("Risposta ricevuta: " + rs);
            
        if(rs.contains("OK")){
            full.setVisible(false);
            full.setManaged(false);
            empty.setManaged(true);
            empty.setVisible(true);
        }
        
    }
}
