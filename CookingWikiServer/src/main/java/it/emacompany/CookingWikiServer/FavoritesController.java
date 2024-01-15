
package it.emacompany.CookingWikiServer;

import com.google.gson.Gson;
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
@RequestMapping(path="/CookingWiki/Favorites")
public class FavoritesController {
    
    private static final Logger logger = LogManager.getLogger(FavoritesController.class);
    
    // Recupera il ricettario dell'utente.
    @GetMapping(path = "/List")
    public @ResponseBody
    String ricercaPref(@RequestParam int dim, @RequestParam String lastName, @RequestParam String lastAutore, @RequestParam String name){
        
        System.out.println("Richiesta ricerca proprie ricette..." + lastName + " : " + lastAutore);
        lastName = lastName.replaceAll("_", " ");
        List<PrevRec> ricette = new ArrayList<PrevRec>();
        int count = 0;
        boolean found = lastName.equals("null");
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT nome, autore, presentazione\n" +
                                           "from ricetta natural join (select ricetta as nome, autorericetta as autore from ricettario where utente = '" + name + "') as T order by data_creazione DESC;");
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
    
    
    // Indica se la ricetta è nel ricettario dell'utente o meno.
    @PostMapping(path = "/Get")
    public @ResponseBody String recipePref(@RequestParam String mser){
        
        Gson mson = new Gson();
        Messaggio ms = mson.fromJson(mser, Messaggio.class);
        String proprietario = ms.header.replaceAll("\"", "");
        String info = ms.body;
        
        Gson r = new Gson();
        InfoRicetta ir = r.fromJson(info, InfoRicetta.class);
        ir.titolo = ir.titolo.replaceAll("\"", "").replaceAll("'", "\\\\'");
        ir.autore = ir.autore.replaceAll("\"", "").replaceAll("'", "\\\\'");
        System.out.println(ir.titolo);
        boolean found = false;
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT *\n" +
                                           "from ricettario\n" +
                                           "WHERE utente = '" + proprietario + "' AND autorericetta = '" + ir.autore + "' AND ricetta = '" + ir.titolo + "';");
           ){
            
            if(rs.next())
                found = true;
                
            
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db:" + ex.getMessage();
            System.out.println(err);
            Gson gson = new Gson();
            return gson.toJson(new Messaggio("FAIL", ""));
        }
        
        if(found)
            return (new Gson()).toJson(new Messaggio("OK", ""));
        return (new Gson()).toJson(new Messaggio("FAIL", ""));
    }
    
    
    // Aggiunge la ricetta ad un dato ricettario.
    @PostMapping(path = "/Add")
    public @ResponseBody String addPref(@RequestParam String mser){
        System.out.println(mser);
        Gson mson = new Gson();
        Messaggio ms = mson.fromJson(mser, Messaggio.class);
        String proprietario = ms.header;
        String info = ms.body;
        System.out.println(proprietario);
        Gson r = new Gson();
        InfoRicetta ir = r.fromJson(info, InfoRicetta.class);
        System.out.println(ir.autore + ir.titolo.replaceAll("'", "\\\\'"));
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            PreparedStatement pstm = co.prepareStatement("INSERT INTO ricettario VALUES(?, ?, ?);");
           ){
                pstm.setString(1, proprietario);
                pstm.setString(2, ir.titolo);
                pstm.setString(3, ir.autore);
                pstm.executeUpdate();
            
        } catch (SQLException ex) {
            String err = "Errore nel recupero dati da db:" + ex.getMessage();
            System.out.println(err);
            Gson gson = new Gson();
            return gson.toJson(new Messaggio("FAIL", ""));
        }
        
        return (new Gson()).toJson(new Messaggio("OK", ""));
    }
    
    
    // Elimina la ricetta da un dato ricettario.
    @PostMapping(path = "/Remove")
    public @ResponseBody String removePref(@RequestParam String mser){
        
        Gson mson = new Gson();
        Messaggio ms = mson.fromJson(mser, Messaggio.class);
        String proprietario = ms.header.replaceAll("'", "\\\\'");
        String info = ms.body;
        
        Gson r = new Gson();
        InfoRicetta ir = r.fromJson(info, InfoRicetta.class);
        System.out.println(ir.autore + ir.titolo.replaceAll("'", "\\\\'"));
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
            PreparedStatement pstm = co.prepareStatement("DELETE FROM ricettario WHERE utente = ? AND ricetta = ? AND autorericetta = ?;");
           ){
                pstm.setString(1, proprietario);
                pstm.setString(2, ir.titolo);
                pstm.setString(3, ir.autore);
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
