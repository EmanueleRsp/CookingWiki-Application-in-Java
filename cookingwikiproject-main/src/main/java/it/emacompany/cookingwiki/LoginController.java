package it.emacompany.cookingwiki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginController implements Initializable{
    
    private static final Logger logger = LogManager.getLogger(LoginController.class);
    
    private final int WAIT_MILLISEC = 1300;
    
    private final String titleSuccess = "Login effettuato!";
    private final String textSuccess = "Sarai rediretto alla home."; 

    private final String titleFail = "Attenzione!"; 
        
    @FXML private VBox resultBox;
    @FXML private HBox escBox;
    @FXML private ImageView closeN;
    
    @FXML private Label titleResult;
    @FXML private Label contentResult;
    
    @FXML private VBox resultBox1;
    @FXML private VBox settingBox;
    @FXML private HBox escBox1;
    @FXML private ImageView closeN1;
    
    @FXML private Label titleResult1;
    @FXML private Label contentResult1;
    
    @FXML private Button loginButton;
    @FXML private Button creaButton;
    @FXML private Button popolaButton;
    
    @FXML private Hyperlink pswHyp;
    @FXML private Hyperlink regHyp;
    
    @FXML private TextField nomeUsr;
    @FXML private PasswordField psw;
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        resultBox.setManaged(false);
        escBox.setManaged(false);
        escBox1.setManaged(false);
        
    }
    
    
    // Reindirizza alla pagina di registrazione.
    @FXML
    private void goSignIn() throws IOException{
        CookingWiki.setRoot("register");        
    }
    
    // Reindirizza alla pagina per il cambio password.
    @FXML
    private void goChangePsw() throws IOException{
        CookingWiki.setRoot("changePsw");        
    }
    
    // Esamina i dati e se validi si accede alla home.
    @FXML
    private void tryLogIn() throws IOException {
        
        disabilitaInput();
        
        Task task = new Task<Void>(){
                        
                        @Override
                        public Void call() throws IOException{
                            tryLogInTask();
                            return null;
                        }
            
                    };
        
        new Thread(task).start();   
    }
    
    // Corpo del task che verifica le info inserite tramite logIn().
    @FXML
    private void tryLogInTask() throws IOException {
        String rs = logIn();
        if(rs.equals("OK"))
            setSuccess();
        else
            setFail();
        
    }
    
    // Si collega al server e verifica le credenziali utente.
    // Ritorna OK se ha successo, altrimenti l'errore.
    private String logIn() throws IOException {
        
        if(psw.getText().isBlank() || nomeUsr.getText().isBlank()){
            logger.debug("Uno o più campi vuoti.");
            return "Uno o più campi vuoti.";
        }
        
        // Hashing della password
        logger.debug("Hashing della password...");
        String hashedPsw = null;
        try {
            hashedPsw = Hash.getHashedPsw(psw.getText());
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Non è stato possibile creare l'istanza per l'hasing della password.");
        }
        logger.debug("Hashing effettuato.");
        
        // Collegamento al server.
        logger.debug("Connessione al servizio...");
        URL url = new URL("http://localhost:8080/CookingWiki/Access/LogIn?nome=" + nomeUsr.getText().trim() + "&pass=" + hashedPsw);
        
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
        
        logger.debug("Risposta ricevuta: " + content);
        
        if(content.toString() == null)
            return "Errore nella connessione al db.";
        
        return content.toString();           
    
    }
    
    // Imposta notifica di successo,
    // dopo WAIT_MILLISEC millisecondi torna alla pagina di login.
    private void setSuccess() throws IOException {
        resultBox.setManaged(true);
        
        loginButton.setDisable(true);
        loginButton.setVisible(false);        
        loginButton.setManaged(false);
        
        closeN.setDisable(true);
        closeN.setVisible(false);
        
        Platform.runLater(
            () -> {
                titleResult.setText(titleSuccess);
                contentResult.setText(textSuccess);
            }
        );

        resultBox.getStylesheets().clear();
        resultBox.getStylesheets().add("/styles/successNotify.css");
        resultBox.setDisable(false);
        resultBox.setVisible(true);
        
        Task task = new Task<Void>(){
                        
                        @Override
                        public Void call() throws IOException{
                            login();
                            return null;
                        }
            
                    };
        
        new Thread(task).start(); 
    }
    
    // Imposta notifica di fail.
    private void setFail() throws IOException {
        
        abilitaInput();
        
        Platform.runLater(
            () -> {
                titleResult.setText(titleFail);
                contentResult.setText("Credenziali incorrette.");
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
        
        closeN.setDisable(true);

        resultBox.setDisable(true);
        resultBox.setVisible(false);
        resultBox.setManaged(false);
        
    }
    
    
    // Corpo del task per accedere alla home.
    // Prima di cambiare pagina, viene salvato il nome utente.
    private void login() throws IOException {
        
        try {
            Thread.currentThread().sleep(WAIT_MILLISEC);
        } catch (InterruptedException ex) {
            System.out.println("Interrupted Exception nell'attesa della redirezione\n al login da recupero password:\n" + ex.getMessage());
        }
        
        // Salvataggio nome utente sul Singelton Pattern SessionData.
        SessionData sd = SessionData.getInstance();
        sd.setUserName(nomeUsr.getText());
        
        CookingWiki.setRoot("home");
        
    }
    
    // Apre le impostazioni del database
    @FXML
    private void openSettings() throws IOException {
        
        closeN1.setDisable(false);
        creaButton.setDisable(false);
        popolaButton.setDisable(false);

        resultBox1.setDisable(false);
        resultBox1.setVisible(true);
        
    }
    
    // Chiude le impostazioni del database
    @FXML
    private void closeSettings() throws IOException {
        
        closeN1.setDisable(true);
        creaButton.setDisable(true);
        popolaButton.setDisable(true);

        resultBox1.setDisable(true);
        resultBox1.setVisible(false);
        
    }
    
    // Crea il database
    @FXML
    private void creaDB() throws IOException {
        
        disabilitaInput();
        
        Task task = new Task<Void>(){
                        
                        @Override
                        public Void call() throws IOException{
                            tryCreaTask();
                            return null;
                        }
            
                    };
        
        new Thread(task).start();
        
    }
    
    // Popola il database
    @FXML
    private void popolaDB() throws IOException {
        
        disabilitaInput();
        
        Task task = new Task<Void>(){
                        
                        @Override
                        public Void call() throws IOException{
                            tryPopolaTask();
                            return null;
                        }
            
                    };
        
        new Thread(task).start();
                
    }
    
    private void tryCreaTask() throws IOException {
        
        if(DBManager.inizializzaDB())
            Platform.runLater(() -> {contentResult1.setText("DB creato");});
        else
            Platform.runLater(() -> {contentResult1.setText("Errore nella creazione del DB");});
            
        abilitaInput();

    }
    
    private void tryPopolaTask() throws IOException {   
        
        if(DBManager.popolaDB())
            Platform.runLater(() -> {contentResult1.setText("DB popolato");});
        else
            Platform.runLater(() -> {contentResult1.setText("Errore nel popolamento del DB");});
            
        abilitaInput();
        
    }
    
    private void disabilitaInput() throws IOException{
        
        loginButton.setDisable(true);
        settingBox.setDisable(true);
        closeN1.setDisable(true);
        creaButton.setDisable(true);
        popolaButton.setDisable(true);
        loginButton.setDisable(true);
        regHyp.setDisable(true);
        pswHyp.setDisable(true);
        nomeUsr.setDisable(true);
        psw.setDisable(true);
        closeNotify();
        
    }
    
    private void abilitaInput(){
        
        resultBox.setManaged(true);
        
        loginButton.setDisable(false);
        settingBox.setDisable(false);
        closeN1.setDisable(false);
        creaButton.setDisable(false);
        popolaButton.setDisable(false);
        regHyp.setDisable(false);
        pswHyp.setDisable(false);
        nomeUsr.setDisable(false);
        psw.setDisable(false);
        
    }
}