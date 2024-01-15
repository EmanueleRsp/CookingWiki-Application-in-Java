/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.emacompany.CookingWikiServer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/CookingWiki/Search")
public class SearchController {
    
    private static final Logger logger = LogManager.getLogger(SearchController.class);
    
    
    // Recupera le ricette in ordine cronologico dalla più recente.
    @GetMapping(path = "/LastRecipes")
    public @ResponseBody
    String ricercaUltime(@RequestParam int dim, @RequestParam String lastName, @RequestParam String lastAutore){
        
        System.out.println("Richiesta ricerca ultime ricette..." + lastName + " : " + lastAutore);
        lastName = lastName.replaceAll("_", " ");
        List<PrevRec> ricette = new ArrayList<PrevRec>();
        int count = 0;
        boolean found = lastName.equals("null");
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT nome, autore, presentazione\n" +
                                           "from ricetta\n" +
                                           "order by data_creazione DESC;");
           ){

            while(!found && rs.next()){
                if(rs.getString("nome").equals(lastName) && rs.getString("autore").equals(lastAutore))
                    found = true;
            }
            
            while(rs.next() && count < dim){
                count ++;
                PrevRec pr = new PrevRec(rs.getString("nome"), rs.getString("autore"), rs.getString("presentazione"));
                ricette.add(pr);
            }
            
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db:" + ex.getMessage();
            System.out.println(err);
        }
        
        Gson gson = new Gson();
        System.out.println("Dati inviati.");
        String body = gson.toJson(ricette);
        Messaggio m = new Messaggio(Integer.toString(count), body);
        return gson.toJson(m);
    
    }
    
    
    // Recupera i risultati della ricerca per tag.
    @GetMapping(path = "/Tag")
    public @ResponseBody
    String ricercaTag(@RequestParam int dim, @RequestParam String lastName, @RequestParam String lastAutore, @RequestParam String tag){
        
        System.out.println("Richiesta ricerca per tag..." + lastName + " : " + lastAutore);
        lastName = lastName.replaceAll("_", " ");
        List<PrevRec> ricette = new ArrayList<PrevRec>();
        int count = 0;
        boolean found = lastName.equals("null");
        
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT DISTINCT nome, autore, presentazione\n" +
                                           "from ricetta natural join(\n" +
                                                "SELECT ricetta AS nome, autorericetta AS autore\n" +
                                                "FROM tag\n" +
                                                "WHERE tag = '" + tag + "') as T order by data_creazione DESC;");
           ){
            
            while(!found && rs.next()){
                if(rs.getString("nome").equals(lastName) && rs.getString("autore").equals(lastAutore))
                    found = true;
            }            
            while(rs.next() && count < dim){
                count ++;
                PrevRec pr = new PrevRec(rs.getString("nome"), rs.getString("autore"), rs.getString("presentazione"));
                ricette.add(pr);
            }
            
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db:" + ex.getMessage();
            System.out.println(err);
        }
        
        Gson gson = new Gson();
        System.out.println("Dati inviati.");
        String body = gson.toJson(ricette);
        Messaggio m = new Messaggio(Integer.toString(count), body);
        return gson.toJson(m);
    
    }
    
    
    // Recupera le ricette dell'utente.
    @GetMapping(path = "/Made")
    public @ResponseBody
    String ricercaProdotte(@RequestParam int dim, @RequestParam String lastName, @RequestParam String lastAutore, @RequestParam String name){
        
        System.out.println("Richiesta ricerca proprie ricette..." + lastName + " : " + lastAutore);
        lastName = lastName.replaceAll("_", " ");
        List<PrevRec> ricette = new ArrayList<PrevRec>();
        int count = 0;
        boolean found = lastName.equals("null");
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT nome, autore, presentazione\n" +
                                           "from ricetta\n" +
                                           "WHERE autore = '" + name + "' order by data_creazione DESC;");
           ){

            while(!found && rs.next()){
                if(rs.getString("nome").equals(lastName) && rs.getString("autore").equals(lastAutore))
                    found = true;
            }            
            while(rs.next() && count < dim){
                count ++;
                PrevRec pr = new PrevRec(rs.getString("nome"), rs.getString("autore"), rs.getString("presentazione"));
                ricette.add(pr);
            }
            
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db:" + ex.getMessage();
            System.out.println(err);
        }
        
        Gson gson = new Gson();
        System.out.println("Dati inviati.");
        String body = gson.toJson(ricette);
        Messaggio m = new Messaggio(Integer.toString(count), body);
        return gson.toJson(m);
    
    }
    
    
    // Recupera i risultati della ricerca per nome.
    @PostMapping(path = "/Name")
    public @ResponseBody String ricercaNome(@RequestParam String mser){
        
        System.out.println("Richiesta ricerca per nome...");
        
        Gson ggson = new Gson();
        JsonObject json = ggson.fromJson(mser, JsonObject.class);
        
        String name = json.get("name").getAsString();
        int lc = json.get("lastCheck").getAsInt();
        String lastName;
        String lastAutore;
        if(lc != 0){
            lastName = json.get("lastName").getAsString();
            lastAutore = json.get("lastAutore").getAsString();
        }else{
            lastName = "null";
            lastAutore = "null";
        }
        int dim = json.get("dim").getAsInt();
        name = name.replaceAll("_", " ");
        lastName = lastName.replaceAll("_", " ");
        List<PrevRec> ricette = new ArrayList<PrevRec>();
        int count = 0;
        boolean found = lastName.equals("null");
        
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT nome, autore, presentazione\n" +
                                           "from ricetta order by data_creazione DESC;");
           ){
            while(!found && rs.next()){
                if(rs.getString("nome").equals(lastName) && rs.getString("autore").equals(lastAutore))
                    found = true;
            }
            while(rs.next() && count < dim){

                if(rs.getString("nome").toLowerCase(Locale.ITALY).contains(name.toLowerCase(Locale.ITALY))){

                    count ++;
                    PrevRec pr = new PrevRec(rs.getString("nome"), rs.getString("autore"), rs.getString("presentazione"));
                    ricette.add(pr);
                }
            }
            
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db:" + ex.getMessage();
            System.out.println(err);
        }
        
        Gson gson = new Gson();
        System.out.println("Dati inviati.");
        String body = gson.toJson(ricette);
        Messaggio m = new Messaggio(Integer.toString(count), body);
        return gson.toJson(m);
    
    }
    
    
}
