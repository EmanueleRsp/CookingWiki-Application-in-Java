package it.emacompany.cookingwiki;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UploadController implements Initializable{
    
    private static final Logger logger = LogManager.getLogger(UploadController.class);
    private final int WAIT_MILLISEC = 1500;

    // Velocità dello scroll.
    private final double SPEED = 0.001;
    
    // Numero di ingredienti e step della ricetta
    private int ingr = 0;
    private int steps = 0;
    
    // Lista dei tag della ricetta
    List<String> tags = new ArrayList<String>();
    
    @FXML VBox resultBox;
    @FXML HBox escBox;
    @FXML ImageView closeN;
    
    @FXML Label titleResult;
    @FXML Label contentResult;

    @FXML    private BorderPane borderPane;
    @FXML    private GridPane rootPane;
    @FXML    private ScrollPane scrollPane;
    
    @FXML    private TextField recipeTitle;
    
    @FXML    private ComboBox difficoltaLevel;
    @FXML    private TextField preparazioneLabel;
    @FXML    private TextField cotturaLabel;
    @FXML    private TextField numDosiLabel;
    @FXML    private TextField costoLabel;
    
    @FXML    private TextArea presentationText;
    @FXML    private VBox ingredientBox;
    @FXML    private VBox stepBox;
    
    @FXML    private TextArea conservationText;
    @FXML    private TextArea suggestText;
    
    @FXML    private Button uploadButton;
    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        resultBox.setManaged(false);
        escBox.setManaged(false);
        
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
        logger.trace(" Impostata.");
        
        // Inserisco i label del primo ingrediente e del primo step.
        FxmlLoader fxItem = new FxmlLoader();
        Parent item = fxItem.getPane("createIngredientItem");
        HBox ingrediente = (HBox) item;
        ingredientBox.getChildren().add(ingrediente);
        ingr++;
        
        FxmlLoader fxStep = new FxmlLoader();
        Parent step = fxStep.getPane("createStepItem");
        VBox passo = (VBox) step;
        stepBox.getChildren().add(passo);
        steps++;
        
        // Inizializzo gli items del livello di difficoltà.
        difficoltaLevel.setItems(RecipeLevel.getOptions());
        
        
    }
    
    // Modifica la lista dei tags.
    @FXML
    private void modifyTagList(MouseEvent event) throws IOException{
        
        CheckBox target = (CheckBox) event.getSource();
        
        if(target.isSelected())
            tags.add(target.getText().replaceAll("'", "''"));
        else
            tags.remove(target.getText().replaceAll("'", "''"));
        
        logger.trace("Tags: " + tags);
        
    }
    
    // Nuovo ingrediente.
    @FXML
    private void addIngredient() throws IOException{
        FxmlLoader fxItem = new FxmlLoader();
        Parent item = fxItem.getPane("createIngredientItem");
        HBox ingrediente = (HBox) item;
        ingredientBox.getChildren().add(ingrediente);
        ingr++;
    }

    // Eliminino l'ultimo ingrediente.
    @FXML
    private void removeIngredient() throws IOException{
        if(ingr == 1)
            return;
        ingredientBox.getChildren().remove(--ingr);
    }
    
    // Nuovo passo della ricetta.
    @FXML
    private void addStep() throws IOException{
        FxmlLoader fxStep = new FxmlLoader();
        Parent step = fxStep.getPane("createStepItem");
        VBox passo = (VBox) step;
        ((Label)((VBox)(passo.getChildren().get(0))).getChildren().get(0)).setText(Integer.toString(++steps));
        stepBox.getChildren().add(passo);
    }

    
    // Rimuovo l'ultimo passo della ricetta.
    @FXML
    private void removeStep() throws IOException{
        if(steps == 1)
            return;
        stepBox.getChildren().remove(--steps);
        
    }
    
    
    // Funzione che tenta di caricare la ricetta.
    @FXML
    private void tryUpload() throws IOException{
        
        // Disabilitare tutti gli input
        
        recipeTitle.setDisable(true);
        
        difficoltaLevel.setDisable(true);
        preparazioneLabel.setDisable(true);
        cotturaLabel.setDisable(true);
        numDosiLabel.setDisable(true);
        costoLabel.setDisable(true);
        
        presentationText.setDisable(true);
        ingredientBox.setDisable(true);
        stepBox.setDisable(true);
        
        conservationText.setDisable(true);
        suggestText.setDisable(true);
        
        uploadButton.setDisable(true);
        
        closeNotify();
        
        Task task = new Task<Void>(){
                        
                        @Override
                        public Void call() throws IOException{
                            tryUploadTask();
                            return null;
                        }
            
                    };
        
        new Thread(task).start(); 
        
    }
    
    // Corpo del task che verifica le info inserite tramite upload().
    private void tryUploadTask() throws IOException {
        
        String result = upload();
        if(result.equals("OK"))
            setSuccess();
        else
            setFail(result);
        
    }
    
    private String translate(String s){
        return s.replaceAll("'", "''").replaceAll("é", "e''").replaceAll("à", "a''").replaceAll("è", "e''").replaceAll("ì", "i''").replaceAll("ò", "o''").replaceAll("ù", "u''");
    }
    // Si collega al server e prova fre upload della ricetta.
    // COn uno stesso profilo non è possibile inserire ricette con stesso titolo.
    // Ritorna OK se ha successo, altrimenti l'errore.
    private String upload() throws IOException {
        
        // Fase di controllo validità campi.
        
        if(recipeTitle.getText().isBlank() || !recipeTitle.getText().matches("[(\\w)(\\s).;,!?°£$%&€àòèìòù'()]*")){
            String err = "Titolo della ricetta non inserito o sono stati utilizzati caratteri non consentiti.";
            logger.debug(err);
            return err;
        }
        if(recipeTitle.getText().trim().length() > 30 || recipeTitle.getText().contains("_")){
            String err = "Il titolo della ricetta deve avere al massimo 30 caratteri e non può contenere il carattere \"_\".";
            logger.debug(err);
            return err;
        }
        String titolo = translate(recipeTitle.getText().trim());
                
        if(difficoltaLevel.getSelectionModel().isEmpty()){
            String err = "Selezionare un livello di difficoltà della ricetta.";
            logger.debug(err);
            return err;
        }
        String diff = difficoltaLevel.getSelectionModel().getSelectedItem().toString();

        if(preparazioneLabel.getText().isBlank() || !preparazioneLabel.getText().matches("(\\d)*")){
            String err = "Tempo di preparazione non inserito (inserire solo valori numerici).";
            logger.debug(err);
            return err;
        }
        String prep = preparazioneLabel.getText();
        
        if(cotturaLabel.getText().isBlank() || !cotturaLabel.getText().matches("(\\d)*")){
            String err = "Tempo di cottura non inserito (inserire solo valori numerici).";
            logger.debug(err);
            return err;
        }
        String cot = cotturaLabel.getText();

        if(numDosiLabel.getText().isBlank() || !numDosiLabel.getText().matches("[(\\w)(\\s)()]*")){
            String err = "Numero di dosi non inserito o sono stati utilizzati caratteri non consentiti.";
            logger.debug(err);
            return err;
        }
        String dosi = numDosiLabel.getText().trim();

        if(costoLabel.getText().isBlank()|| !numDosiLabel.getText().matches("[(\\w)(\\s)£$€]*")){
            String err = "Costo non inserito o sono stati utilizzati caratteri non consentiti.";
            logger.debug(err);
            return err;
        }
        String costo = costoLabel.getText().replaceAll("'", "''").trim();
        
        if(presentationText.getText().isBlank() || !presentationText.getText().matches("[(\\w)(\\s).;,!?°£$%&€àòèìòù'()]*")){
            String err = "Presentazione non inserita (o contiene caratteri non consentiti).";
            logger.debug(err);
            return err;
        }
        String pres = translate(presentationText.getText().trim());
        
        List<Ingrediente> listaIngr = new ArrayList<Ingrediente>();
        for(int i = 0; i < ingr; i++){
            HBox tg = (HBox) ingredientBox.getChildren().get(i);
            TextField name = (TextField)(tg.getChildren().get(1));
            TextField qnt = (TextField)(tg.getChildren().get(3));
            if(name.getText().isBlank() || qnt.getText().isBlank() || !name.getText().matches("[(\\w)(\\s).;,!?°£$%&€àòèìòù'()]*") || !qnt.getText().matches("[(\\w)(\\s).;,!?°£$%&€àòèìòù'()]*")){
                String err = "Uno o più campi ingrediente non sono compilati correttamente (o contengono caratteri vietati).";
                logger.debug(err);
                return err;
            }
            listaIngr.add(new Ingrediente(translate(name.getText().trim()), translate(qnt.getText().trim())));
        }
        
        List<String> listaStep = new ArrayList<String>();
        for(int i = 0; i < steps; i++){
            VBox tg = (VBox) stepBox.getChildren().get(i);
            TextArea name = (TextArea)(tg.getChildren().get(2));
            if(name.getText().isBlank() || !name.getText().matches("[(\\w)(\\s).;,!?°£$%&€àòèìòù'()]*")){
                String err = "Uno o più passaggi risultano vuoti o contengono caratteri non consentiti.";
                logger.debug(err);
                return err;
            }
            listaStep.add(translate(name.getText().trim()));
        }
        
        if(conservationText.getText().isBlank() || !conservationText.getText().matches("[(\\w)(\\s).;,!?°£$%&€àòèìòù'()]*")){
            String err = "Modalità di conservazione non inserita o contengono caratteri non consentiti.";
            logger.debug(err);
            return err;
        }
        String cons = translate(conservationText.getText().trim());

        if(suggestText.getText().isBlank() || !suggestText.getText().matches("[(\\w)(\\s).;,!?°£$%&€àòèìòù'()]*")){
            String err = "Consigli non inseriti o contengono caratteri non consentiti.";
            logger.debug(err);
            return err;
        }
        String sugg = translate(suggestText.getText().trim());

        if(tags.isEmpty()){
            String err = "Selezionare almeno un tag.";
            logger.debug(err);
            return err;
        }

        
        // Serializzazione dei dati
        logger.debug("Serializzazione dei dati...");
        
        InfoRicetta ir = new InfoRicetta(
                SessionData.getInstance().getUserName(),
                titolo,
                diff,
                prep,
                cot,
                dosi,
                costo,
                pres,
                listaIngr,
                listaStep,
                cons,
                sugg,
                tags
        );
        Gson gson = new Gson();
        String serInfo = gson.toJson(ir);
        logger.debug("Dati serializzati:\n" + serInfo);        
                
        // Collegamento al server
        logger.debug("Connessione al servizio...");
        URL url = new URL("http://localhost:8080/CookingWiki/Recipes/Upload");
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        
        // Preparazione dei dati
        String param = "serInfo=" + serInfo;
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
        String response;
        StringBuilder content = new StringBuilder();
        
        // Lettura risposta
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while ((response = in.readLine()) != null)
            content.append(response);
              
        logger.debug("Risposta ricevuta: " + content);
        
        if(content.toString() == null)
            return "Nessuna risposta dal server.";
        
        if(content.toString().equals("OK"))
            return "OK";
        
        return content.toString().replaceAll("Ã¨", "è").replaceAll("Ã", "à");
    }
    
    
    // Imposta notifica di successo,
    // dopo WAIT_MILLISEC millisecondi torna alla pagina di login.
    private void setSuccess() throws IOException {
        resultBox.setManaged(true);
        
        uploadButton.setDisable(true);
        uploadButton.setVisible(false);
        uploadButton.setManaged(false);
        
        closeN.setDisable(true);
        closeN.setVisible(false);
        
        Platform.runLater(
            () -> {
                titleResult.setText("Upload effettuato!");
                contentResult.setText("Sarai rediretto alla home.");
            }
        );

        resultBox.getStylesheets().clear();
        resultBox.getStylesheets().add("/styles/successNotify.css");
        resultBox.setDisable(false);
        resultBox.setVisible(true);
        
        Task task = new Task<Void>(){
                        
                        @Override
                        public Void call() throws IOException{
                            home();
                            return null;
                        }
            
                    };
        
        new Thread(task).start(); 
        
    }
    
    // Corpo del task per tornare alla home.
    private void home() throws IOException {
        
        try {
            Thread.currentThread().sleep(WAIT_MILLISEC);
        } catch (InterruptedException ex) {
            System.out.println("Interrupted Exception nell'attesa della redirezione\n al login da recupero password:\n" + ex.getMessage());
        }
        
        CookingWiki.setRoot("home");
        
    }
    
    // Imposta notifica di fail.
    private void setFail(String whyFail) throws IOException {
        
        // Abilito nuovamente gli input
        recipeTitle.setDisable(false);
        
        difficoltaLevel.setDisable(false);
        preparazioneLabel.setDisable(false);
        cotturaLabel.setDisable(false);
        numDosiLabel.setDisable(false);
        costoLabel.setDisable(false);
        
        presentationText.setDisable(false);
        ingredientBox.setDisable(false);
        stepBox.setDisable(false);
        
        conservationText.setDisable(false);
        suggestText.setDisable(false);
        
        uploadButton.setDisable(false);
        
        resultBox.setManaged(true);
        
        Platform.runLater(
            () -> {
                titleResult.setText("Attenzione!");
                contentResult.setText(whyFail);
            }
        );
        
        closeN.setDisable(false);
        closeN.setVisible(true);

        resultBox.getStylesheets().clear();
        resultBox.getStylesheets().add("/styles/failNotify.css");
        resultBox.setDisable(false);
        resultBox.setVisible(true);
    }
    
    
    // Funzione per chiudere la notifica.
    @FXML
    private void closeNotify() throws IOException {
        
        uploadButton.setDisable(false);
        
        closeN.setDisable(true);

        resultBox.setDisable(true);
        resultBox.setVisible(false);
        resultBox.setManaged(false);
        
    }
    
}