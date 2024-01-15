package it.emacompany.CookingWikiServer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/CookingWiki/Access")
public class AccessController {
    
    private static final Logger logger = LogManager.getLogger(AccessController.class);
    
    
    // Gestisce la fase di login.
    @GetMapping(path = "/LogIn")
    public @ResponseBody String logIn(@RequestParam String nome, @RequestParam("pass") String password){
        
        System.out.println("Richiesta convalida credenziali in corso...");
        
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT password FROM utente WHERE user = '" + nome + "';");
           ){
            
            if(!rs.next()){
                System.out.println("Utente non trovato.");
                return "Utente non trovato.";
            }
            
            /*System.out.println("rs: " + rs.getString("password"));*/
            if(rs.getString("password").equals(password)){
                System.out.println("Login effettuato.");
                return "OK";
            }
            
            System.out.println("Password incorretta.");
            return("Password incorretta.");
                    
        } catch (SQLException e) {
            String err = "Errore nel recupero dati da db:" + e.getMessage();
            System.out.println(err);
            return("FAIL");
        }
               
    }
    
    
    // Gestisce la fase di registrazione di un nuovo utente.
    @PostMapping(path = "/Register")
    public @ResponseBody String register(@RequestParam String serInfo){
        
        System.out.println("Richiesta registrazione nuovo utente...");
        boolean valido = true;
        
        // Recupero il nome.
        Gson gson = new Gson();
        JsonElement json = gson.fromJson(serInfo, JsonElement.class);
        JsonObject rootObj = json.getAsJsonObject();
        String nome = rootObj.get("nome").toString().replaceAll("\"", "");
        
        // Controllo se esiste già un profilo con tale nome.
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM utente WHERE user = '" + nome + "';");
           ){
            
            if(rs.next())
                valido = false;
            
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db:" + ex.getMessage();
            System.out.println(err);
        }
        
        if(!valido){
            System.out.println("Nome utente già in uso.");
            return "Nome utente già in uso.";
        }
        
        // Recupero anche le altre info.
        String psw = rootObj.get("psw").toString().replaceAll("\"", "");
        String domanda = rootObj.get("domanda").toString().replaceAll("\"", "");
        String risposta= rootObj.get("risposta").toString().replaceAll("\"", "");

        // Inserisco il nuovo utente.
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            PreparedStatement pstm = co.prepareStatement("INSERT INTO utente VALUES (?, ?, ?, ?);");
           ){
            
            pstm.setString(1, nome);
            pstm.setString(2, psw);
            pstm.setString(3, domanda);
            pstm.setString(4, risposta);
            pstm.executeUpdate();
            
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db: " + ex.getMessage();
            System.out.println(err);
            return("Errore nel recupero dati dal server.");
        }
        
        System.out.println("Utente inserito!");
            return "OK";
        
    }
    
    
    // Gestisce il cambio password per un utente.
    @PostMapping(path = "/ChangePsw")
    public @ResponseBody String changePsw(@RequestParam String serInfo){
        
        System.out.println("Richiesta cambio credenziali...");
        
        // Recupero nome, domanda e risposta di sicurezza.
        Gson gson = new Gson();
        JsonElement json = gson.fromJson(serInfo, JsonElement.class);
        JsonObject rootObj = json.getAsJsonObject();
        String nome = rootObj.get("nome").toString().replaceAll("\"", "");
        String domanda = rootObj.get("domanda").toString().replaceAll("\"", "");
        String risposta= rootObj.get("risposta").toString().replaceAll("\"", "");
        
        // Controllo se le credenziali di sicurezza combaciano.
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM utente WHERE user = '" + nome + "';");
           ){
            
            if(!rs.next()){
                System.out.println("Nome utente non esistente.");
                return "Nome utente non esistente.";
            }
            if(!rs.getString("domanda").equals(domanda) || !rs.getString("risposta").equals(risposta) ){
                System.out.println("Credenziali di sicurezza errate.");
                return "Credenziali di sicurezza errate.";
            }
            
            
        } catch (SQLException ex) {
            String err = "Errore nell'inserimento dei dati nel db: " + ex.getMessage();
            System.out.println(err);
            return "Errore nell'inserimento dei dati nel db.";
        }
        
        // Recupero la nuova password da impostare.
        String psw = rootObj.get("psw").toString().replaceAll("\"", "");

        // Cambio la password.
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            PreparedStatement pstm = co.prepareStatement("UPDATE utente SET password = ? WHERE user = ?;");
           ){
            
            pstm.setString(1, psw);
            pstm.setString(2, nome);
            pstm.executeUpdate();
            
        } catch (SQLException ex) {
            String err = "Errore nell'inserimento dei dati neldb: " + ex.getMessage();
            System.out.println(err);
            return("Errore nell'inserimento dei dati nelserver.");
        }
        
        System.out.println("Password cambiata!");
        return "OK";
        
    }
       
    
    // Gestisce l'inizializzazione della home.
    @GetMapping(path = "/Home")
    public @ResponseBody String initHome(){
        
        System.out.println("Richiesta inizializzazione home...");
        
        List<PrevRec> ricette = new ArrayList<PrevRec>();
        int i = 0;
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT nome, autore, presentazione, difficolta, preparazione, cottura, dosi, costo\n" +
                                           "from ricetta\n" +
                                           "order by data_creazione DESC\n" +
                                           "LIMIT 3;");
           ){
            
            while(rs.next()){
                i++;
                PrevRec pr = new PrevRec(rs.getString("nome"), rs.getString("autore"), rs.getString("presentazione"), 
                                         rs.getString("difficolta"), rs.getString("preparazione"), rs.getString("cottura"), rs.getString("dosi"), rs.getString("costo"));
                ricette.add(pr);
            }
            
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db:" + ex.getMessage();
            System.out.println(err);
        }
        
        Gson gson = new Gson();
        Messaggio m = new Messaggio(Integer.toString(i), gson.toJson(ricette));
        Gson sson = new Gson();
        System.out.println("Dati inviati.");
        return sson.toJson(m);
        
    }
    
 
    
}
