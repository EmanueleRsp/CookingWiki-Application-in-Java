
package it.emacompany.CookingWikiServer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/CookingWiki/Recipes")
public class RecipeController {
    
    private static final Logger logger = LogManager.getLogger(RecipeController.class);
    
    
    // Gestisce l'upload delle ricette.
    @PostMapping(path = "/Upload")
    public @ResponseBody String upload(@RequestParam String serInfo){
        
        System.out.println("Richiesta upload ricetta...");
        
        // Recupero nome, domanda e risposta di sicurezza.
        Gson gson = new Gson();
        JsonElement json = gson.fromJson(serInfo, JsonElement.class);
        JsonObject rootObj = json.getAsJsonObject();
        String autore = rootObj.get("autore").toString().replaceAll("\"", "");
        String titolo = rootObj.get("titolo").toString().replaceAll("\"", "");
        
        // Controllo se le credenziali di sicurezza combaciano.
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM ricetta WHERE nome = '" + titolo + "' AND autore = '" + autore + "';");
           ){
            
            if(rs.next()){
                String err = "Una ricetta con questo titolo è già stata inserita dall'utente.";
                System.out.println(err);
                return err;
            }
            
            
        } catch (SQLException ex) {
            String err = "Errore nell'inserimento dei dati nel db";
            System.out.println(err + ": " + ex.getMessage());
            return err + ".";
        }
        
        // Recupero delle altre informazioni.
        String difficolta= rootObj.get("difficolta").toString().replaceAll("\"", "");
        String preparazione= rootObj.get("preparazione").toString().replaceAll("\"", "");
        String cottura= rootObj.get("cottura").toString().replaceAll("\"", "");
        String dosi= rootObj.get("numeroDosi").toString().replaceAll("\"", "");
        String costo= rootObj.get("costo").toString().replaceAll("\"", "");
        String presentazione= rootObj.get("presentazione").toString().replaceAll("\"", "");
        String conservazione= rootObj.get("conservazione").toString().replaceAll("\"", "");
        String suggerimenti= rootObj.get("suggerimenti").toString().replaceAll("\"", "");
        
        // Occorre inserire anche la data di creazione della ricetta:
        Date date = new Date();
        SimpleDateFormat format = new           
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
        String currentDateTime = format.format(date);

        // Inserisco la nuova ricetta.
        System.out.println("Inserimento ricetta...");
        
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            PreparedStatement pstm = co.prepareStatement("INSERT INTO ricetta VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
           ){
            
            pstm.setString(1, titolo);
            pstm.setString(2, autore);
            pstm.setString(3, currentDateTime);
            pstm.setString(4, difficolta);
            pstm.setString(5, preparazione);
            pstm.setString(6, cottura);
            pstm.setString(7, dosi);
            pstm.setString(8, costo);
            pstm.setString(9, presentazione);
            pstm.setString(10, conservazione);
            pstm.setString(11, suggerimenti);
            
            pstm.executeUpdate();
            
        } catch (SQLException ex) {
            String err = "Errore nell'inserimento dei dati nel db: " + ex.getMessage();
            System.out.println(err);
            return("Errore nell'inserimento dei dati nel db (caratteri non consentiti o valori non leciti).");
        }
        
        System.out.println("Ricetta inserita!");

        
        // Inserisco gli ingredienti.
        JsonArray ingrList = rootObj.get("ingredienti").getAsJsonArray();
        System.out.println("Inserimento ingredienti...");
        
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
           ){
            
            for(int i = 0; i < ingrList.size(); i++){
                PreparedStatement pstm = co.prepareStatement("INSERT INTO ingrediente VALUES (?, ?, ?, ?);");
                
                JsonObject ingr = ingrList.get(i).getAsJsonObject();
                
                pstm.setString(1, ingr.get("nome").toString().replaceAll("\"", ""));
                pstm.setString(2, titolo);
                pstm.setString(3, autore);
                pstm.setString(4, ingr.get("quantita").toString().replaceAll("\"", ""));

                pstm.executeUpdate();
            }
            
        } catch (SQLException ex) {
            String err = "Errore nell'inserimento dei dati nel db: " + ex.getMessage();
            System.out.println(err);
            remove(titolo, autore);
            return("Errore nell'inserimento dei dati nel db (caratteri non consentiti o valori non leciti).");
        }
        
        System.out.println("Ingredienti inseriti!");

        
        // Inserisco gli steps della ricetta.
        JsonArray stepsList = rootObj.get("passi").getAsJsonArray();
        System.out.println("Inserimento passi della ricetta...");
        
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
           ){
            
            for(int i = 0; i < stepsList.size(); i++){
                PreparedStatement pstm = co.prepareStatement("INSERT INTO passaggio VALUES (?, ?, ?, ?);");
                
                JsonElement step = stepsList.get(i);
                
                pstm.setString(1, titolo);
                pstm.setString(2, autore);
                pstm.setString(3, Integer.toUnsignedString(i + 1));
                pstm.setString(4, step.toString().replaceAll("\"", ""));

                pstm.executeUpdate();
            }

        } catch (SQLException ex) {
            String err = "Errore nell'inserimento dei dati nel db: " + ex.getMessage();
            System.out.println(err);
            remove(titolo, autore);
            return("Errore nell'inserimento dei dati nel db (caratteri non consentiti o valori non leciti).");
        }
        
        System.out.println("Passi inseriti!");
        
        
        // Inserisco i tags.
        JsonArray tagList= rootObj.get("tags").getAsJsonArray();
        System.out.println("Inserimento tags della ricetta...");
        
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
           ){
            
            for(int i = 0; i < tagList.size(); i++){
                PreparedStatement pstm = co.prepareStatement("INSERT INTO tag VALUES (?, ?, ?);");
                
                JsonElement tag = tagList.get(i);
                
                pstm.setString(1, titolo);
                pstm.setString(2, autore);
                pstm.setString(3, tag.toString().replaceAll("\"", ""));

                pstm.executeUpdate();
            }

        } catch (SQLException ex) {
            String err = "Errore nell'inserimento dei dati nel db: " + ex.getMessage();
            System.out.println(err);
            remove(titolo, autore);
            return("Errore nell'inserimento dei dati nel db (caratteri non consentiti o valori non leciti).");
        }        
        
        System.out.println("Tags inseriti!");


        return "OK";
        
    }
    
    
    private void remove(String titolo, String autore){
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
           ){
            
            co.createStatement().execute("DELETE FROM ricetta WHERE nome = '" + titolo +"' AND autore = '" + autore + "';");
            
        } catch (SQLException ex) {
            String err = "Errore nell'eliminazione dei dati dal db: " + ex.getMessage();
            System.out.println(err);
        }
    }
    
    
    // Invia una ricetta dato il nome come parametro.
    @PostMapping(path = "/Get")
    public @ResponseBody String recipe(@RequestParam String mser){
        
        Gson mson = new Gson();
        Messaggio ms = mson.fromJson(mser, Messaggio.class);
        String titolo = ms.header.replaceAll("'", "\\\\'");
        String autore = ms.body;
        
        System.out.println("Richiesta invio ricetta: " + titolo + " : " + autore);
        InfoRicetta ir = new InfoRicetta();
        
        // Dati principali della ricetta
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT *\n" +
                                           "from ricetta\n" +
                                           "WHERE nome = '" + titolo + "' AND autore = '" + autore + "';");
           ){
            
            if(rs.next()){
                ir.autore = rs.getString("autore");
                ir.titolo = rs.getString("nome");
                ir.presentazione = rs.getString("presentazione");
                ir.conservazione = rs.getString("conservazione");
                ir.difficolta = rs.getString("difficolta");
                ir.preparazione = rs.getString("preparazione");
                ir.cottura = rs.getString("cottura");
                ir.numeroDosi = rs.getString("dosi");
                ir.costo = rs.getString("costo");
                ir.suggerimenti = rs.getString("suggerimenti");
            }else{
                Gson gson = new Gson();
                return gson.toJson(new Messaggio("FAIL", ""));
            }
                
            
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db:" + ex.getMessage();
            System.out.println(err);
            Gson gson = new Gson();
            return gson.toJson(new Messaggio("FAIL", ""));
        }
        
        // Ingredienti
        List<Ingrediente> ingr = new ArrayList<Ingrediente>();
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT *\n" +
                                           "from ingrediente\n" +
                                           "WHERE ricetta = '" + titolo + "' AND autorericetta = '" + autore + "';");
           ){
                        
            while(rs.next()){
                ingr.add(new Ingrediente(rs.getString("ingrediente"), rs.getString("quantita")));
            }
                
            
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db:" + ex.getMessage();
            System.out.println(err);
            Gson gson = new Gson();
            return gson.toJson(new Messaggio("FAIL", ""));
        }
        
        // Passaggi
        List<String> steps = new ArrayList<String>();
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT *\n" +
                                           "from passaggio\n" +
                                           "WHERE ricetta = '" + titolo + "' AND autorericetta = '" + autore + "' order by numero;");
           ){
                        
            while(rs.next()){
                steps.add(rs.getString("testo"));
            }
                
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db:" + ex.getMessage();
            System.out.println(err);
            Gson gson = new Gson();
            return gson.toJson(new Messaggio("FAIL", ""));
        }
        
        // Tags
        List<String> tags = new ArrayList<String>();
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT *\n" +
                                           "from tag\n" +
                                           "WHERE ricetta = '" + titolo + "' AND autorericetta = '" + autore + "';");
           ){
                        
            while(rs.next()){
                tags.add(rs.getString("tag"));
            }
                
            
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db:" + ex.getMessage();
            System.out.println(err);
            Gson gson = new Gson();
            return gson.toJson(new Messaggio("FAIL", ""));
        }
        
        ir.ingredienti = ingr;
        ir.passi = steps;
        ir.tags = tags;
        
        Gson gson = new Gson();
        String body = gson.toJson(ir);
        Messaggio m = new Messaggio("OK", body);
        System.out.println("Ricetta inviata!");
        return gson.toJson(m); 
    }
    
    
    // Elimina la ricetta dal database.
    @PostMapping(path = "/Remove")
    public @ResponseBody String removeRecipe(@RequestParam String mser){
        
        Gson mson = new Gson();
        Messaggio ms = mson.fromJson(mser, Messaggio.class);
        String proprietario = ms.header.replaceAll("'", "\\\\'");
        String info = ms.body;
        
        Gson r = new Gson();
        InfoRicetta ir = r.fromJson(info, InfoRicetta.class);
        System.out.println(ir.autore + ir.titolo.replaceAll("'", "\\\\'"));
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            PreparedStatement pstm = co.prepareStatement("DELETE FROM ricetta WHERE nome = ? AND autore = ?;");
           ){
                pstm.setString(1, ir.titolo);
                pstm.setString(2, ir.autore);
                pstm.executeUpdate();
            
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db:" + ex.getMessage();
            System.out.println(err);
            Gson gson = new Gson();
            return gson.toJson(new Messaggio("FAIL", ""));
        }
        
        return (new Gson()).toJson(new Messaggio("OK", ""));
    }
    
    
}
