package it.emacompany.cookingwiki;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SearchController implements Initializable{
    
    private static final Logger logger = LogManager.getLogger(SearchController.class);
    
    // Velocità dello scroll.
    private final double SPEED = 0.001;
    
    // Variabili per calcolare il numero di risultati da visualizzare
    private int dimRisultati = 2;
    
    // Variabile per determinare il tipo di ricerca da effettuare
    private final SearchType st = SessionData.getInstance().getTypeS();

    @FXML    private BorderPane borderPane;
    @FXML    private GridPane rootPane;
    @FXML    private ScrollPane scrollPane;
    @FXML    private VBox contenitore;
    @FXML    private VBox termine;
    @FXML    private Label titolo;
    @FXML    private Label caption;
    @FXML    private Button chargeButton;
    @FXML    private ComboBox numPag;
    
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
        logger.trace(" Impostate.");
        
        termine.setManaged(false);
        SessionData.getInstance().setLastName(null);
        SessionData.getInstance().setLastAutore(null);
        SessionData.getInstance().setLastCheck(0);
        
        ObservableList<String> options = 
        FXCollections.observableArrayList("2","3","5","10","20");
        
        numPag.setItems(options);
        numPag.setPromptText(Integer.toString(SessionData.getInstance().getLastDim()));
        
        // Occorre effettuare la ricerca delle ricette indicate, a seconda dei dati impostati in SessionData
        try{
            charge();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    // Funzione per recuperare le ricette
    @FXML
    private void charge() throws IOException{
            if(numPag.getSelectionModel().isEmpty())
                dimRisultati = Integer.parseInt(numPag.getPromptText());
            else{
                dimRisultati = Integer.parseInt(numPag.getSelectionModel().getSelectedItem().toString());
            }
            switch(st){
                case LAST_RECIPE:
                    ricercaUltime();
                    break;
                case TAGS:
                    ricercaTag(SessionData.getInstance().getRicerca());
                    break;
                case NAME:
                    ricercaNome(SessionData.getInstance().getRicerca());
                    break;
                case SELF:
                    ricercaProdotte(SessionData.getInstance().getUserName());
                    break;
                case PREF:
                    ricercaPref(SessionData.getInstance().getUserName());
                    break;
            }
            
    }
    
    
    // Ricerca in ordine cronologico
    private void ricercaUltime() throws IOException{
        
        if(SessionData.getInstance().getLastName() != null && SessionData.getInstance().getLastCheck() < SessionData.getInstance().getLastDim())
            return;
        
        titolo.setText("Ultime ricette");
        caption.setText("Risultati di ricerca per le");
                
        // Collegamento al server.
        logger.debug("Connessione al servizio...");
        if(SessionData.getInstance().getLastName() != null || SessionData.getInstance().getLastCheck() != 0) 
            SessionData.getInstance().setLastName(SessionData.getInstance().getLastName().replaceAll(" ", "_"));
        URL url = new URL("http://localhost:8080/CookingWiki/Search/LastRecipes?dim=" + dimRisultati + "&lastName=" + SessionData.getInstance().getLastName() + "&lastAutore=" + SessionData.getInstance().getLastAutore());
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        logger.debug("Attesa risposta...");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String response;
        StringBuffer content = new StringBuffer();
        while ((response = in.readLine()) != null) {
            content.append(response);
        }
        in.close();
        
        String result = content.toString();
        logger.debug("Risposta ricevuta: " + result);
        
        Gson gson = new Gson();
        Messaggio m = gson.fromJson(result, Messaggio.class);
        
        SessionData.getInstance().setLastCheck(Integer.parseInt(m.header));
        
        setUp(m.body);
    }
    
    // Ricerca per tag
    private void ricercaTag(String target) throws IOException{
        
        if(SessionData.getInstance().getLastName() != null && SessionData.getInstance().getLastCheck() < SessionData.getInstance().getLastDim())
            return;
        
        titolo.setText(target);
        caption.setText("Risultati della ricerca per tag:");
        // Collegamento al server.
        logger.debug("Connessione al servizio...");
        if(SessionData.getInstance().getLastName() != null || SessionData.getInstance().getLastCheck() != 0) 
            SessionData.getInstance().setLastName(SessionData.getInstance().getLastName().replaceAll(" ", "_"));
        URL url = new URL("http://localhost:8080/CookingWiki/Search/Tag?dim=" + dimRisultati + "&lastName=" + SessionData.getInstance().getLastName() + "&lastAutore=" + SessionData.getInstance().getLastAutore() + "&tag=" + target.replaceAll(" ", "_"));
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        logger.debug("Attesa risposta...");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String response;
        StringBuffer content = new StringBuffer();
        while ((response = in.readLine()) != null) {
            content.append(response);
        }
        in.close();
        
        String result = content.toString();
        logger.debug("Risposta ricevuta: " + result);
        
        Gson gson = new Gson();
        Messaggio m = gson.fromJson(result, Messaggio.class);
        
        SessionData.getInstance().setLastCheck(Integer.parseInt(m.header));
        
        setUp(m.body);
        
    }
    
    // Ricerca per nome
    private void ricercaNome(String target) throws IOException{
        
        if(SessionData.getInstance().getLastName() != null && SessionData.getInstance().getLastCheck() < SessionData.getInstance().getLastDim())
            return;

        titolo.setText(target);
        caption.setText("Ricette contenenti nel titolo:");
        
        SearchByNameData sbnd = new SearchByNameData(dimRisultati, SessionData.getInstance().getLastCheck(), SessionData.getInstance().getLastName(), SessionData.getInstance().getLastAutore(), retranslate(target));
        Gson sson = new Gson();
        String mser = sson.toJson(sbnd);
        
        // Collegamento al server.
        logger.debug("Connessione al servizio...");
        URL url = new URL("http://localhost:8080/CookingWiki/Search/Name");
        
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

        logger.debug("Attesa risposta...");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String response;
        StringBuffer content = new StringBuffer();
        while ((response = in.readLine()) != null) {
            content.append(response);
        }
        in.close();
        
        String result = content.toString();
        logger.debug("Risposta ricevuta: " + result);
        
        Gson gson = new Gson();
        Messaggio m = gson.fromJson(result, Messaggio.class);
        
        SessionData.getInstance().setLastCheck(Integer.parseInt(m.header));
        
        setUp(m.body);
        
    }
    
    // Ricerca ricette caricate
    private void ricercaProdotte(String target) throws IOException{
        
        if(SessionData.getInstance().getLastName() != null && SessionData.getInstance().getLastCheck() < SessionData.getInstance().getLastDim())
            return;

        titolo.setText("Le mie ricette");
        caption.setText("");

        // Collegamento al server.
        logger.debug("Connessione al servizio...");
        if(SessionData.getInstance().getLastName() != null || SessionData.getInstance().getLastCheck() != 0) 
            SessionData.getInstance().setLastName(SessionData.getInstance().getLastName().replaceAll(" ", "_"));
        URL url = new URL("http://localhost:8080/CookingWiki/Search/Made?dim=" + dimRisultati + "&lastName=" + SessionData.getInstance().getLastName() + "&lastAutore=" + SessionData.getInstance().getLastAutore() + "&name=" + target);
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        logger.debug("Attesa risposta...");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String response;
        StringBuffer content = new StringBuffer();
        while ((response = in.readLine()) != null) {
            content.append(response);
        }
        in.close();
        
        String result = content.toString();
        logger.debug("Risposta ricevuta: " + result);
        
        Gson gson = new Gson();
        Messaggio m = gson.fromJson(result, Messaggio.class);
        
        SessionData.getInstance().setLastCheck(Integer.parseInt(m.header));
        
        setUp(m.body);
        
    }
    
    // Carica le ricette salvate tra i preferiti
    private void ricercaPref(String target) throws IOException{
        
        if(SessionData.getInstance().getLastName() != null && SessionData.getInstance().getLastCheck() < SessionData.getInstance().getLastDim())
            return;

        titolo.setText("Il mio ricettario");
        caption.setText("");

        // Collegamento al server.
        logger.debug("Connessione al servizio...");
        if(SessionData.getInstance().getLastName() != null) 
            SessionData.getInstance().setLastName(SessionData.getInstance().getLastName().replaceAll(" ", "_"));
        URL url = new URL("http://localhost:8080/CookingWiki/Favorites/List?dim=" + dimRisultati + "&lastName=" + SessionData.getInstance().getLastName() + "&lastAutore=" + SessionData.getInstance().getLastAutore() + "&name=" + target);
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        logger.debug("Attesa risposta...");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String response;
        StringBuffer content = new StringBuffer();
        while ((response = in.readLine()) != null) {
            content.append(response);
        }
        in.close();
        
        String result = content.toString();
        logger.debug("Risposta ricevuta: " + result);
        
        Gson gson = new Gson();
        Messaggio m = gson.fromJson(result, Messaggio.class);
        
        SessionData.getInstance().setLastCheck(Integer.parseInt(m.header));
        
        setUp(m.body);
        
    }
    
    // Funzioni per facilitare lo scambio di caratteri accentati.
    private String retranslate(String s){
        return s.replaceAll("'", "''").replaceAll("à", "a''").replaceAll("è", "e''").replaceAll("ò", "o''").replaceAll("ì", "i''").replaceAll("ù", "u''");
    }
    private String translate(String s){
        return s.replaceAll("''", "'").replaceAll("a'", "à").replaceAll("e'", "è").replaceAll("o'", "ò").replaceAll("i'", "ì").replaceAll("u'", "ù");
    }
    
    // Inizializza gli elementi della pagina
    private void setUp(String result) throws IOException{
        
        SessionData.getInstance().setLastDim(dimRisultati);
        
        if(SessionData.getInstance().getLastCheck() < dimRisultati){
            eliminaPulsante();
            if(SessionData.getInstance().getLastName() == null && SessionData.getInstance().getLastCheck() == 0)
                paginaVuota();
        }
        
        if(SessionData.getInstance().getLastCheck() == 0)
            return;
        
        Gson gson = new Gson();
        JsonArray ricette = gson.fromJson(result, JsonArray.class);
        
        for(int i = 0; i < ricette.size(); i++){
            Parent view;
                
            JsonObject job = ricette.get(i).getAsJsonObject();
            FxmlLoader obj = new FxmlLoader(); 
            if(job.get("autore").toString().replaceAll("\"", "").replaceAll("''", "'").equals(SessionData.getInstance().getUserName())){
                view = obj.getPane("myRecipePreview");
            }else{
                view = obj.getPane("recipePreview");
            }
            
            VBox visual = (VBox) view;
            
            ((Label)visual.getChildren().get(1)).setText(translate(job.get("nome").toString()).replaceAll("\"", ""));
            ((Label)((HBox)visual.getChildren().get(2)).getChildren().get(2)).setText(job.get("autore").toString().replaceAll("\"", "").replaceAll("''", "'"));
            ((Label)visual.getChildren().get(3)).setText(translate(job.get("presentazione").toString()).replaceAll("\"", ""));
                        
            contenitore.getChildren().add(visual);
            
            InfoRicetta ir = new InfoRicetta(job.get("autore").toString(), job.get("nome").toString());
            Gson rson = new Gson();
            String sir = rson.toJson(ir);
            
            Messaggio m = new Messaggio(SessionData.getInstance().getUserName(), sir);
            Gson mson = new Gson();
            String mser = mson.toJson(m);
            
            URL url = new URL("http://localhost:8080/CookingWiki/Favorites/Get");

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
                ImageView tg = ((ImageView)((HBox)visual.getChildren().get(0)).getChildren().get(1));
                tg.setVisible(false);
                tg.setManaged(false);
            }else{
                ImageView tg = ((ImageView)((HBox)visual.getChildren().get(0)).getChildren().get(0));
                tg.setVisible(false);
                tg.setManaged(false);
            }
            
        }
        
        JsonObject last = ricette.get((ricette.size() - 1)).getAsJsonObject();
        SessionData.getInstance().setLastName(last.get("nome").toString().replaceAll("\"", ""));
        SessionData.getInstance().setLastAutore(last.get("autore").toString().replaceAll("\"", ""));
    }
    
    
    private void eliminaPulsante(){
        chargeButton.setDisable(true);
        chargeButton.setVisible(false);
        chargeButton.setManaged(false);
        termine.setManaged(true);
        termine.setVisible(true);
    }
    
    
    private void paginaVuota(){
        
        FxmlLoader obj = new FxmlLoader();
        Parent view = obj.getPane("emptyPage");
        HBox visual = (HBox) view;
        
        Label sottotitolo = (Label)((VBox)visual.getChildren().get(1)).getChildren().get(1);
        Label testo = (Label)((VBox)visual.getChildren().get(1)).getChildren().get(2);
        
        if(SessionData.getInstance().getTypeS() == SearchType.SELF){
            sottotitolo.setText("Non hai ancora caricato alcuna ricetta :(");
            testo.setText("Per caricare la tua prima ricetta clicca sull'apposita sezione nel menù a destra.");
        }else if(SessionData.getInstance().getTypeS() == SearchType.PREF){
            sottotitolo.setText("Il tuo ricettario è vuoto :(");
            testo.setText("Se trovi una ricetta che ti piace nella pagina di ricerca, puoi salvarla cliccando con il tasto destro sul titolo.");
        }
        
        contenitore.getChildren().add(visual);
        
    }
}