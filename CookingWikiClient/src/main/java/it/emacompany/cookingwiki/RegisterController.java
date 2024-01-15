package it.emacompany.cookingwiki;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegisterController implements Initializable{
    
    private static final Logger logger = LogManager.getLogger(RegisterController.class);
    private final int WAIT_MILLISEC = 1300;
    
    private final String titleSuccess = "Registrazione effettuata!";
    private final String textSuccess = "Sarai rediretto al login."; 

    private final String titleFail = "Attenzione!"; 
    
    private final String titleNotify = "Attendere prego"; 
    private final String textNotify = "Stai per essere rediretto alla pagina di login."; 
    
    @FXML VBox resultBox;
    @FXML HBox escBox;
    @FXML ImageView closeN;
    
    @FXML Label titleResult;
    @FXML Label contentResult;
    
    @FXML Button changeButton;
    @FXML Button returnButton;
    
    @FXML TextField nome;
    @FXML TextField psw;
    @FXML TextField pswConf;
    @FXML TextField risposta;
    @FXML ComboBox domanda;
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        resultBox.setManaged(false);
        escBox.setManaged(false);
        domanda.setItems(QuestList.getOptions());
    }    
    
    
    // Torna alla pagina di login
    @FXML
    private void backLoginPage() throws IOException {
        
        resultBox.setManaged(true);
        
        changeButton.setDisable(true);
        returnButton.setDisable(true);
        
        returnButton.setVisible(false);
        changeButton.setVisible(false);
        
        returnButton.setManaged(false);
        changeButton.setManaged(false);
        
        closeN.setDisable(true);
        closeN.setVisible(false);
        
        Platform.runLater(
            () -> {
                titleResult.setText(titleNotify);
                contentResult.setText(textNotify);
            }
        );
        
        resultBox.getStylesheets().clear();
        resultBox.getStylesheets().add("/styles/neutralNotify.css");
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
    
    
    // Corpo del task per tornare alla pagina di login.
    private void login() throws IOException {
        
        try {
            Thread.currentThread().sleep(WAIT_MILLISEC);
        } catch (InterruptedException ex) {
            System.out.println("Interrupted Exception nell'attesa della redirezione\n al login da recupero password:\n" + ex.getMessage());
        }
        
        CookingWiki.setRoot("login");
        
    }
    
    // Tenta di registrare il nuovo utente.
    @FXML
    private void trySignIn() throws IOException {
        
        // Disabilito gli input
        changeButton.setDisable(true);
        returnButton.setDisable(true);
        closeNotify();
        
        Task task = new Task<Void>(){
                        
                        @Override
                        public Void call() throws IOException{
                            trySignInTask();
                            return null;
                        }
            
                    };
        
        new Thread(task).start(); 
    }    
    
    
    // Corpo del task che verifica le info inserite tramite signIn().
    private void trySignInTask() throws IOException {
        
        String result = signIn();
        if(result.equals("OK"))
            setSuccess();
        else
            setFail(result);
        
    }
    
    
    // Si collega al server e prova a registrare il nuovo utente.
    // Non possibile se esiste già profilo con stesso nome utente:
    // Ritorna OK se ha successo, altrimenti l'errore.
    private String signIn() throws IOException {
        
        //Fase di controllo validità campi.
        if(nome.getText().isBlank() || !nome.getText().matches("[(\\w).]*")){
            String err = "Campo nome vuoto o contente caratteri vietati (consentiti: 0-9 a-z A-Z _ . ).";
            logger.debug(err);
            return err;
        }
        if(nome.getText().trim().length() > 21 || nome.getText().trim().length() < 2){
            String err = "Il nome utente deve essere compreso tra 3 e 20 caratteri.";
            logger.debug(err);
            return err;
        }
        
        if(psw.getText().isBlank() || !psw.getText().matches("[(\\w)\\.!?£\\$€%]*")){
            String err = "Campo password vuoto o contente caratteri vietati (consentiti: 0-9a-zA-Z_.!?£$%€).";
            logger.debug(err);
            return err;
        }
        
        if(psw.getText().length() < 8 || psw.getText().length() > 20 ){
            String err = "La password deve avere una lunghezza compresa tra 8 e 20 caratteri.";
            logger.debug(err);
            return err;
        }
        
        if(pswConf.getText().isBlank() || !pswConf.getText().equals(psw.getText())){
            logger.debug("Password non confermata.");
            return "Password non confermata.";
        }
        
        if(domanda.getSelectionModel().isEmpty()){
            logger.debug("Domanda di sicurezza non selezionata.");
            return "La domanda di sicurezza non è stata selezionata.";
        }
        
        if(risposta.getText().isBlank() || !risposta.getText().matches("[(\\w)(\\s)]*")){
            String err = "Risposta non inserita o contenente caratteri vietati (consentiti: 0-9 a-z A-Z _ e spazio).";
            logger.debug(err);
            return err;
        }
        
        // Hashing della password
        logger.debug("Hashing della password...");
        String hashedPsw = null;
        try {
            hashedPsw = Hash.getHashedPsw(psw.getText());
        } catch (NoSuchAlgorithmException ex) {
            String err = "Non è stato possibile creare l'istanza per l'hashing della password scelta.";
            logger.error(err);
            return err;
        }
        logger.debug("Hashing effettuato.");
        
        // Serializzazione dati.
        logger.debug("Serializzazione dei dati...");
      /*logger.debug("Nome: " + nome.getText());
        logger.debug("Psw: " + hashedPsw);
        logger.debug("Domanda: " + domanda.getSelectionModel().getSelectedItem().toString());
        logger.debug("Risposta: " + risposta.getText());*/
        
        InfoUtente iu = new InfoUtente(nome.getText().trim(), hashedPsw, domanda.getSelectionModel().getSelectedItem().toString(), risposta.getText().trim());
        Gson gson = new Gson();
        String serInfo = gson.toJson(iu);
        logger.debug("Dati serializzati.");

        // Collegamento al server.
        logger.debug("Connessione al servizio...");
        URL url = new URL("http://localhost:8080/CookingWiki/Access/Register");
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        
        // Preparazione dati.
        String param = "serInfo=" + serInfo;
        byte[] postData = param.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        con.setInstanceFollowRedirects( false );
        con.setRequestMethod( "POST" );
        con.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty( "charset", "utf-8");
        con.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        con.setUseCaches( false );
        
        // Invio dati
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
        
        return content.toString().replace('Ã', 'à');
    }
    
    // Imposta notifica di successo,
    // dopo WAIT_MILLISEC millisecondi torna alla pagina di login.
    private void setSuccess() throws IOException {
        resultBox.setManaged(true);
        
        changeButton.setDisable(true);
        returnButton.setDisable(true);
        
        returnButton.setVisible(false);
        changeButton.setVisible(false);
        
        returnButton.setManaged(false);
        changeButton.setManaged(false);
        
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
    private void setFail(String whyFail) throws IOException {
        resultBox.setManaged(true);
        
        Platform.runLater(
            () -> {
                titleResult.setText(titleFail);
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
        
        changeButton.setDisable(false);
        returnButton.setDisable(false);
        
        closeN.setDisable(true);

        resultBox.setDisable(true);
        resultBox.setVisible(false);
        resultBox.setManaged(false);
        
    }
}
