package it.emacompany.CookingWikiServer;

import java.io.Serializable;

public class Messaggio implements Serializable{
    
    public String header;
    public String body;

    public Messaggio(String header, String body) {
        this.header = header;
        this.body = body;
    }
    
}
