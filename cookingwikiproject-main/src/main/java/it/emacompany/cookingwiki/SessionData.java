package it.emacompany.cookingwiki;

public class SessionData {
    
    // Nome utente loggato
    private String userName;
    // Parametro di ricerca
    private String ricerca;
    // Parametri della ricetta da visualizzare
    private String recipe;
    private String autore;
    // Tipologia di ricerca
    private SearchType typeS;
    // Parametri per la gestione dei risultati di ricerca
    private String lastName = null;
    private String lastAutore = null;
    private int lastCheck = 0;
    private int lastDim = 2;
    // Istanza unica dell'oggetto
    private static SessionData sd;
    
    private SessionData(){
        
    };

    public int getLastDim() {
        return lastDim;
    }

    public void setLastDim(int lastDim) {
        this.lastDim = lastDim;
    }
    
    public static SessionData getInstance(){
        if(sd == null)
            sd = new SessionData();
        return sd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRicerca() {
        return ricerca;
    }

    public void setRicerca(String ricerca) {
        this.ricerca = ricerca;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public SearchType getTypeS() {
        return typeS;
    }

    public void setTypeS(SearchType typeS) {
        this.typeS = typeS;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastAutore() {
        return lastAutore;
    }

    public void setLastAutore(String lastAutore) {
        this.lastAutore = lastAutore;
    }

    public int getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(int lastCheck) {
        this.lastCheck = lastCheck;
    }

    public static SessionData getSd() {
        return sd;
    }

    public static void setSd(SessionData sd) {
        SessionData.sd = sd;
    }
    
    
}
