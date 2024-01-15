package it.emacompany.cookingwiki;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBManager {
    
    private static final Logger logger = LogManager.getLogger(DBManager.class);
    
    static boolean inizializzato = false;
        
    public static boolean inizializzaDB(){
        
        logger.trace("Creazione database...");

        
        // Recupero il file e lo eseguo
        try (
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704?createDatabaseIfNotExist=true", "root", "root");
                BufferedReader reader = new BufferedReader(new FileReader(DBManager.class.getResource("/database/creazione.sql").getPath()));
            ){
            ScriptRunner sr = new ScriptRunner(con);
            sr.runScript(reader);

        }catch (SQLException | IOException ex) {
            ex.printStackTrace();
            return false;
        }
        
        logger.debug("Database creato!");

        inizializzato = true;
        
        return true;
       
    }
    
    // Svuota il database ricreandolo e lo popola
    public static boolean popolaDB() throws IOException{
        
        // Se ancora non è stato creato il db lo faccio
        if(!inizializzato)
            inizializzaDB();
        
        logger.debug("Popolamento database...");
        
        
        // Recupero il file
        String line;
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(DBManager.class.getResource("/database/popolamento.json").getPath()));
            
            while((line = reader.readLine()) != null)
                sb.append(line);
                    
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return false;
        }
        
        Gson gson = new Gson();
        JsonElement json = gson.fromJson(sb.toString(), JsonElement.class);
        JsonObject rootObj = json.getAsJsonObject();
        JsonArray utenti = rootObj.getAsJsonArray("utenti").getAsJsonArray();
        JsonArray ricette = rootObj.getAsJsonArray("ricette").getAsJsonArray();
        
        // Registro ciascun utente
        for(int i = 0; i < utenti.size(); i++){
            JsonObject u = utenti.get(i).getAsJsonObject();
            String nome = u.get("nome").toString().replaceAll("\"", "");
            String psw = null;
            String domanda = u.get("domanda").toString().replaceAll("\"", "");
            String risposta = u.get("risposta").toString().replaceAll("\"", "");
            try {
                psw = Hash.getHashedPsw(u.get("psw").toString().replaceAll("\"", ""));
            } catch (NoSuchAlgorithmException ex) {
                logger.error("Non è stato possibile creare l'istanza per l'hasing della password.");
            }

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
                logger.debug(err);
            }

            
        }
        
        logger.trace("Utenti inseriti.");

        // Registo ciascuna nuova ricetta con relativi ingredienti, passi e tags.
        for(int j = 0; j < ricette.size(); j++){
            JsonObject r = ricette.get(j).getAsJsonObject();
            
            String autore = r.get("autore").toString().replaceAll("\"", "");
            String titolo = r.get("titolo").toString().replaceAll("\"", "");
            String difficoltà = r.get("difficolta").toString().replaceAll("\"", "");
            String preparazione= r.get("preparazione").toString().replaceAll("\"", "");
            String cottura= r.get("cottura").toString().replaceAll("\"", "");
            String dosi= r.get("numeroDosi").toString().replaceAll("\"", "");
            String costo= r.get("costo").toString().replaceAll("\"", "");
            String presentazione= r.get("presentazione").toString().replaceAll("\"", "");
            String conservazione= r.get("conservazione").toString().replaceAll("\"", "");
            String suggerimenti= r.get("suggerimenti").toString().replaceAll("\"", "");
        
            // Occorre inserire anche la data di creazione della ricetta:
            Date date = new Date();
            SimpleDateFormat format = new           
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
            String currentDateTime = format.format(date);

            //Inserisco la nuova ricetta
            try(
                Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/emanuele_respino_616704","root", "root");
                PreparedStatement pstm = co.prepareStatement("INSERT INTO ricetta VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
               ){

                pstm.setString(1, titolo);
                pstm.setString(2, autore);
                pstm.setString(3, currentDateTime);
                pstm.setString(4, difficoltà);
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
            }
            // Inserisco gli ingredienti.
            JsonArray ingrList = r.get("ingredienti").getAsJsonArray();
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
            }
            // Inserisco gli steps della ricetta.
            JsonArray stepsList = r.get("passi").getAsJsonArray();
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
            }
            // Inserisco i tags.
            JsonArray tagList= r.get("tags").getAsJsonArray();
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
            }
            
            logger.trace("Ricette inserite.");
        }

        logger.debug("Database popolato!");
        return true;
    }
}